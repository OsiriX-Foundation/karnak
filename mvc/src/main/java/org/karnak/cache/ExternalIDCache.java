package org.karnak.cache;

import org.springframework.stereotype.Component;

@Component
public class ExternalIDCache extends PatientClient {
    private static final String name = "externalid";
    private static final int ttlSeconds = 60*60*24*7;

    public ExternalIDCache() {
        super(name, ttlSeconds);
    }
}
