package com.application.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    @Primary
    public CacheManager imageCache() {
        CaffeineCacheManager caffeineCache = new CaffeineCacheManager();
        caffeineCache.setCaffeine(caffeine());
        return caffeineCache;
    }
    @Bean
    public CacheManager addressCache() {
        CaffeineCacheManager caffeineCache = new CaffeineCacheManager();
        caffeineCache.setCaffeine(caffeine());
        return caffeineCache;
    }
    @Bean
    public com.github.benmanes.caffeine.cache.Caffeine caffeine() {
//		com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(100, TimeUnit.SECONDS).initialCapacity(100).maximumSize(500);
        return com.github.benmanes.caffeine.cache.Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).initialCapacity(100).maximumSize(500);
    }
}
