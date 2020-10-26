package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.html.Div;

public class DesidentificationName extends Div {

    public void setShowValue(String text) {
        removeAll();
        add(text);
    }
}
