package org.karnak.ui.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;

public class ProfileShowHide extends Div {
    private Component component;
    private Button btnShowHide = new Button();
    private Boolean show = true;
    private String textShow = "Show";
    private String textHide = "Hide";

    public ProfileShowHide(Component component, Boolean show) {
        this.component = component;
        this.show = show;
        setStyle();
    }

    public void setTextShow(String textShow) {
        this.textShow = textShow;
        setTextButtonShowHide();
    }

    public void setTextHide(String textHide) {
        this.textHide = textHide;
        setTextButtonShowHide();
    }

    private void setStyle() {
        getStyle().set("margin-top", "0px");
    }

    private void setTextButtonShowHide() {
        btnShowHide.setText(show ? textHide : textShow);
    }

    public void setView() {
        removeAll();
        component.setVisible(show);
        setTextButtonShowHide();
        btnShowHide.addClickListener(buttonClickEvent -> {
            show = show ? false : true;
            component.setVisible(show);
            setTextButtonShowHide();
        });
        add(component);
        add(btnShowHide);
    }

}
