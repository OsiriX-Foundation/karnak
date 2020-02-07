package org.karnak.data.output;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputRepository extends JpaRepository<ForwardNode, Long> {
}
