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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class HazelcastConfig {

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
    config.getNetworkConfig().setPortAutoIncrement(true);
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    config
        .getNetworkConfig()
        .getJoin()
        .getEurekaConfig()
        .setEnabled(true)
        .setProperty("self-registration", "true")
        .setProperty("namespace", "hazelcast-karnak")
        .setProperty("use-metadata-for-host-and-port", "false")
    // TODO to test
            .setProperty("shouldUseDns", "false")
        .setProperty("name", "hazelcast-karnak")
        .setProperty("serviceUrl.default", "http://eureka:8761/eureka")
    ;
//
//    ClientConfig clientConfig = new ClientConfig();
//    var application = eurekaClient.getApplication("karnak");
//    var instances = application.getInstancesAsIsFromEureka();
//    for (InstanceInfo info : instances)
//    {
//      var metadata = info.getMetadata();
//      var address = metadata.get("hazelcast.host") + ":" + metadata.get("hazelcast.port");
//      clientConfig.getNetworkConfig().addAddress(address);
//    }


    // TODO to test
//        .setProperty("use-classpath-eureka-client-props", "false")
//        .setProperty("shouldUseDns", "false")
//        .setProperty("name", "hazelcast-karnak")
//        .setProperty("serviceUrl.default", "http://eureka:8761/eureka");

    return config;
//    return clientConfig;
  }


}
