package com.bincher.getCoupon.config;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}") private String host;
    @Value("${spring.redis.port}") private int port;

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

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
