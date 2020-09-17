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

    public NewSwitchingAlbum(Binder<KheopsAlbums> binder) {
        setWidthFull();

        this.binder = binder;
        kheopsApi = new KheopsApi();
        buttonAdd = new Button("Add");
        textAuthorizationDestination = new TextField("Valid token of destination");
        textAuthorizationSource = new TextField("Valid token of source");
        textCondition = new TextField("Condtion");
        textUrlAPI = new TextField("Url API");

        setElements();
        setBinder();

        add(textUrlAPI, textAuthorizationDestination, textAuthorizationSource, textCondition, buttonAdd);
        binder.bindInstanceFields(this);
    }

    private void setElements() {
        textAuthorizationDestination.setWidth("20%");
        textAuthorizationSource.setWidth("20%");
        textCondition.setWidth("20%");
        textUrlAPI.setWidth("20%");
    }

    private void setBinder() {
        binder.forField(textAuthorizationDestination)
                .withValidator(StringUtils::isNotBlank,"Token destination is mandatory")
                .withValidator(value -> {
                    if (textUrlAPI.getValue() != "") {
                        return validateToken(value, textUrlAPI.getValue(), SwitchingAlbum.MIN_SCOPE_DESTINATION);
                    }
                    return true;
                }, "Token can't be validate")
                .bind(KheopsAlbums::getAuthorizationDestination, KheopsAlbums::setAuthorizationDestination);
        binder.forField(textAuthorizationSource)
                .withValidator(StringUtils::isNotBlank,"Token source is mandatory")
                .withValidator(value -> {
                    if (textUrlAPI.getValue() != "") {
                        return validateToken(value, textUrlAPI.getValue(), SwitchingAlbum.MIN_SCOPE_SOURCE);
                    }
                    return true;
                }, "Token can't be validate")
                .bind(KheopsAlbums::getAuthorizationSource, KheopsAlbums::setAuthorizationSource);
        binder.forField(textUrlAPI)
                .withValidator(StringUtils::isNotBlank,"Url API is mandatory")
                .bind(KheopsAlbums::getUrlAPI, KheopsAlbums::setUrlAPI);
        binder.forField(textCondition)
                .bind(KheopsAlbums::getCondition, KheopsAlbums::setCondition);
    }

    private boolean validateToken(String token, String urlAPI, List<String> validMinScope) {
        try {
            JSONObject responseIntrospect = kheopsApi.tokenIntrospect(urlAPI, token, token);
            return SwitchingAlbum.validateIntrospectedToken(responseIntrospect, validMinScope);
        } catch (Exception e) {
            return false;
        }
    }

    public Button getButtonAdd() {
        return buttonAdd;
    }
}
