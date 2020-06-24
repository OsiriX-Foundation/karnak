package org.karnak.standard.dicominnolitics;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class StandardSOPS {
    private static final String sopsFileName = "sops.json";
    private static jsonSOP[] sops;

    public StandardSOPS() {
        URL url = this.getClass().getResource(sopsFileName);
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            sops = gson.fromJson(reader, jsonSOP[].class);
        } catch( Exception e) {
            throw new JsonParseException("Cannot parse json SOPS correctly", e);
        }
    }

    public static jsonSOP[] getSOPS() {
        return sops;
    }
}
