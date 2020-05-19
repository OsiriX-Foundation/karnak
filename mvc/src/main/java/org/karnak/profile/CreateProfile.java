package org.karnak.profile;

import org.dcm4che6.data.DicomElement;
import org.karnak.data.StreamRegistry;
import org.karnak.profile.action.Action;
import org.karnak.profile.profilebody.ProfileBody;
import org.karnak.profile.profilebody.ProfileChainBody;
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
