package org.karnak.ui.image;

import com.vaadin.flow.component.html.Image;

public class LogoKarnak extends Image {
    private String logoPath = "img/karnak.png";

    public LogoKarnak(String alt, String maxSize) {
        setSrc(logoPath);
        setAlt(alt);
        setMaxHeight(maxSize);
        setMaxWidth(maxSize);
    }
}
