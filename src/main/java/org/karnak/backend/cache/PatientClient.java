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
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class PatientClient {

  private final Cache cache;
  private final RedisTemplate<String, PatientCache> redisTemplate;
  private static final String KEY_SEPARATOR = "::";
  private final String prefixKeySearchCache;
  private final String patternSearchAllKeysCache;

  public PatientClient(Cache cache, RedisTemplate<String, PatientCache> redisTemplate, String name) {
    this.cache = cache;
    this.redisTemplate = redisTemplate;
    this.prefixKeySearchCache = "%s%s".formatted(name, KEY_SEPARATOR);
    this.patternSearchAllKeysCache = "%s*".formatted(prefixKeySearchCache);
  }

  public PatientCache put(String key, PatientCache patient) {
    return (PatientCache) cache.putIfAbsent(key, patient);
  }

  public PatientCache get(String key) {
    ValueWrapper valueFromCache = cache.get(key);
    return valueFromCache != null ? (PatientCache) valueFromCache.get() : null;
  }

  public void remove(String key) {
    cache.evictIfPresent(key);
  }

  public Collection<PatientCache> getAll() {
    return Objects.requireNonNull(redisTemplate.keys(patternSearchAllKeysCache)).stream()
        .filter(Objects::nonNull)
        .filter(c -> c.length() > prefixKeySearchCache.length())
        .map(
            k -> {
              ValueWrapper keyValue =
                  cache.get(k.substring(prefixKeySearchCache.length()));
              return keyValue != null ? (PatientCache) keyValue.get() : null;
            })
        .collect(Collectors.toList());
  }

  public void removeAll() {
    Objects.requireNonNull(redisTemplate.keys(patternSearchAllKeysCache)).stream()
        .filter(Objects::nonNull)
        .filter(c -> c.length() > prefixKeySearchCache.length())
        .forEach(k -> remove(k.substring(prefixKeySearchCache.length())));
  }
}
