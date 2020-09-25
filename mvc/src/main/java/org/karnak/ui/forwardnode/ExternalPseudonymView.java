package org.karnak.ui.forwardnode;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;
import org.karnak.util.DoubleToIntegerConverter;

public class ExternalPseudonymView extends HorizontalLayout {

    private Binder<Destination> binder;
    private Checkbox pseudonymAsPatientName;
    private Checkbox externalPseudonymCheckbox;
    private TextField delimiter;
    private TextField tag;
    private NumberField position;
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
                    idTypes = extidListBox.getValue().equals(extidSentence[0]) ?  IdTypes.EXTID : IdTypes.ADD_EXTID;
                } else {
                    extidListBox.setValue(extidSentence[0]);
                    verticalLayoutExeternalPseudonym.setVisible(false);
                    pseudonymAsPatientName.setValue(false);
                    idTypes = IdTypes.PID;
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
        position = new NumberField("Position");
        position.setHasControls(true);
        position.setMin(0);
        position.setStep(1);


    }

    public void setBinder() {
        binder.forField(pseudonymAsPatientName).bind(destination -> {
                    if (destination.getPseudonymAsPatientName() == null) {
                        return false;
                    } else {
                        return destination.getPseudonymAsPatientName();
                    }
                },
                (destination, aBoolean) -> {
                    if (idTypes == null || idTypes.equals(IdTypes.PID)) {
                        destination.setPseudonymAsPatientName(null);
                    } else {
                        destination.setPseudonymAsPatientName(aBoolean);
                    }
        });

        binder.forField(extidListBox).bind(destination -> {
            if (destination.getIdTypes() == null || !destination.getIdTypes().equals(IdTypes.ADD_EXTID)) {
                return extidSentence[0];
            } else {
                return extidSentence[1];
            }
        }, (destination, value) -> {
            destination.setIdTypes(idTypes);
        });

        binder.forField(externalPseudonymCheckbox).bind(destination -> {
            if (destination.getIdTypes() == null || destination.getIdTypes().equals(IdTypes.PID)) {
                verticalLayoutExeternalPseudonym.setVisible(false);
                return false;
            } else {
                verticalLayoutExeternalPseudonym.setVisible(true);
                return true;
            }
        }, (destination, value) -> {
            destination.setIdTypes(idTypes);
        });

        binder.forField(tag)
                .withConverter(String::valueOf, value -> (value == null) ? "" : String.valueOf(value), "Must be a tag")
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
                    if(value.equals("")){
                        destination.setTag(null);
                    } else {
                        destination.setTag(value);
                    }
                });

        binder.forField(delimiter)
                .withConverter(String::valueOf, value -> (value == null) ? "" : String.valueOf(value), "Must be a delimiter")
                .withValidator(delimiter -> (!enableValidatorADD_EXTID || !(delimiter.equals("") && position.getValue() > 0d)),
                        "Choose a delimiter when a position is defined\n")
                .bind(Destination::getDelimiter, (destination, value) -> {
                    if(value.equals("")){
                        destination.setDelimiter(null);
                    } else {
                        destination.setDelimiter(value);
                    }
                });

        binder.forField(position)
                .withConverter(new DoubleToIntegerConverter())
                .withValidator(position -> !enableValidatorADD_EXTID || !(position == null && !delimiter.equals("") && position < 0),
                        "Choose a position when a delimiter is defined\n")
                .bind(Destination::getPosition, (destination, value) -> {
                    if(value < 0){
                        destination.setPosition(null);
                    } else {
                        destination.setPosition(value);
                    }
                });
    }

    public void disableDesidentification() {
        pseudonymAsPatientName.setValue(false);
        externalPseudonymCheckbox.setValue(false);
        dontUseADD_EXTID();
        idTypes = null;
    }

    public void useADD_EXTID() {
        horizontalLayoutPseudonymInDicom.setVisible(true);
        enableValidatorADD_EXTID = true;
        position.setValue(0d); //set 0 value by default
    }

    public void dontUseADD_EXTID() {
        //allows you to know when to set a null in the db
        tag.setValue("");
        delimiter.setValue("");
        position.setValue(-1d);

        horizontalLayoutPseudonymInDicom.setVisible(false);
        enableValidatorADD_EXTID = false;
    }
}
