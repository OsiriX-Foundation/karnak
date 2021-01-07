package org.karnak.frontend.image;

import com.vaadin.flow.component.html.Image;

public class LogoKarnak extends Image {

  private final String logoPath = "img/karnak.png";

    public LogoKarnak(String alt, String maxSize) {
        setSrc(logoPath);
        setAlt(alt);
        setMaxHeight(maxSize);
        setMaxWidth(maxSize);
    }
}
