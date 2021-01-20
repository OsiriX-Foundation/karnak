package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.ProfileElementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileElementRepo extends JpaRepository<ProfileElementEntity, Long> {

}
