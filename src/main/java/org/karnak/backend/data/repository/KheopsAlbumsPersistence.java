package org.karnak.backend.data.repository;

import java.util.List;
import org.karnak.backend.data.entity.Destination;
import org.karnak.backend.data.entity.KheopsAlbums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KheopsAlbumsPersistence extends JpaRepository<KheopsAlbums, Long> {
    List<KheopsAlbums> findAllByDestination(Destination destination);
}
