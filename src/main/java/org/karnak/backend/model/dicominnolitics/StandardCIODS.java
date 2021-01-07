package org.karnak.backend.model.dicominnolitics;

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
        ciods = read(url);
    }

    public jsonCIOD[] getCIODS() {
        return ciods;
    }

    public static jsonCIOD[] readJsonCIODS() {
        URL url = StandardCIODS.class.getResource(ciodsFileName);
        return read(url);
    }

    private static jsonCIOD[] read(URL url) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            return gson.fromJson(reader, jsonCIOD[].class);
        } catch( Exception e) {
            throw new JsonParseException(String.format("Cannot parse json %s correctly", ciodsFileName), e);
        }
    }
}
