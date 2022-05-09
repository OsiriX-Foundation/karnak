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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class PatientClient {

  private final String name;
  private final Cache cache;
  private final RedisTemplate redisTemplate;
  private final RedisCacheManager redisCacheManager;
  private static final String KEY_SEPARATOR = "::";
  private final String prefixKeySearchCache;
  private final String patternSearchAllKeysCache;

  public PatientClient(
      Cache cache,
      RedisTemplate redisTemplate,
      String name,
      RedisCacheManager
          redisCacheManager /*RedisCacheManager redisCacheManager*/ /*, int ttlSeconds*/) {
    this.name = name;
    this.cache = cache;
    this.redisTemplate = redisTemplate;
    this.redisCacheManager = redisCacheManager;
    this.prefixKeySearchCache = "%s%s".formatted(name, KEY_SEPARATOR);
    this.patternSearchAllKeysCache = "%s*".formatted(prefixKeySearchCache);

    // PseudonymPatient pseudonymPatient = new CachedPatient("test",        "test", "test",        "test",        "test",        1L);
//        String key = RandomStringUtils.randomAlphabetic(1);
//        cache.putIfAbsent(key, pseudonymPatient);
//    Set<String> keys = redisTemplate.keys("*");
    //    redisTemplate.opsForValue().multiGet(keys);
  }

  public PseudonymPatient put(String key, PseudonymPatient patient) {
    return (PseudonymPatient) cache.putIfAbsent(key, patient);
  }

  public PseudonymPatient get(String key) {
    ValueWrapper valueFromCache = cache.get(key);
    return valueFromCache != null ? (PseudonymPatient) valueFromCache.get() : null;
  }

  public void remove(String key) {
    cache.evictIfPresent(key);
  }

  public Collection<PseudonymPatient> getAll() {

//    return (Collection<PseudonymPatient>)
//        redisTemplate.keys(patternSearchAllKeysCache).stream()
//            .filter(Objects::nonNull)
//            .filter(c -> ((String)c).length() > prefixKeySearchCache.length())
//            .map(k -> {
//              ValueWrapper keyValue = cache.get(
//                  ((String) k).substring(prefixKeySearchCache.length()));
//              return keyValue != null  ? (PseudonymPatient) keyValue.get() : null;
//            })
//            .collect(Collectors.toList());

    List<CachedPatient> collect = (List<CachedPatient>) redisTemplate.keys(patternSearchAllKeysCache).stream()
        .filter(Objects::nonNull)
        .filter(c -> ((String) c).length() > prefixKeySearchCache.length())
        .map(k -> {
          ValueWrapper keyValue = cache.get(
              ((String) k).substring(prefixKeySearchCache.length()));
          return keyValue != null ? keyValue.get() : null;
        })
        .collect(Collectors.toList());

    return null;
  }
}
