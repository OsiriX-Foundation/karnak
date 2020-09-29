package org.karnak.ui.kheops;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.karnak.api.KheopsApi;
import org.karnak.data.gateway.KheopsAlbums;
import org.karnak.kheops.SwitchingAlbum;

import java.util.List;

public class TextFieldsBindSwitchingAlbum {
    private KheopsApi kheopsApi;
    private Binder<KheopsAlbums> binder;

    private TextField textUrlAPI;
    private TextField textAuthorizationDestination;
    private TextField textAuthorizationSource;
    private TextField textCondition;

    public TextFieldsBindSwitchingAlbum() {
        kheopsApi = new KheopsApi();
        textUrlAPI = new TextField();
        textAuthorizationDestination = new TextField();
        textAuthorizationSource = new TextField();
        textCondition = new TextField();

        binder = setBinder();

    }

    private Binder<KheopsAlbums> setBinder() {
        Binder<KheopsAlbums> binder = new BeanValidationBinder<>(KheopsAlbums.class);
        binder.forField(textAuthorizationDestination)
                .withValidator(StringUtils::isNotBlank,"Token destination is mandatory")
                .withValidator(value -> {
                    if (textUrlAPI.getValue() != "") {
                        return validateToken(value, textUrlAPI.getValue(), SwitchingAlbum.MIN_SCOPE_DESTINATION);
                    }
                    return true;
                }, "Token can't be validate, minimum permissions: [write]")
                .bind(KheopsAlbums::getAuthorizationDestination, KheopsAlbums::setAuthorizationDestination);
        binder.forField(textAuthorizationSource)
                .withValidator(StringUtils::isNotBlank,"Token source is mandatory")
                .withValidator(value -> {
                    if (textUrlAPI.getValue() != "") {
                        return validateToken(value, textUrlAPI.getValue(), SwitchingAlbum.MIN_SCOPE_SOURCE);
                    }
                    return true;
                }, "Token can't be validate, minimum permissions: [read, send]")
                .bind(KheopsAlbums::getAuthorizationSource, KheopsAlbums::setAuthorizationSource);
        binder.forField(textUrlAPI)
                .withValidator(StringUtils::isNotBlank,"Url API is mandatory")
                .bind(KheopsAlbums::getUrlAPI, KheopsAlbums::setUrlAPI);
        binder.forField(textCondition)
                .bind(KheopsAlbums::getCondition, KheopsAlbums::setCondition);
        return binder;
    }

    private boolean validateToken(String token, String urlAPI, List<String> validMinScope) {
        try {
            JSONObject responseIntrospect = kheopsApi.tokenIntrospect(urlAPI, token, token);
            return SwitchingAlbum.validateIntrospectedToken(responseIntrospect, validMinScope);
        } catch (Exception e) {
            return false;
        }
    }

    public Binder<KheopsAlbums> getBinder() {
        return binder;
    }

    public TextField getTextUrlAPI() {
        return textUrlAPI;
    }

    public TextField getTextAuthorizationDestination() {
        return textAuthorizationDestination;
    }

    public TextField getTextAuthorizationSource() {
        return textAuthorizationSource;
    }

    public TextField getTextCondition() {
        return textCondition;
    }
}