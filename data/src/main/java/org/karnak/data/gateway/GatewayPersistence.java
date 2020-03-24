package org.karnak.data.gateway;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayPersistence extends JpaRepository<ForwardNode, Long> {
}
