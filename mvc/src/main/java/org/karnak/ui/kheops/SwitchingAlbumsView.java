package org.karnak.ui.kheops;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.ArrayList;
import java.util.List;

public class SwitchingAlbumsView extends VerticalLayout {
    private NewSwitchingAlbum newSwitchingAlbum;
    private Binder<KheopsAlbums> newSwitchingAlbumBinder;
    private List<KheopsAlbums> kheopsAlbumsList;
    private Destination currentDestination;

    public SwitchingAlbumsView(Destination currentDestination) {
        this.currentDestination = currentDestination;

        newSwitchingAlbumBinder = new BeanValidationBinder<>(KheopsAlbums.class);
        newSwitchingAlbum = new NewSwitchingAlbum(newSwitchingAlbumBinder);
        kheopsAlbumsList = new ArrayList<>();
        setEventButtonAdd();
        add(newSwitchingAlbum);
    }

    private void setEventButtonAdd() {
        KheopsAlbums newKheopsAlbums = new KheopsAlbums();
        newSwitchingAlbum.getButtonAdd().addClickListener(event -> {
            if (newSwitchingAlbumBinder.writeBeanIfValid(newKheopsAlbums)) {
                kheopsAlbumsList.add(newKheopsAlbums);
            }
        });
    }
}
