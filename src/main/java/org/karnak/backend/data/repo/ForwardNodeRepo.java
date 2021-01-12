package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.ForwardNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForwardNodeRepo extends JpaRepository<ForwardNodeEntity, Long> {

}
