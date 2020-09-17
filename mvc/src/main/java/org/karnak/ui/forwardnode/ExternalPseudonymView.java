package org.karnak.ui.forwardnode;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;

public class ExternalPseudonymView extends HorizontalLayout {

    private Binder<Destination> binder;
    private Checkbox pseudonymAsPatientName;
    private Checkbox externalPseudonymCheckbox;
    private TextField delimiter;
    private TextField tag;
    private TextField position;
    private HorizontalLayout horizontalLayoutPseudonymInDicom;
    private Div verticalLayoutExeternalPseudonym;
    private Select<String> extidListBox;
    private boolean enableValidatorADD_EXTID;

    final String [] extidSentence = {"Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};
    private IdTypes idTypes;

    public ExternalPseudonymView(Binder<Destination> binder) {
        this.binder = binder;
        idTypes = IdTypes.EXTID;

        setElements();

        setBinder();

        externalPseudonymCheckbox.addValueChangeListener(event -> {
            if (event != null) {
                if(event.getValue()) {
                    verticalLayoutExeternalPseudonym.setVisible(true);
                } else {
                    verticalLayoutExeternalPseudonym.setVisible(false);
                    pseudonymAsPatientName.setValue(false);
                    dontUseADD_EXTID();
                }
            }
        });

        extidListBox.addValueChangeListener(valueChangeEvent -> {
            if (valueChangeEvent.getValue() != null) {
                if(valueChangeEvent.getValue().equals(extidSentence[0])){
                    idTypes = IdTypes.EXTID;
                    dontUseADD_EXTID();
                }else{
                    idTypes = IdTypes.ADD_EXTID;
                    useADD_EXTID();
                }
            }
        });
        horizontalLayoutPseudonymInDicom = new HorizontalLayout(tag, delimiter, position);
        verticalLayoutExeternalPseudonym = new Div();
        verticalLayoutExeternalPseudonym.add(pseudonymAsPatientName);
        verticalLayoutExeternalPseudonym.add(extidListBox);
        verticalLayoutExeternalPseudonym.add(horizontalLayoutPseudonymInDicom);
        add(externalPseudonymCheckbox);
        add(verticalLayoutExeternalPseudonym);
    }

    private void setElements() {
        externalPseudonymCheckbox = new Checkbox();
        externalPseudonymCheckbox.setLabel("Use an external pseudonym");
        externalPseudonymCheckbox.setMinWidth("25%");

        pseudonymAsPatientName = new Checkbox("Use external pseudonym as Patient Name");

        extidListBox = new Select<>();
        extidListBox.setWidthFull();
        extidListBox.setItems(extidSentence);

        delimiter = new TextField("Delimiter");
        tag = new TextField("Tag");
        position = new TextField("Position");


    }

    public void setBinder() {
        binder.forField(pseudonymAsPatientName).bind(Destination::getPseudonymAsPatientName, Destination::setPseudonymAsPatientName);

        binder.forField(extidListBox).bind(destination -> {
            if (destination.getIdTypes().equals(IdTypes.ADD_EXTID)) {
                return extidSentence[1];
            } else {
                return extidSentence[0];
            }
        }, (destination, value) -> {
            if (value.equals(extidSentence[1])) {
                destination.setIdTypes(IdTypes.ADD_EXTID);
            } else {
                destination.setIdTypes(IdTypes.EXTID);
            }
        });

        binder.forField(externalPseudonymCheckbox).bind(destination -> {
            if (destination.getIdTypes().equals(IdTypes.PID)) {
                verticalLayoutExeternalPseudonym.setVisible(false);
                return false;
            } else {
                verticalLayoutExeternalPseudonym.setVisible(true);
                return true;
            }
        }, (destination, value) -> {
            if (value) {
                destination.setIdTypes(idTypes);
            } else {
                destination.setIdTypes(IdTypes.PID);
            }
        });

        binder.forField(tag)
                .withValidator(tag -> {
                            final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
                            try {
                                TagUtils.intFromHexString(cleanTag);
                            } catch (Exception e) {
                                return false;
                            }
                            return !enableValidatorADD_EXTID || (tag != null && !tag.equals("") && cleanTag.length() == 8);
                        },
                        "Choose a valid tag\n")
                .bind(Destination::getTag, (destination, value) -> {
                    destination.setTag(value);
                });

        binder.forField(delimiter)
                .withValidator(delimiter -> !enableValidatorADD_EXTID || !(delimiter.equals("") && !position.getValue().equals("")),
                        "Choose a delimiter when a position is defined\n")
                .bind(Destination::getDelimiter, (destination, value) -> {
                    destination.setDelimiter(value);
                });

        binder.forField(position)
                .withConverter(new StringToIntegerConverter("Must be a numeric value"))
                .withValidator(position -> !enableValidatorADD_EXTID || !(position == null && !delimiter.equals("")),
                        "Choose a position when a delimiter is defined\n")
                .bind(Destination::getPosition, (destination, integer) -> {
                    destination.setPosition(integer);
                });
    }

    public void disableDesidentification() {
        idTypes = IdTypes.PID;
        pseudonymAsPatientName.setValue(false);
        externalPseudonymCheckbox.setValue(false);
        dontUseADD_EXTID();
    }

    public void useADD_EXTID() {
        horizontalLayoutPseudonymInDicom.setVisible(true);
        enableValidatorADD_EXTID = true;
    }

    public void dontUseADD_EXTID() {
        tag.setValue("");
        delimiter.setValue("");
        position.setValue("0");
        horizontalLayoutPseudonymInDicom.setVisible(false);
        enableValidatorADD_EXTID = false;
    }
}
