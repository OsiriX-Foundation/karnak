package org.karnak.data.gateway;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DicomSourceNodePersistence extends JpaRepository<DicomSourceNode, Long> {
}