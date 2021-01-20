package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.MaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaskRepo extends JpaRepository<MaskEntity, Long> {

}

