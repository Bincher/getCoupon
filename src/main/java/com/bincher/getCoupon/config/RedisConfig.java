package com.bincher.getCoupon.config;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}") private String host;
    @Value("${spring.redis.port}") private int port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }

    @Bean
    public RBlockingQueue<String> couponQueue(RedissonClient redissonClient) {
        return redissonClient.getBlockingQueue("COUPON_QUEUE");
    }

    @Bean
    public RAtomicLong couponCounter(RedissonClient redissonClient) {
        return redissonClient.getAtomicLong("COUPON_COUNTER");
    }
}
