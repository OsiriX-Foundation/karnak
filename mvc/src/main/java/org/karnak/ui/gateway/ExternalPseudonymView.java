package org.karnak.ui.gateway;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.data.gateway.IdTypes;

public class ExternalPseudonymView extends Div {

    private Binder<Destination> binder;
    private TextField delimiter;
    private TextField tag;
    private TextField position;
    private HorizontalLayout horizontalLayoutPseudonymInDicom;
    private Select<String> extidListBox;

    final String [] extidSentence = {"Pseudonym is already store in KARNAK", "Pseudonym is in a DICOM tag"};
    private IdTypes idTypes;

    public ExternalPseudonymView(Binder<Destination> binder) {
        setWidthFull();
        this.binder = binder;
        idTypes = IdTypes.EXTID;

        delimiter = new TextField("Delimiter");
        tag = new TextField("Tag");
        position = new TextField("Position");
        horizontalLayoutPseudonymInDicom = new HorizontalLayout(tag, delimiter, position);

        extidListBox = new Select<>();
        extidListBox.setWidthFull();
        extidListBox.setItems(extidSentence);
        extidListBox.setValue(extidSentence[0]);
        showStoreInDicom(false);
        extidListBox.addValueChangeListener(valueChangeEvent -> {
            if(valueChangeEvent.getValue().equals(extidSentence[0])){
                idTypes = IdTypes.EXTID;
                showStoreInDicom(false);
            }else{
                idTypes = IdTypes.ADD_EXTID;
                showStoreInDicom(true);
            }
        });
        fieldValidator();

        add(extidListBox);
        add(horizontalLayoutPseudonymInDicom);
    }

    public void fieldValidator() {
        binder.forField(tag)
                .withValidator(tag -> {
                            if(extidListBox.getValue().equals(extidSentence[0])){
                                return true;
                            }
                            final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
                            try {
                                TagUtils.intFromHexString(cleanTag);
                            } catch (Exception e) {
                                return false;
                            }
                            return tag != null && !tag.equals("") && cleanTag.length() == 8;
                        },
                        "Choose a valid tag\n")
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        return destination.getExternalPseudonym().getTag();
                    } else {
                        return null;
                    }
                }, (destination, value) -> {
                    destination.getExternalPseudonym().setTag(value);
                });

        binder.forField(delimiter)
                .withValidator(delimiter -> !(delimiter.equals("") && !position.getValue().equals("")) || (extidListBox.getValue().equals(extidSentence[0])),
                        "Choose a delimiter when a position is defined\n")
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        return destination.getExternalPseudonym().getDelimiter();
                    } else {
                        return "";
                    }
                }, (destination, value) -> {
                    destination.getExternalPseudonym().setDelimiter(value);
                });

        binder.forField(position)
                .withConverter(new StringToIntegerConverter("Must be a numeric value"))
                .withValidator(position -> (extidListBox.getValue().equals(extidSentence[0])) || !(position == null && !delimiter.equals("")),
                        "Choose a position when a delimiter is defined\n")
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        return destination.getExternalPseudonym().getPosition();
                    } else {
                        return 0;
                    }
                }, (destination, value) -> {
                    destination.getExternalPseudonym().setPosition(value);
                });

        binder.forField(extidListBox)
                .bind(destination -> {
                    if(destination.getExternalPseudonym() != null) {
                        if(destination.getIdTypes().equals(IdTypes.ADD_EXTID)){
                            return extidSentence[1];
                        } else {
                            return extidSentence[0];
                        }
                    } else {
                        return "";
                    }
                }, (destination, value) -> {
                    if(value.equals(extidSentence[1])){
                        destination.setIdTypes(IdTypes.ADD_EXTID);
                    } else {
                        destination.setIdTypes(IdTypes.EXTID);
                    }
                });
    }

    public void showStoreInDicom(boolean show){
        tag.setVisible(show);
        position.setVisible(show);
        delimiter.setVisible(show);
    }

    public IdTypes getIdTypes() {
        return idTypes;
    }
}
