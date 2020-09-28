package org.karnak.ui.data;

import org.karnak.data.gateway.*;

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
        for (KheopsAlbums kheopsAlbum : destination.getKheopsAlbums()) {
            Long destinationID = kheopsAlbum.getDestination() != null ? kheopsAlbum.getDestination().getId() : null;
            if (destinationID == null) {
                kheopsAlbum.setDestination(destination);
            }
            kheopsAlbumsPersistence.saveAndFlush(kheopsAlbum);
        }
    }
}
