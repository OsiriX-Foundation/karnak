package org.karnak.ui.kheops;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.KheopsAlbums;

import java.util.ArrayList;
import java.util.List;

public class SwitchingAlbumsView extends CustomField<List<KheopsAlbums>> {
    private NewSwitchingAlbum newSwitchingAlbum;
    private GridSwitchingAlbums gridSwitchingAlbums;
    private Binder<KheopsAlbums> newSwitchingAlbumBinder;
    private List<KheopsAlbums> kheopsAlbumsList;
    private VerticalLayout layout;

    public SwitchingAlbumsView() {
        newSwitchingAlbumBinder = new BeanValidationBinder<>(KheopsAlbums.class);
        newSwitchingAlbum = new NewSwitchingAlbum(newSwitchingAlbumBinder);
        gridSwitchingAlbums = new GridSwitchingAlbums();
        kheopsAlbumsList = new ArrayList<>();
        layout = new VerticalLayout();
        setEventButtonAdd();
    }

    public void addComponent(Boolean value) {
        if (value == true) {
            layout.add(newSwitchingAlbum, gridSwitchingAlbums);
        } else {
            layout.removeAll();
        }
    }

    public VerticalLayout getComponent() {
        return layout;
    }

    private void setEventButtonAdd() {
        KheopsAlbums newKheopsAlbums = new KheopsAlbums();
        newSwitchingAlbum.getButtonAdd().addClickListener(event -> {
            if (newSwitchingAlbumBinder.writeBeanIfValid(newKheopsAlbums)) {
                kheopsAlbumsList.add(newKheopsAlbums);
            }
        });
    }

    @Override
    protected List<KheopsAlbums> generateModelValue() {
        return kheopsAlbumsList;
    }

    @Override
    public List<KheopsAlbums> getValue() {
        List<KheopsAlbums> l = super.getValue();
        return l;
    }

    @Override
    protected void setPresentationValue(List<KheopsAlbums> kheopsAlbums) {
        kheopsAlbumsList = kheopsAlbums;
        gridSwitchingAlbums.initialize(kheopsAlbums);
    }
}
