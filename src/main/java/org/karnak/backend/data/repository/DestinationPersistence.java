package org.karnak.backend.data.repository;

import org.karnak.backend.data.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DestinationPersistence extends JpaRepository<Destination, Long> {
}
