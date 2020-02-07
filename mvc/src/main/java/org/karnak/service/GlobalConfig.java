package org.karnak.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.karnak.service.GatewayConfig.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public final class GlobalConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfig.class);

    private final GatewayConfig configOut;
    private final GatewayConfig configIn;


    public GlobalConfig(Environment environment) {
        try {
            configOut = new GatewayConfig(Stream.OUT, environment);
            configIn = new GatewayConfig(Stream.IN, environment);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load the base configuration", e);
        }
    }

    private Properties getPropertiesFromClasspath(String propFileName) throws IOException {
        // loading xmlProfileGen.properties from the classpath
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        Properties props = new Properties();
        props.load(inputStream);

        return props;
    }

    public final GatewayConfig getConfigOut() {
        return configOut;
    }

    public final GatewayConfig getConfigIn() {
        return configIn;
    }

}
