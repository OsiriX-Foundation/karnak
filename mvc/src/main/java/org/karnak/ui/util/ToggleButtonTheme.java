package org.karnak.ui.util;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.Lumo;

public class ToggleButtonTheme extends HorizontalLayout {
    private ToggleButton toggleButton;
    private String THEME_COLOR_KEY = "theme-variant";

    public ToggleButtonTheme(){
        Icon moonIcon = new Icon(VaadinIcon.MOON_O);
        Icon sunIcon = new Icon(VaadinIcon.SUN_O);
        toggleButton = new ToggleButton();

        //read local storage theme
        UI.getCurrent().getPage().executeJs("return localStorage.getItem($0)", THEME_COLOR_KEY).then(String.class, string->{
            final String themeColor = string;
            if (themeColor != null) {
                if (string.equals(Lumo.DARK)) {
                    toggleButton.setValue(true);
                } else if (string.equals(Lumo.LIGHT)) {
                    toggleButton.setValue(false);
                }
            }
        });

        toggleButton.addValueChangeListener(toggleButtonBooleanComponentValueChangeEvent -> {
            if (Boolean.TRUE.equals(toggleButtonBooleanComponentValueChangeEvent.getValue())) {
                UI.getCurrent().getElement().setAttribute("theme", Lumo.DARK);
                UI.getCurrent().getPage().executeJs("localStorage.setItem($0, $1)", THEME_COLOR_KEY,  Lumo.DARK);
            } else {
                UI.getCurrent().getElement().setAttribute("theme", Lumo.LIGHT);
                UI.getCurrent().getPage().executeJs("localStorage.setItem($0, $1)", THEME_COLOR_KEY,  Lumo.LIGHT);
            }
        });
        add(sunIcon, toggleButton, moonIcon);
    }

}
