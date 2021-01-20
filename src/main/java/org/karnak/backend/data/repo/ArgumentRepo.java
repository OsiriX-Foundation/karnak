package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.ArgumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArgumentRepo extends JpaRepository<ArgumentEntity, Long> {

}
