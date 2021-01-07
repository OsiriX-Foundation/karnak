package org.karnak.frontend.kheops;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.KheopsAlbums;
import org.karnak.frontend.util.UIS;

public class SwitchingAlbumsView extends CustomField<List<KheopsAlbums>> {

    private final NewSwitchingAlbum newSwitchingAlbum;
    private final GridSwitchingAlbums gridSwitchingAlbums;
    private final Binder<KheopsAlbums> newSwitchingAlbumBinder;
    private final ListDataProvider<KheopsAlbums> dataProviderSwitchingAlbums;
    private final List<KheopsAlbums> kheopsAlbumsList;
    private final Checkbox checkboxSwitchingAlbums;
    private final VerticalLayout layout;

    public SwitchingAlbumsView() {
        gridSwitchingAlbums = new GridSwitchingAlbums();
        dataProviderSwitchingAlbums = (ListDataProvider<KheopsAlbums>) gridSwitchingAlbums
            .getDataProvider();
        newSwitchingAlbum = new NewSwitchingAlbum();
        newSwitchingAlbumBinder = newSwitchingAlbum.getBinder();
        kheopsAlbumsList = new ArrayList<>();
        layout = new VerticalLayout();
        checkboxSwitchingAlbums = new Checkbox("Switching in different KHEOPS albums");
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
        checkboxSwitchingAlbums.setValue(!dataProviderSwitchingAlbums.getItems().isEmpty());
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
                dataProviderSwitchingAlbums.getItems().add(newKheopsAlbums);
                dataProviderSwitchingAlbums.refreshAll();
                newSwitchingAlbum.clear();
            }
        });
    }

    @Override
    protected List<KheopsAlbums> generateModelValue() {
        return kheopsAlbumsList;
    }

    @Override
    public List<KheopsAlbums> getValue() {
        return checkboxSwitchingAlbums.getValue() ? new ArrayList<>(dataProviderSwitchingAlbums.getItems()) : null;
    }

    @Override
    public void setValue(List<KheopsAlbums> kheopsAlbums) {
        dataProviderSwitchingAlbums.getItems().removeAll(dataProviderSwitchingAlbums.getItems());
        gridSwitchingAlbums.clearEditorEditButtons();
        dataProviderSwitchingAlbums.getItems().addAll(kheopsAlbums != null ? kheopsAlbums : new ArrayList<>());
        dataProviderSwitchingAlbums.refreshAll();
        setCheckboxSwitchingAlbumsValue();
    }

    @Override
    protected void setPresentationValue(List<KheopsAlbums> kheopsAlbums) {
    }
}
