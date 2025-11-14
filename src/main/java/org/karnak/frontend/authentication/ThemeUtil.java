package org.karnak.frontend.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.theme.lumo.Lumo;

public final class ThemeUtil {

	private static final String THEME_COLOR_KEY = "theme-variant";

	private ThemeUtil() {
		// Utility class
	}

	public static void initializeTheme() {
		UI.getCurrent()
			.getPage()
			.executeJs("return localStorage.getItem($0)", THEME_COLOR_KEY)
			.then(String.class, ThemeUtil::applyTheme);
	}

	private static void applyTheme(String themeColor) {
		if (isValidTheme(themeColor)) {
			UI.getCurrent().getElement().setAttribute("theme", themeColor);
			UI.getCurrent().getPage().executeJs("localStorage.setItem($0, $1)", THEME_COLOR_KEY, themeColor);
		}
	}

	private static boolean isValidTheme(String theme) {
		return Lumo.DARK.equals(theme) || Lumo.LIGHT.equals(theme);
	}

}
