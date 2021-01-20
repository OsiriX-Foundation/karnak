package org.karnak.backend.data.repo;

import org.karnak.backend.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepo extends JpaRepository<TagEntity, Long> {

}
