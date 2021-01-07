package org.karnak.backend.data.repository;

import org.karnak.backend.data.entity.SOPClassUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SOPClassUIDPersistence extends JpaRepository<SOPClassUID, Long> {

    SOPClassUID getSOPClassUIDByName(String name);

    SOPClassUID getSOPClassUIDById(Long id);

    Boolean existsByCiodAndUidAndName(String ciod, String uid, String name);
}
