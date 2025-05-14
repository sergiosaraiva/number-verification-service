package com.motive.numberverification.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Configuration for Redis caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Redis cache manager.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // Default TTL of 10 minutes
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
        
        // Configure different TTLs for different caches
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withCacheConfiguration("devicePhoneNumbers", 
                        defaultCacheConfig.entryTtl(Duration.ofMinutes(30)))  // 30 minutes TTL for device phone numbers
                .withCacheConfiguration("verificationResults", 
                        defaultCacheConfig.entryTtl(Duration.ofMinutes(5)))   // 5 minutes TTL for verification results
                .build();
    }
}
