package org.karnak.ui.data;

import org.karnak.data.gateway.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class KheopsAlbumsDataProvider {
    private KheopsAlbumsPersistence kheopsAlbumsPersistence;
    {
        kheopsAlbumsPersistence = GatewayConfiguration.getInstance().getKheopsAlbumsPersistence();
    }

    public void newSwitchingAlbum(KheopsAlbums kheopsAlbum) {
        Long destinationID = kheopsAlbum.getDestination() != null ? kheopsAlbum.getDestination().getId() : null;
        if (destinationID != null) {
            kheopsAlbumsPersistence.saveAndFlush(kheopsAlbum);
        }
    }

    public void updateSwitchingAlbumsFromDestination(Destination destination) {
        if (destination.getKheopsAlbums() != null) {
            for (KheopsAlbums kheopsAlbum : destination.getKheopsAlbums()) {
                setDestination(destination, kheopsAlbum);
            }
        }
        removeKheopsAlbums(destination);
    }

    private void removeKheopsAlbums(Destination destination) {
        List<KheopsAlbums> kheopsAlbumsListDatabase = new ArrayList<>();
        kheopsAlbumsPersistence.findAllByDestination(destination) //
                .forEach(kheopsAlbumsListDatabase::add);
        deleteDiffCurrentAndDatabase(destination, kheopsAlbumsListDatabase);
        deleteAll(destination, kheopsAlbumsListDatabase);
    }

    private void deleteDiffCurrentAndDatabase(Destination destination, List<KheopsAlbums> kheopsAlbumsListDatabase) {
        if (destination.getKheopsAlbums() != null && kheopsAlbumsListDatabase != null
                && destination.getKheopsAlbums().size() != kheopsAlbumsListDatabase.size()) {
            for (KheopsAlbums kheopsAlbumDatabase : kheopsAlbumsListDatabase) {
                Predicate<KheopsAlbums> idIsAlwaysPresent = kheopsAlbum -> kheopsAlbum.getId().equals(kheopsAlbumDatabase.getId());
                if (!destination.getKheopsAlbums().stream().anyMatch(idIsAlwaysPresent)) {
                    deleteSwitchingAlbums(kheopsAlbumDatabase);
                }
            }
        }
    }

    private void deleteAll(Destination destination, List<KheopsAlbums> kheopsAlbumsListDatabase) {
        if (destination.getKheopsAlbums() == null && kheopsAlbumsListDatabase != null) {
            deleteListSwitchingAlbums(kheopsAlbumsListDatabase);
        }
    }

    public void setDestination(Destination destination, KheopsAlbums kheopsAlbum) {
        Long destinationID = kheopsAlbum.getDestination() != null ? kheopsAlbum.getDestination().getId() : null;
        if (destinationID == null) {
            kheopsAlbum.setDestination(destination);
        }
        kheopsAlbumsPersistence.saveAndFlush(kheopsAlbum);
    }

    public void deleteSwitchingAlbums(KheopsAlbums kheopsAlbums) {
        kheopsAlbumsPersistence.deleteById(kheopsAlbums.getId());
        kheopsAlbumsPersistence.flush();
    }

    public void deleteListSwitchingAlbums(List<KheopsAlbums> kheopsAlbumsList) {
        if (kheopsAlbumsList != null) {
            kheopsAlbumsList.forEach(kheopsAlbums -> {
                kheopsAlbumsPersistence.deleteById(kheopsAlbums.getId());
            });
            kheopsAlbumsPersistence.flush();
        }
    }
}
