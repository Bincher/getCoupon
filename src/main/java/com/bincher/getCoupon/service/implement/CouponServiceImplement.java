package com.bincher.getCoupon.service.implement;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;
import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.PostCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCouponResponseDto;
import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.entity.CouponEventEntity;
import com.bincher.getCoupon.entity.UserCouponId;
import com.bincher.getCoupon.entity.UserEntity;
import com.bincher.getCoupon.repository.CouponEventRepository;
import com.bincher.getCoupon.repository.CouponRepository;
import com.bincher.getCoupon.repository.UserRepository;
import com.bincher.getCoupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImplement implements CouponService{

    private final CouponRepository couponRepository;
    private final CouponEventRepository couponEventRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient; 

    private static final String COUPON_STOCK_KEY_PREFIX = "coupon_stock:";
    private static final String COUPON_USERS_KEY_PREFIX = "coupon_users:";

    @Override
    public ResponseEntity<? super GetCouponListResponseDto> getCouponList() {
        
        List<CouponEntity> couponEntities = new ArrayList<>();

        try{
            couponEntities =  couponRepository.findByOrderByIdDesc();
            

        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetCouponListResponseDto.success(couponEntities);
    }

    @Override
    public ResponseEntity<? super PostCouponResponseDto> postCoupon(PostCouponRequestDto dto) {
        try{


            CouponEntity couponEntity = new CouponEntity(dto);
            couponRepository.save(couponEntity);

        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return PostCouponResponseDto.success();
    }

    @Override
    public ResponseEntity<? super ReceiveCouponResponseDto> receiveCoupon(ReceiveCouponRequestDto dto, String userId) {
        
        UserEntity userEntity = null;
        CouponEntity couponEntity = null;

        Date now = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try{
            userEntity = userRepository.findById(userId);
            if(userEntity == null) return ReceiveCouponResponseDto.notExistedUser();

            int couponId = dto.getCouponId();
            couponEntity = couponRepository.findById(couponId);
            if(couponEntity == null) return ReceiveCouponResponseDto.notExistedCoupon();

            if(couponEntity.getAmount() < 1) return ReceiveCouponResponseDto.insufficientCoupon();

            Date endDate = simpleDateFormat.parse(couponEntity.getEndDate());
            if (endDate.before(now)) return ReceiveCouponResponseDto.expiredCoupon();

            UserCouponId userCouponId = new UserCouponId(userId, couponId);
            boolean isDuplicated = couponEventRepository.existsById(userCouponId);
            if(isDuplicated) return ReceiveCouponResponseDto.duplicatedCoupon();
            
            couponEntity.decreaseAmount();

            CouponEventEntity couponEventEntity = new CouponEventEntity(dto, userId);
            couponEventRepository.save(couponEventEntity);

        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ReceiveCouponResponseDto.success();
    }

    @Override
    public ResponseEntity<? super GetCouponResponseDto> getCoupon(String couponId) {

        CouponEntity couponEntity = null;
        try {
            int id = Integer.parseInt(couponId);

            couponEntity = couponRepository.findById(id);
            if(couponEntity == null) return GetCouponResponseDto.notExistedCoupon();

        } catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetCouponResponseDto.success(couponEntity);
    }

    @Override
    public ResponseEntity<? super ReceiveCouponResponseDto> receiveCouponWithQueue(ReceiveCouponRequestDto dto, String userId) {
        try {
            int couponId = dto.getCouponId();
            String queueKey = "coupon_queue:" + couponId;
            String resultKey = "coupon_result:" + couponId + ":" + userId;

            // 이미 대기열에 등록된 유저는 중복 등록 방지
            RBucket<String> resultBucket = redissonClient.getBucket(resultKey);
            if (resultBucket.isExists()) {
                String result = resultBucket.get();
                if ("SUCCESS".equals(result)) {
                    return ReceiveCouponResponseDto.duplicatedCoupon();
                } else if ("INSUFFICIENT".equals(result)) {
                    return ReceiveCouponResponseDto.insufficientCoupon();
                } else if ("QUEUED".equals(result)) {
                    return ReceiveCouponResponseDto.waitingQueue();
                }
            }

            // 대기열에 userId 등록 (이미 있으면 추가하지 않음)
            RList<String> queue = redissonClient.getList(queueKey);
            if (!queue.contains(userId)) {
                queue.add(userId);
            }

            // 대기 상태 기록
            resultBucket.set("QUEUED");

            // 즉시 발급 결과를 반환하지 않고, "대기열 등록 완료"만 응답
            return ReceiveCouponResponseDto.waitingQueue();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }


    @Override
    public ResponseEntity<? super ReceiveCouponResponseDto> receiveCouponWithRedisLua(ReceiveCouponRequestDto dto, String userId) {
        try {
            // 1. 유저, 쿠폰 유효성 체크 (DB)
            UserEntity userEntity = userRepository.findById(userId);
            if (userEntity == null) return ReceiveCouponResponseDto.notExistedUser();

            int couponId = dto.getCouponId();
            CouponEntity couponEntity = couponRepository.findById(couponId);
            if (couponEntity == null) return ReceiveCouponResponseDto.notExistedCoupon();

            Date now = Date.from(Instant.now());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = sdf.parse(couponEntity.getEndDate());
            if (endDate.before(now)) return ReceiveCouponResponseDto.expiredCoupon();

            // 2. Redis Lua Script로 원자적 발급 시도
            String stockKey = COUPON_STOCK_KEY_PREFIX + couponId;
            String usersKey = COUPON_USERS_KEY_PREFIX + couponId;

            RScript rScript = redissonClient.getScript();
            String luaScript =
                "if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then " +
                    "return -1 " +
                "end " +
                "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                "if stock == nil or stock <= 0 then " +
                    "return 0 " +
                "end " +
                "redis.call('DECR', KEYS[1]) " +
                "redis.call('SADD', KEYS[2], ARGV[1]) " +
                "return 1";

            List<Object> keys = Arrays.asList(stockKey, usersKey);
            Object result = rScript.eval(
                    RScript.Mode.READ_WRITE,
                    luaScript,
                    RScript.ReturnType.INTEGER,
                    keys,
                    userId
            );
            int luaResult = ((Number) result).intValue();

            if (luaResult == -1) {
                return ReceiveCouponResponseDto.duplicatedCoupon();
            } else if (luaResult == 0) {
                return ReceiveCouponResponseDto.insufficientCoupon();
            }

            // 3. DB에 발급 내역 저장 (이중화)
            CouponEventEntity couponEventEntity = new CouponEventEntity(dto, userId);
            couponEventRepository.save(couponEventEntity);

            return ReceiveCouponResponseDto.success();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    // 쿠폰 등록 시 Redis 재고 초기화 메소드 (관리자 쿠폰 등록 시 호출)
    public void setCouponStockToRedis(int couponId, int amount) {
        String stockKey = "coupon_stock:" + couponId;
        redissonClient.getBucket(stockKey).set(amount);
        // 유저 발급 기록도 초기화(테스트 반복시 필요)
        String usersKey = "coupon_users:" + couponId;
        redissonClient.getSet(usersKey).clear();
    }
}
