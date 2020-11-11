package org.karnak.cache;

import org.springframework.stereotype.Component;

@Component
public class MainzellisteCache extends PatientClient {
    private static final String name = "mainzelliste";
    private static final int ttlSeconds = 15*60;

    public MainzellisteCache() {
        super(name, ttlSeconds);
    }
}
