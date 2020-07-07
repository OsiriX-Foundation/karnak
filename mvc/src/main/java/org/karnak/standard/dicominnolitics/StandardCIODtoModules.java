package org.karnak.standard.dicominnolitics;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StandardCIODtoModules {
    private static final String ciodToModulesFileName = "ciodtomodules.json";
    private static jsonCIODtoModule[] ciodToModules;

    public StandardCIODtoModules() {
        URL url = this.getClass().getResource(ciodToModulesFileName);
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            ciodToModules = gson.fromJson(reader, jsonCIODtoModule[].class);
        } catch( Exception e) {
            throw new JsonParseException("Cannot parse json SOPS correctly", e);
        }
    }

    public static jsonCIODtoModule[] getCIODToModules() {
        return ciodToModules;
    }
}
