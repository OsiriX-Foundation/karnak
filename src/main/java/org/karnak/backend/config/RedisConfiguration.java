package org.karnak.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.karnak.backend.cache.PatientCache;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("patient:queue");
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, PatientCache> redisTemplate(
			RedisConnectionFactory connectionFactory) {

		ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());

		Jackson2JsonRedisSerializer<PatientCache> serializer = new Jackson2JsonRedisSerializer<>(
				PatientCache.class);
		serializer.setObjectMapper(objectMapper);

		RedisTemplate<String, PatientCache> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(serializer);

		return template;
	}

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
		return (builder) -> builder
				.withCacheConfiguration("externalId.cache",
						RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(7)))
				.withCacheConfiguration("mainzelliste.cache",
						RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)));
	}
}