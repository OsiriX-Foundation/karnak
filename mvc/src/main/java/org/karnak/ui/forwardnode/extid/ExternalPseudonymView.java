package org.karnak.ui.forwardnode.extid;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.select.Select;
import org.karnak.data.gateway.ExternalPseudonym;

public class ExternalPseudonymView extends CustomField<ExternalPseudonym> {


    private ExternalPseudonym externalPseudonym;
    private Checkbox checkboxUseAsPatientName;
    private ADD_EXTIDView add_extidView;
    private Select<String> extidListBox;
    final String [] extidSentence = {"Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};

    public ExternalPseudonymView() {
        setElements();
        externalPseudonym = new ExternalPseudonym();

        setEventExtidListBox();

        add(checkboxUseAsPatientName);
        add(extidListBox);
    }


    private void setElements() {
        checkboxUseAsPatientName = new Checkbox("Use as Patient Name");

        extidListBox = new Select<>();
        extidListBox.setWidthFull();
        extidListBox.setItems(extidSentence);

        add_extidView = new ADD_EXTIDView();

    }


    private void setEventExtidListBox() {
        extidListBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue().equals(extidSentence[0])) {
                    add_extidView.clear();
                    remove(add_extidView);
                } else {
                    add(add_extidView);
                }
            }
        });
    }

    public void clear() {
        remove(add_extidView);
        add_extidView.clear();
        extidListBox.clear();
        checkboxUseAsPatientName.clear();
    }

    @Override
    protected ExternalPseudonym generateModelValue() {
        return externalPseudonym;
    }

    @Override
    protected void setPresentationValue(ExternalPseudonym externalPseudonym) {

    }

}
