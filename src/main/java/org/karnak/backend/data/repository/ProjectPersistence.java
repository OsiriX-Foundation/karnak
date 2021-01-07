package org.karnak.backend.data.repository;

import org.karnak.backend.data.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectPersistence extends JpaRepository<Project, Long> {

}
