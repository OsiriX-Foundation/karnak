package org.karnak.backend.cache;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("patientClient")
@Profile("jpackage")
@Primary
public class InMemoryExternalIDCache extends PatientClient {

	private static final String NAME = "externalId.cache";

	public InMemoryExternalIDCache() {
		super(new ConcurrentMapCache(NAME), null, NAME);
	}

}
