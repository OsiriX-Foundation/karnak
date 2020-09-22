package org.karnak.ui.kheops;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.ui.util.UIS;

import java.util.ArrayList;
import java.util.List;

public class SwitchingAlbumsView extends CustomField<List<KheopsAlbums>> {
    private NewSwitchingAlbum newSwitchingAlbum;
    private GridSwitchingAlbums gridSwitchingAlbums;
    private Binder<KheopsAlbums> newSwitchingAlbumBinder;
    private List<KheopsAlbums> kheopsAlbumsList;
    private Checkbox checkboxSwitchingAlbums;
    private VerticalLayout layout;

    public SwitchingAlbumsView() {
        newSwitchingAlbum = new NewSwitchingAlbum();
        newSwitchingAlbumBinder = newSwitchingAlbum.getBinder();
        gridSwitchingAlbums = new GridSwitchingAlbums();
        kheopsAlbumsList = new ArrayList<>();
        layout = new VerticalLayout();
        checkboxSwitchingAlbums = new Checkbox("Swithing in different KHEOPS albums");
        add(UIS.setWidthFull(checkboxSwitchingAlbums), layout);
        setEventCheckBox();
        setEventButtonAdd();
    }

    private void setEventCheckBox() {
        checkboxSwitchingAlbums.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                addComponent(event.getValue());
            }
        });
    }

    private void setCheckboxSwitchingAlbumsValue() {
        checkboxSwitchingAlbums.setValue(!kheopsAlbumsList.isEmpty());
    }

    public void addComponent(Boolean value) {
        if (value == true) {
            layout.add(newSwitchingAlbum, gridSwitchingAlbums);
        } else {
            newSwitchingAlbum.clear();
            layout.removeAll();
        }
    }

    public VerticalLayout getComponent() {
        return layout;
    }

    private void setEventButtonAdd() {
        newSwitchingAlbum.getButtonAdd().addClickListener(event -> {
            KheopsAlbums newKheopsAlbums = new KheopsAlbums();
            if (newSwitchingAlbumBinder.writeBeanIfValid(newKheopsAlbums)) {
                kheopsAlbumsList.add(newKheopsAlbums);
                gridSwitchingAlbums.setItems(kheopsAlbumsList);
            }
        });
    }

    @Override
    protected List<KheopsAlbums> generateModelValue() {
        return kheopsAlbumsList;
    }

    @Override
    public List<KheopsAlbums> getValue() {
        return checkboxSwitchingAlbums.getValue() ? super.getValue() : null;
    }

    @Override
    protected void setPresentationValue(List<KheopsAlbums> kheopsAlbums) {
        kheopsAlbumsList = kheopsAlbums;
        if (kheopsAlbums != null) {
            setCheckboxSwitchingAlbumsValue();
            gridSwitchingAlbums.initialize(kheopsAlbums);
        }
    }
}
