package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SOPClassUIDRepo extends JpaRepository<SOPClassUIDEntity, Long> {

  SOPClassUIDEntity getSOPClassUIDByName(String name);

  SOPClassUIDEntity getSOPClassUIDById(Long id);

  Boolean existsByCiodAndUidAndName(String ciod, String uid, String name);
}
