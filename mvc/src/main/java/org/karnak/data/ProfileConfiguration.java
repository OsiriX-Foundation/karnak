package org.karnak.data;

import javax.annotation.PostConstruct;

import org.karnak.data.gateway.ProfilePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileConfiguration {
    private static ProfileConfiguration instance;

    public static ProfileConfiguration getInstance() {
        return instance;
    }

    @Autowired
    private ProfilePersistence profilePersistence;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    @Bean("ProfilePersistence")
    public ProfilePersistence getProfilePersistence() {
        return profilePersistence;
    }
}


