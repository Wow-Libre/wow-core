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

        // Configuración específica para otros caches
        RedisCacheConfiguration userCacheConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(60)); // TTL para findByUserId

        RedisCacheConfiguration mailsConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(30));

        // Map de configuraciones para caches específicos
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("findByUserId", userCacheConfig);
        cacheConfigurations.put("mails", mailsConfig);

        // Construcción del RedisCacheManager con configuraciones específicas
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)  // Configuración por defecto
                .withInitialCacheConfigurations(cacheConfigurations)  // Configuraciones personalizadas
                .build();
    }
}
