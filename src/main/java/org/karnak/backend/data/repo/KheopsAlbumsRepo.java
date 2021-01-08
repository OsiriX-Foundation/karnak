package org.karnak.backend.data.repo;

import java.util.List;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KheopsAlbumsRepo extends JpaRepository<KheopsAlbumsEntity, Long> {

    List<KheopsAlbumsEntity> findAllByDestinationEntity(DestinationEntity destinationEntity);
}
