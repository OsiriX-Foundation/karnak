package org.karnak.ui.util;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.theme.lumo.Lumo;

public class ToggleButtonTheme extends HorizontalLayout {
    private ThemeList themeList;
    private ToggleButton toggleButton;

    public ToggleButtonTheme(){
        Icon moonIcon = new Icon(VaadinIcon.MOON_O);
        Icon sunIcon = new Icon(VaadinIcon.SUN_O);
        toggleButton = new ToggleButton();
        toggleButton.addValueChangeListener(toggleButtonBooleanComponentValueChangeEvent -> {
            themeList = UI.getCurrent().getElement().getThemeList(); //

            if (themeList.contains(Lumo.DARK)) { //
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
        add(sunIcon, toggleButton, moonIcon);
    }

}
