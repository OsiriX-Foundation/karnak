package org.karnak.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
@Profile("jpackage")
public class SimpleCacheConfiguration {

	@Bean
	@Primary
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}

}
