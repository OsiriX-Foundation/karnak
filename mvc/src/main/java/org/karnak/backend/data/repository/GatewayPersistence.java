package org.karnak.backend.data.repository;

import org.karnak.backend.data.entity.ForwardNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayPersistence extends JpaRepository<ForwardNode, Long> {
}
