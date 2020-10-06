package org.karnak.ui.forwardnode.extid;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

public class ADD_EXTIDView extends Div {
    private TextField delimiter;
    private TextField tag;
    private NumberField position;
    final String [] extidSentence = {"Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};

    public ADD_EXTIDView() {
        setElements();
        add(tag, delimiter, position);
    }

    public void setElements() {
        delimiter = new TextField("Delimiter");
        tag = new TextField("Tag");
        position = new NumberField("Position");
        position.setHasControls(true);
        position.setMin(0);
        position.setStep(1);
    }

    public void clear() {
        delimiter.clear();
        tag.clear();
        position.clear();
    }
}
