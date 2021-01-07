package org.karnak.backend.model.dicominnolitics;

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
        sops = read(url);
    }

    public jsonSOP[] getSOPS() {
        return sops;
    }

    public static jsonSOP[] readJsonSOPS() {
        URL url = StandardSOPS.class.getResource(sopsFileName);
        return read(url);
    }

    private static jsonSOP[] read(URL url) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            return gson.fromJson(reader, jsonSOP[].class);
        } catch( Exception e) {
            throw new JsonParseException(String.format("Cannot parse json %s correctly", sopsFileName), e);
        }
    }
}
