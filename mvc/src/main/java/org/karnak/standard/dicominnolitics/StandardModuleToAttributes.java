package org.karnak.standard.dicominnolitics;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StandardModuleToAttributes {
    private static final String moduleToAttributesFileName = "moduletoattributes.json";
    private static jsonModuleToAttribute[] moduleToAttributes;

    public StandardModuleToAttributes() {
        URL url = this.getClass().getResource(moduleToAttributesFileName);
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            moduleToAttributes = gson.fromJson(reader, jsonModuleToAttribute[].class);
        } catch( Exception e) {
            throw new JsonParseException("Cannot parse json SOPS correctly", e);
        }
    }

    public static jsonModuleToAttribute[] getModuleToAttributes() {
        return moduleToAttributes;
    }
}
