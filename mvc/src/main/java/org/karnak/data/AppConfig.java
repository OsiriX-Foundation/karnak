package org.karnak.data;

import org.karnak.data.profile.ProfilePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Autowired
    private ProfilePersistence profilePersistence;

    @Bean("ProfilePersistence")
    public ProfilePersistence getProfilePersistence() {
        return profilePersistence;
    }
}
