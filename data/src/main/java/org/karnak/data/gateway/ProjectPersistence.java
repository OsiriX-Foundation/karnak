package org.karnak.data.gateway;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectPersistence extends JpaRepository<Project, Long> {

}
