package org.karnak.backend.cache;

import org.springframework.stereotype.Component;

@Component
public class ExternalIDCache extends PatientClient {

  private static final String NAME = "externalid";
  private static final int TTL_SECONDS = 60 * 60 * 24 * 7;

  public ExternalIDCache() {
    super(NAME, TTL_SECONDS);
  }
}
