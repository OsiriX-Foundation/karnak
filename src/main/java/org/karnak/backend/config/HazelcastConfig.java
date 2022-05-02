/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import com.hazelcast.config.Config;
import com.hazelcast.eureka.one.EurekaOneDiscoveryStrategyFactory;
import com.netflix.discovery.EurekaClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableEurekaClient
public class HazelcastConfig {

  // TODO: hazelcast
  static Config configHazelcast;

  @Bean
  @Profile("!test")
  public Config hazelcastConfiguration(EurekaClient eurekaClient) {
    //		Config config = new Config();
    //		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    //		config.getNetworkConfig().getJoin().getEurekaConfig().setEnabled(true)
    //				.setProperty("self-registration", "true")
    //				.setProperty("namespace", "hazelcast-karnak");
    //		return config;
    EurekaOneDiscoveryStrategyFactory.setEurekaClient(eurekaClient);
    Config config = new Config();
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    config
        .getNetworkConfig()
        .getJoin()
        .getEurekaConfig()
        .setEnabled(true)
        .setProperty("self-registration", "true")
        .setProperty("namespace", "hazelcast-karnak")
        .setProperty("use-metadata-for-host-and-port", "true");

    // TODO: hazelcast
    configHazelcast = config;

    return config;
  }

  // TODO: hazelcast
  public static Config getConfigHazelcast() {
    return configHazelcast;
  }
}
