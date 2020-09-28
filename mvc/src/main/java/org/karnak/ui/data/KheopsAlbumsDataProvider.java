package org.karnak.ui.data;

import org.karnak.data.gateway.*;

import java.util.List;

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
                Long destinationID = kheopsAlbum.getDestination() != null ? kheopsAlbum.getDestination().getId() : null;
                if (destinationID == null) {
                    kheopsAlbum.setDestination(destination);
                }
                kheopsAlbumsPersistence.saveAndFlush(kheopsAlbum);
            }
        }
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
