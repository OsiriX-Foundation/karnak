package org.karnak.data;

import org.karnak.standard.ConfidentialityProfiles;
import org.karnak.profileschain.utils.HMAC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class AppConfig {

    private static AppConfig instance;
    private String environment;
    private String name;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    public static AppConfig getInstance() {
        return instance;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bean("ConfidentialityProfiles")
    public ConfidentialityProfiles getConfidentialityProfile() {
        return new ConfidentialityProfiles();
    }

    @Bean("HMAC")
    public HMAC getHmac(){
        return new HMAC();
    }
    /*
    @Bean("StandardDICOM")
    public StandardDICOM getStandardDICOM() {
        return new StandardDICOM();
    }
     */
}
