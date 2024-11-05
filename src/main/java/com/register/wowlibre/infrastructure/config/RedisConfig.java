package com.register.wowlibre.infrastructure.config;

import org.springframework.cache.*;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.*;

import java.time.*;
import java.util.*;

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Configuración general de cache
        RedisCacheConfiguration defaultCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(2)); // TTL predeterminado

        // Map de configuraciones para caches específicos
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("findByUserId", configurationTtl(Duration.ofSeconds(60)));
        cacheConfigurations.put("mails", configurationTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("emailCodeCache", configurationTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("serversFindById", configurationTtl(Duration.ofHours(1)));
        cacheConfigurations.put("recoveryPassword", configurationTtl(Duration.ofMinutes(2)));

        // Construcción del RedisCacheManager con configuraciones específicas
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private RedisCacheConfiguration configurationTtl(Duration duration) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(duration);
    }
}
