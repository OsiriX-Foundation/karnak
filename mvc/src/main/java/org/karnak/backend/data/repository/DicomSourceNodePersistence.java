package org.karnak.backend.data.repository;

import org.karnak.backend.data.entity.DicomSourceNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DicomSourceNodePersistence extends JpaRepository<DicomSourceNode, Long> {
}