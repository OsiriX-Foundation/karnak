package org.karnak.profileschain.utils;

import org.karnak.data.StreamRegistry;
import org.karnak.profileschain.profilebody.ProfileBody;
import org.karnak.profileschain.profilebody.ProfileChainBody;
import org.karnak.profileschain.profiles.ProfileChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


import java.io.InputStream;
import java.util.List;

public class CreateProfile {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamRegistry.class);
    private ProfileChain profile;

    public CreateProfile(String path) {
        try {
            final Yaml yaml = new Yaml(new Constructor(ProfileChainBody.class));;
            InputStream inputStream = this.getClass().getResourceAsStream(path);
            ProfileChainBody profileChainYml = yaml.load(inputStream);


            //create profilechain
            ProfileChain parent = null;
            final List<ProfileBody> profilesYml = profileChainYml.getProfiles();
            for (ProfileBody profileYml : profilesYml) {
                System.out.println(profileYml.getName());
                Object instanceProfileChain;
                try {
                    instanceProfileChain = Class.forName(profileYml.getClassname()).getConstructor(ProfileChain.class).newInstance(parent);
                    parent = (ProfileChain) instanceProfileChain;
                } catch (final Exception e) {
                    LOGGER.error("Cannot instantiate class {}", profileYml.getClassname(), e);
                }


            }
            this.profile = parent;
        }catch (final Exception e) {
            LOGGER.error("Cannot load yaml {}", path, e);
        }
    }

    public ProfileChain getProfile() {
        return profile;
    }
}
