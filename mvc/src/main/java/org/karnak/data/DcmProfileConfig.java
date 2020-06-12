package org.karnak.data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix="dcmprofile")
public class DcmProfileConfig {
    private static DcmProfileConfig instance;
    private String hmackey;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    public static DcmProfileConfig getInstance() {
        return instance;
    }

    public static void setInstance(DcmProfileConfig instance) {
        DcmProfileConfig.instance = instance;
    }

    public String getHmackey() {
        return hmackey;
    }

    public void setHmackey(String hmackey) {
        this.hmackey = hmackey;
    }
}
