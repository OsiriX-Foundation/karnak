package org.karnak.data;

import org.karnak.data.profile.ProfilePersistence;
import org.karnak.profilepipe.Profiles;
import org.karnak.profilepipe.profilebody.ProfilePipeBody;
import org.karnak.standard.ConfidentialityProfiles;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.ui.profile.ProfilePipeService;
import org.karnak.ui.profile.ProfilePipeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URL;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class AppConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
    private static AppConfig instance;
    private String environment;
    private String name;
    private String karnakadmin;
    private String karnakpassword;

    @Autowired
    private ProfilePersistence profilePersistence;

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

    public String getKarnakadmin() {
        return karnakadmin;
    }

    public void setKarnakadmin(String karnakadmin) {
        this.karnakadmin = karnakadmin;
    }

    public String getKarnakpassword() {
        return karnakpassword;
    }

    public void setKarnakpassword(String karnakpassword) {
        this.karnakpassword = karnakpassword;
    }

    public ProfilePersistence getProfilePersistence() {
        return profilePersistence;
    }

    @Bean("ConfidentialityProfiles")
    public ConfidentialityProfiles getConfidentialityProfile() {
        return new ConfidentialityProfiles();
    }

    @Bean("HMAC")
    public HMAC getHmac(){
        return new HMAC();
    }

    @PostConstruct
    public void setProfilesByDefault() {
        URL profileURL = Profiles.class.getResource("profileByDefault.yml");
        if(profilePersistence.existsByNameAndBydefault("Dicom Basic Profile", true)==false){
            try (InputStream inputStream = profileURL.openStream()) {
                final Yaml yaml = new Yaml(new Constructor(ProfilePipeBody.class));
                final ProfilePipeBody profilePipeYml = yaml.load(inputStream);
                final ProfilePipeService profilePipeService = new ProfilePipeServiceImpl();
                profilePipeService.saveProfilePipe(profilePipeYml, true);
            } catch (final Exception e) {
                LOGGER.error("Cannot persist default profile {}", profileURL, e);
            }
        }

    }
    /*
    @Bean("StandardDICOM")
    public StandardDICOM getStandardDICOM() {
        return new StandardDICOM();
    }
     */
}
