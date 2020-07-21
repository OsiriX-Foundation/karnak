package org.karnak.ui.util;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;

public class ToggleButtonTheme extends HorizontalLayout {
    private ThemeList themeList;
    private ToggleButton toggleButton;
    private String THEME_COLOR_KEY = "theme-variant";

    public ToggleButtonTheme(){
        Icon moonIcon = new Icon(VaadinIcon.MOON_O);
        Icon sunIcon = new Icon(VaadinIcon.SUN_O);
        toggleButton = new ToggleButton();

        //read local storage theme
        UI.getCurrent().getPage().executeJs("return localStorage.getItem($0)", THEME_COLOR_KEY).then(String.class, string->{
            final String themeColor = string;
            if(string.equals(Lumo.DARK)){
                toggleButton.setValue(true);
                UI.getCurrent().getElement().setAttribute("theme", Lumo.DARK);
            }else if (string.equals(Lumo.LIGHT)){
                toggleButton.setValue(false);
                UI.getCurrent().getElement().setAttribute("theme", Lumo.LIGHT);
            }
        });

        toggleButton.addValueChangeListener(toggleButtonBooleanComponentValueChangeEvent -> {
            themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                UI.getCurrent().getElement().setAttribute("theme", Lumo.LIGHT);
                UI.getCurrent().getPage().executeJs("localStorage.setItem($0, $1)", THEME_COLOR_KEY,  Lumo.LIGHT);
            } else {
                UI.getCurrent().getElement().setAttribute("theme", Lumo.DARK);
                UI.getCurrent().getPage().executeJs("localStorage.setItem($0, $1)", THEME_COLOR_KEY,  Lumo.DARK);
            }
        });
        add(sunIcon, toggleButton, moonIcon);
    }

}
