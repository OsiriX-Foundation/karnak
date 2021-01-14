package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.DicomSourceNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DicomSourceNodeRepo extends JpaRepository<DicomSourceNodeEntity, Long> {

}
