package org.karnak.cache;

import org.springframework.stereotype.Component;

@Component
public class MainzellisteCache extends PatientClient {
    private static final String NAME = "mainzelliste";
    private static final int TTL_SECONDS = 15*60;

    public MainzellisteCache() {
        super(NAME, TTL_SECONDS);
    }
}
