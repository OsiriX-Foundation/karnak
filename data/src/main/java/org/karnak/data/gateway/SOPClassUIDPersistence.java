package org.karnak.data.gateway;


import org.karnak.data.profile.ProfileTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SOPClassUIDPersistence extends JpaRepository<SOPClassUID, Long> {
}
