package org.karnak.ui.kheops;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.karnak.data.gateway.KheopsAlbums;

public class NewSwitchingAlbum extends Div {
    private Binder<KheopsAlbums> binder;

    private Button buttonAdd;
    private TextField textAuthorizationDestination;
    private TextField textAuthorizationSource;
    private TextField textCondition;
    private TextField textUrlAPI;

    public NewSwitchingAlbum() {
        setWidthFull();

        TextFieldsBindSwitchingAlbum textFieldsBindSwitchingAlbum = new TextFieldsBindSwitchingAlbum();
        binder = textFieldsBindSwitchingAlbum.getBinder();
        buttonAdd = new Button("Add");
        textAuthorizationDestination = textFieldsBindSwitchingAlbum.getTextAuthorizationDestination();
        textAuthorizationSource = textFieldsBindSwitchingAlbum.getTextAuthorizationSource();
        textCondition = textFieldsBindSwitchingAlbum.getTextCondition();
        textUrlAPI = textFieldsBindSwitchingAlbum.getTextUrlAPI();

        setElements();

        add(textUrlAPI, textAuthorizationDestination, textAuthorizationSource, textCondition, buttonAdd);
        binder.bindInstanceFields(this);
    }

    private void setElements() {
        textUrlAPI.setWidth("20%");
        textUrlAPI.getStyle().set("padding-right", "10px");
        textUrlAPI.setPlaceholder("Url API");
        textAuthorizationDestination.setWidth("20%");
        textAuthorizationDestination.getStyle().set("padding-right", "10px");
        textAuthorizationDestination.setPlaceholder("Valid token of destination");
        textAuthorizationSource.setWidth("20%");
        textAuthorizationSource.getStyle().set("padding-right", "10px");
        textAuthorizationSource.setPlaceholder("Valid token of source");
        textCondition.setWidth("20%");
        textCondition.getStyle().set("padding-right", "10px");
        textCondition.setPlaceholder("Condition");
    }

    public Button getButtonAdd() {
        return buttonAdd;
    }

    public void clear() {
        textUrlAPI.clear();
        textAuthorizationDestination.clear();
        textAuthorizationSource.clear();
        textCondition.clear();
    }

    public Binder<KheopsAlbums> getBinder() {
        return binder;
    }
}
