package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalIDProviderRepo extends JpaRepository<ExternalIDProviderEntity, Long> {}
