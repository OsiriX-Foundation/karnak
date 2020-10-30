package org.karnak.data.gateway;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KheopsAlbumsPersistence extends JpaRepository<KheopsAlbums, Long> {
    List<KheopsAlbums> findAllByDestination(Destination destination);
}
