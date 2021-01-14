package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepo extends JpaRepository<ProfileEntity, Long> {

    Boolean existsByName(String name);

    Boolean existsByNameAndBydefault(String name, Boolean bydefault);
}
