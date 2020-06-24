package org.karnak.standard.dicominnolitics;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StandardCIODS {
    private static final String ciodsFileName = "ciods.json";
    private static jsonCIOD[] ciods;

    public StandardCIODS() {
        URL url = this.getClass().getResource(ciodsFileName);
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            ciods = gson.fromJson(reader, jsonCIOD[].class);
        } catch( Exception e) {
            throw new JsonParseException("Cannot parse json SOPS correctly", e);
        }
    }

    public static jsonCIOD[] getCIODS() {
        return ciods;
    }
}
