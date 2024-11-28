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
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("clustercfg.wowlibre.tkfyma.use2.cache.amazonaws.com");
        jedisConnectionFactory.setPort(6379); // Cambia el puerto si es diferente
        return jedisConnectionFactory;
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
        cacheConfigurations.put("verifyAccount", configurationTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("recoveryPassword", configurationTtl(Duration.ofMinutes(2)));
        cacheConfigurations.put("server-apikey", configurationTtl(Duration.ofHours(2)));


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
