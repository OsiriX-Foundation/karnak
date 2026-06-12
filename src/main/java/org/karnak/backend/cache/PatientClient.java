/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class PatientClient {

	private final Cache cache;

	private final RedisTemplate<String, Patient> redisTemplate;

	private static final String KEY_SEPARATOR = "::";

	private final String prefixKeySearchCache;

	private final String patternSearchAllKeysCache;

	public PatientClient(Cache cache, RedisTemplate<String, Patient> redisTemplate, String name) {
		this.cache = cache;
		this.redisTemplate = redisTemplate;
		this.prefixKeySearchCache = "%s%s".formatted(name, KEY_SEPARATOR);
		this.patternSearchAllKeysCache = "%s*".formatted(prefixKeySearchCache);
	}

	public Patient put(String key, Patient patient) {
		return unwrap(cache.putIfAbsent(key, patient));
	}

	public Patient get(String key) {
		return unwrap(cache.get(key));
	}

	public void remove(String key) {
		cache.evictIfPresent(key);
	}

	public Collection<Patient> getAll() {
		// In-memory cache (ConcurrentMapCache): iterate the native map keys
		if (cache instanceof ConcurrentMapCache concurrentMapCache) {
			return concurrentMapCache.getNativeCache()
				.keySet()
				.stream()
				.map(k -> get(k.toString()))
				.filter(Objects::nonNull)
				.toList();
		}
		// Redis cache: iterate the keys matching the cache prefix
		if (redisTemplate != null) {
			return redisCacheKeys().map(this::get).filter(Objects::nonNull).toList();
		}
		return Collections.emptyList();
	}

	public void removeAll() {
		// In-memory cache (ConcurrentMapCache): clear directly
		if (cache instanceof ConcurrentMapCache concurrentMapCache) {
			concurrentMapCache.clear();
			return;
		}
		// Redis cache: evict every key matching the cache prefix
		if (redisTemplate != null) {
			redisCacheKeys().forEach(this::remove);
		}
	}

	private static Patient unwrap(ValueWrapper wrapper) {
		return wrapper != null ? (Patient) wrapper.get() : null;
	}

	/**
	 * Streams the Redis cache keys matching the cache prefix, with the prefix stripped.
	 */
	private Stream<String> redisCacheKeys() {
		return Objects.requireNonNull(redisTemplate.keys(patternSearchAllKeysCache))
			.stream()
			.filter(Objects::nonNull)
			.filter(c -> c.length() > prefixKeySearchCache.length())
			.map(k -> k.substring(prefixKeySearchCache.length()));
	}

}
