package org.karnak.ui.kheops;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    private Span textErrorConditionMsg;
    private TextFieldsBindSwitchingAlbum textFieldsBindSwitchingAlbum;



    public NewSwitchingAlbum() {
        setWidthFull();

        textFieldsBindSwitchingAlbum = new TextFieldsBindSwitchingAlbum();
        binder = textFieldsBindSwitchingAlbum.getBinder();
        buttonAdd = new Button("Add");
        textAuthorizationDestination = textFieldsBindSwitchingAlbum.getTextAuthorizationDestination();
        textAuthorizationSource = textFieldsBindSwitchingAlbum.getTextAuthorizationSource();
        textCondition = textFieldsBindSwitchingAlbum.getTextCondition();
        textErrorConditionMsg = textFieldsBindSwitchingAlbum.getTextErrorConditionMsg();
        textUrlAPI = textFieldsBindSwitchingAlbum.getTextUrlAPI();

        setElements();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(new HorizontalLayout(textUrlAPI, textAuthorizationDestination, textAuthorizationSource, textCondition, buttonAdd));
        verticalLayout.add(new HorizontalLayout(textErrorConditionMsg));
        add(verticalLayout);
        binder.bindInstanceFields(this);
    }

    private void setElements() {
        textErrorConditionMsg.getStyle()
                .set("margin-left","calc(var(--lumo-border-radius-m) / 4")
                .set("font-size","var(--lumo-font-size-xs)")
                .set("line-height","var(--lumo-line-height-xs)")
                .set("color","var(--lumo-error-text-color)")
                .set("will-change","max-height")
                .set("transition","0.4s max-height")
                .set("max-height","5em");
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
        textErrorConditionMsg.setText("");
    }

    public Binder<KheopsAlbums> getBinder() {
        return binder;
    }
}
