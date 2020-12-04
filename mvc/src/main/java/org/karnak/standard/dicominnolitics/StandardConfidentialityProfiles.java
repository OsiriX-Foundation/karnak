package org.karnak.standard.dicominnolitics;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StandardConfidentialityProfiles {
    private static final String confidentialityProfilesFileName = "confidentiality_profile_attributes.json";
    private static jsonConfidentialityProfiles[] confidentialityProfiles;

    public StandardConfidentialityProfiles() {
        URL url = this.getClass().getResource(confidentialityProfilesFileName);
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            confidentialityProfiles = gson.fromJson(reader, jsonConfidentialityProfiles[].class);
        } catch( Exception e) {
            throw new JsonParseException(String.format("Cannot parse json %s correctly", confidentialityProfilesFileName), e);
        }
    }

    public static jsonConfidentialityProfiles[] getConfidentialityProfiles() {
        return confidentialityProfiles;
    }
}
