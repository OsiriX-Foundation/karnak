package org.karnak.frontend.kheops;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.dcm4che6.data.DicomObject;
import org.json.JSONObject;
import org.karnak.backend.api.KheopsApi;
import org.karnak.backend.data.entity.KheopsAlbumsEntity;
import org.karnak.backend.model.expression.ExprConditionKheops;
import org.karnak.backend.model.expression.ExpressionError;
import org.karnak.backend.model.expression.ExpressionResult;
import org.karnak.backend.service.kheops.SwitchingAlbumService;

public class TextFieldsBindSwitchingAlbum {

  private final KheopsApi kheopsApi;
  private final Binder<KheopsAlbumsEntity> binder;

  private final TextField textUrlAPI;
  private final TextField textAuthorizationDestination;
  private final TextField textAuthorizationSource;
  private final TextField textCondition;
  private ExpressionError expressionError;
  private final Span textErrorConditionMsg;

  public TextFieldsBindSwitchingAlbum() {
    kheopsApi = new KheopsApi();
    textUrlAPI = new TextField();
    textAuthorizationDestination = new TextField();
    textAuthorizationSource = new TextField();
    textCondition = new TextField();
    textErrorConditionMsg = new Span();
    expressionError = new ExpressionError(true, "");
    binder = buildBinder();

  }

  private Binder<KheopsAlbumsEntity> buildBinder() {
    Binder<KheopsAlbumsEntity> b = new BeanValidationBinder<>(KheopsAlbumsEntity.class);
    b.forField(textAuthorizationDestination)
        .withValidator(StringUtils::isNotBlank, "Token destination is mandatory")
        .withValidator(value -> {
          if (!textUrlAPI.getValue().isBlank()) {
            return validateToken(value, textUrlAPI.getValue(),
                SwitchingAlbumService.MIN_SCOPE_DESTINATION);
          }
          return true;
        }, "Token can't be validate, minimum permissions: [write]")
        .bind(KheopsAlbumsEntity::getAuthorizationDestination,
            KheopsAlbumsEntity::setAuthorizationDestination);
    b.forField(textAuthorizationSource)
        .withValidator(StringUtils::isNotBlank, "Token source is mandatory")
        .withValidator(value -> {
          if (!textUrlAPI.getValue().isBlank()) {
            return validateToken(value, textUrlAPI.getValue(),
                SwitchingAlbumService.MIN_SCOPE_SOURCE);
          }
          return true;
        }, "Token can't be validate, minimum permissions: [read, send]")
        .bind(KheopsAlbumsEntity::getAuthorizationSource,
            KheopsAlbumsEntity::setAuthorizationSource);
    b.forField(textUrlAPI)
        .withValidator(StringUtils::isNotBlank, "Url API is mandatory")
        .bind(KheopsAlbumsEntity::getUrlAPI, KheopsAlbumsEntity::setUrlAPI);
    b.forField(textCondition)
        .withValidator(value -> {
          if (!textCondition.getValue().equals("")) {
            expressionError = ExpressionResult.isValid(textCondition.getValue(),
                new ExprConditionKheops(DicomObject.newDicomObject()),
                Boolean.class);
            textErrorConditionMsg.setText(expressionError.getMsg());
            return expressionError.isValid();
          }
          textErrorConditionMsg.setText("");
          return true;
        }, "Condition is not valid")
        .bind(KheopsAlbumsEntity::getCondition, KheopsAlbumsEntity::setCondition);
        return b;
    }

    private boolean validateToken(String token, String urlAPI, List<String> validMinScope) {
        try {
            JSONObject responseIntrospect = kheopsApi.tokenIntrospect(urlAPI, token, token);
          return SwitchingAlbumService.validateIntrospectedToken(responseIntrospect, validMinScope);
        } catch (Exception e) {
            return false;
        }
    }

  public Binder<KheopsAlbumsEntity> getBinder() {
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

    public Span getTextErrorConditionMsg() {
        return textErrorConditionMsg;
    }
}
