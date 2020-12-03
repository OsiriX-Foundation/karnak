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
        moduleToAttributes = read(url);
    }

    public jsonModuleToAttribute[] getModuleToAttributes() {
        return moduleToAttributes;
    }

    public static jsonModuleToAttribute[] readJsonModuleToAttributes() {
        URL url = StandardModuleToAttributes.class.getResource(moduleToAttributesFileName);
        return read(url);
    }

    private static jsonModuleToAttribute[] read(URL url) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
             return gson.fromJson(reader, jsonModuleToAttribute[].class);
        } catch( Exception e) {
            throw new JsonParseException("Cannot parse json moduletoattributes.json correctly", e);
        }
    }
}
