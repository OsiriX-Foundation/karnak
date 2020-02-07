package org.karnak.data.input;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputRepository extends JpaRepository<SourceNode, Long> {
}
