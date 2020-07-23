package org.karnak.data.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilePersistence extends JpaRepository<Profile, Long> {
    Boolean existsByName(String name);

    Boolean existsByNameAndBydefault(String name, Boolean bydefault);
}
