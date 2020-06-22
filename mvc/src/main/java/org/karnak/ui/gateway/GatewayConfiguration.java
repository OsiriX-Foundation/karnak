package org.karnak.ui.gateway;

import javax.annotation.PostConstruct;

import org.karnak.data.gateway.GatewayPersistence;
import org.karnak.data.gateway.SOPClassUIDPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {
    private static GatewayConfiguration instance;

    public static GatewayConfiguration getInstance() {
        return instance;
    }

    @Autowired
    private GatewayPersistence gatewayPersistence;

    @Autowired
    private SOPClassUIDPersistence sopClassUIDPersistence;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    @Bean("GatewayPersistence")
    public GatewayPersistence getGatewayPersistence() {
        return gatewayPersistence;
    }

    @Bean("SOPClassUIDPersistence")
    public SOPClassUIDPersistence getSOPClassUIDPersistence() {
        return sopClassUIDPersistence;
    }
}
