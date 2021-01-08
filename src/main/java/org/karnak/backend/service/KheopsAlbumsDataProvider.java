package org.karnak.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.karnak.backend.config.GatewayConfig;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.karnak.backend.data.repo.KheopsAlbumsRepo;

public class KheopsAlbumsDataProvider {

    private final KheopsAlbumsRepo kheopsAlbumsRepo;

    {
        kheopsAlbumsRepo = GatewayConfig.getInstance().getKheopsAlbumsPersistence();
    }

    public void newSwitchingAlbum(KheopsAlbumsEntity kheopsAlbum) {
        Long destinationID =
            kheopsAlbum.getDestinationEntity() != null ? kheopsAlbum.getDestinationEntity().getId()
                : null;
        if (destinationID != null) {
            kheopsAlbumsRepo.saveAndFlush(kheopsAlbum);
        }
    }

    public void updateSwitchingAlbumsFromDestination(DestinationEntity destinationEntity) {
        if (destinationEntity.getKheopsAlbumEntities() != null) {
            for (KheopsAlbumsEntity kheopsAlbum : destinationEntity.getKheopsAlbumEntities()) {
                setDestination(destinationEntity, kheopsAlbum);
            }
        }
        removeKheopsAlbums(destinationEntity);
    }

    private void removeKheopsAlbums(DestinationEntity destinationEntity) {
        List<KheopsAlbumsEntity> kheopsAlbumsEntityListDatabase = new ArrayList<>();
        kheopsAlbumsRepo.findAllByDestinationEntity(destinationEntity) //
            .forEach(kheopsAlbumsEntityListDatabase::add);
        deleteDiffCurrentAndDatabase(destinationEntity, kheopsAlbumsEntityListDatabase);
        deleteAll(destinationEntity, kheopsAlbumsEntityListDatabase);
    }

    private void deleteDiffCurrentAndDatabase(DestinationEntity destinationEntity,
        List<KheopsAlbumsEntity> kheopsAlbumsEntityListDatabase) {
        if (destinationEntity.getKheopsAlbumEntities() != null
            && kheopsAlbumsEntityListDatabase != null
            && destinationEntity.getKheopsAlbumEntities().size() != kheopsAlbumsEntityListDatabase
            .size()) {
            for (KheopsAlbumsEntity kheopsAlbumDatabase : kheopsAlbumsEntityListDatabase) {
                Predicate<KheopsAlbumsEntity> idIsAlwaysPresent = kheopsAlbum -> kheopsAlbum.getId()
                    .equals(kheopsAlbumDatabase.getId());
                if (!destinationEntity.getKheopsAlbumEntities().stream()
                    .anyMatch(idIsAlwaysPresent)) {
                    deleteSwitchingAlbums(kheopsAlbumDatabase);
                }
            }
        }
    }

    private void deleteAll(DestinationEntity destinationEntity,
        List<KheopsAlbumsEntity> kheopsAlbumsEntityListDatabase) {
        if (destinationEntity.getKheopsAlbumEntities() == null
            && kheopsAlbumsEntityListDatabase != null) {
            deleteListSwitchingAlbums(kheopsAlbumsEntityListDatabase);
        }
    }

    public void setDestination(DestinationEntity destinationEntity,
        KheopsAlbumsEntity kheopsAlbum) {
        Long destinationID =
            kheopsAlbum.getDestinationEntity() != null ? kheopsAlbum.getDestinationEntity().getId()
                : null;
        if (destinationID == null) {
            kheopsAlbum.setDestinationEntity(destinationEntity);
        }
        kheopsAlbumsRepo.saveAndFlush(kheopsAlbum);
    }

    public void deleteSwitchingAlbums(KheopsAlbumsEntity kheopsAlbumsEntity) {
        kheopsAlbumsRepo.deleteById(kheopsAlbumsEntity.getId());
        kheopsAlbumsRepo.flush();
    }

    public void deleteListSwitchingAlbums(List<KheopsAlbumsEntity> kheopsAlbumsEntityList) {
        if (kheopsAlbumsEntityList != null) {
            kheopsAlbumsEntityList.forEach(kheopsAlbums -> {
                kheopsAlbumsRepo.deleteById(kheopsAlbums.getId());
            });
            kheopsAlbumsRepo.flush();
        }
    }
}
