package org.karnak.data.gateway;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SOPClassUIDPersistence extends JpaRepository<SOPClassUID, Long> {
    SOPClassUID getSOPClassUIDByName(String name);
}
