package org.karnak.ui.profile;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

@Tag("div")
public class TitleValue extends Component {
    private Element titleElement = new Element("h5");
    private Element valueElement = new Element("div");

    public TitleValue(String title, String value) {
        titleElement.setText(title);
        valueElement.setText(value).getStyle().set("color", "grey").set("padding-left", "10px");
        getElement().getStyle().set("margin-top", "0px");
        getElement().appendChild(titleElement, valueElement);
    }

    public String getTitle() {
        return  titleElement.getText();
    }

    public String getValue() {
        return valueElement.getText();
    }

    public void setTitle(String title) {
        titleElement.setText(title);
    }

    public void setValue(String value) {
        valueElement.setText(value);
    }
}
