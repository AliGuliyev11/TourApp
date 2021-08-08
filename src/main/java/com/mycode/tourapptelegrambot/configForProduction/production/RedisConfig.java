package com.mycode.tourapptelegrambot.configForProduction.production;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote config class for cache
 */

@Configuration
@Profile("!dev")
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate() throws URISyntaxException {
        RedisTemplate<String, Object> objectRedisTemplate = new RedisTemplate<>();
        objectRedisTemplate.setConnectionFactory(jedisConnectionFactory());
        objectRedisTemplate.setKeySerializer(new StringRedisSerializer());
        objectRedisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
        objectRedisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        objectRedisTemplate.setEnableTransactionSupport(true);
        objectRedisTemplate.afterPropertiesSet();
        return objectRedisTemplate;
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() throws URISyntaxException {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        String envRedisUrl = System.getenv("REDIS_URL");
        URI redisUri = new URI(envRedisUrl);
        configuration.setPort(redisUri.getPort());
        configuration.setHostName(redisUri.getHost());
        configuration.setPassword(redisUri.getUserInfo().split(":", 2)[1]);
        return new LettuceConnectionFactory(configuration);
    }
}
