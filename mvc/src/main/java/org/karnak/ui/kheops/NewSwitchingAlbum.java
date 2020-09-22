package org.karnak.ui.kheops;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.karnak.api.KheopsApi;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.kheops.SwitchingAlbum;

import java.util.List;

public class NewSwitchingAlbum extends Div {
    private Binder<KheopsAlbums> binder;
    private KheopsApi kheopsApi;

    private Button buttonAdd;
    private TextField textAuthorizationDestination;
    private TextField textAuthorizationSource;
    private TextField textCondition;
    private TextField textUrlAPI;

    public NewSwitchingAlbum() {
        setWidthFull();

        TextFieldsBindSwitchingAlbum textFieldsBindSwitchingAlbum = new TextFieldsBindSwitchingAlbum();
        binder = textFieldsBindSwitchingAlbum.getBinder();
        kheopsApi = new KheopsApi();
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
        textAuthorizationDestination.setWidth("20%");
        textAuthorizationDestination.setPlaceholder("Valid token of destination");
        textAuthorizationSource.setWidth("20%");
        textAuthorizationSource.setPlaceholder("Valid token of source");
        textCondition.setWidth("20%");
        textCondition.setPlaceholder("Condition");
        textUrlAPI.setWidth("20%");
        textUrlAPI.setPlaceholder("Url API");
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
