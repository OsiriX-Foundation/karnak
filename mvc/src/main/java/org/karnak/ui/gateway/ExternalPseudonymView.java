package org.karnak.ui.gateway;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.ExternalPseudonym;

public class ExternalPseudonymView extends Div {

    private Binder<ExternalPseudonym> binder;
    private TextField delimiter;
    private TextField tag;
    private TextField position;
    private HorizontalLayout horizontalLayoutPseudonymInDicom;
    private Select<String> listBox;
    final String [] extidSentence = {"Pseudonym is already store in karnak", "Pseudonym is in a dicom tag"};

    public ExternalPseudonymView(Binder<ExternalPseudonym> binder) {
        this.binder = binder;
        this.setWidthFull();



        delimiter = new TextField("Delimiter");
        tag = new TextField("Tag");
        position = new TextField("Position");
        horizontalLayoutPseudonymInDicom = new HorizontalLayout(tag, position, delimiter);

        listBox = new Select<>();
        listBox.setWidthFull();
        listBox.setItems(extidSentence);
        listBox.setValue(extidSentence[0]);
        showStoreInDicom(false);
        listBox.addValueChangeListener(valueChangeEvent -> {
            if(valueChangeEvent.getValue().equals(extidSentence[0])){
                showStoreInDicom(false);
            }else{
                showStoreInDicom(true);
            }
        });

        binder.forField(delimiter)
                .withValidator(delimiter -> !(delimiter.equals("") && !position.getValue().equals("")),
                        "Choose a delimiter when a position is defined\n")
                .bind(ExternalPseudonym::getDelimiter, ExternalPseudonym::setDelimiter);

        binder.forField(tag)
                .withValidator(tag -> {
                            String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
                            try {
                                TagUtils.intFromHexString(cleanTag);
                            } catch (Exception e) {
                                return false;
                            }
                            return tag != null && !tag.equals("") && cleanTag.length() == 8;
                        },
                        "Choose a valid tag\n")
                .bind(ExternalPseudonym::getTag, ExternalPseudonym::setTag);

        binder.forField(position)
                .withConverter(new StringToIntegerConverter("Must be a numeric value"))
                .withValidator(position ->  !(position == null && !delimiter.equals("")),
                        "Choose a position when a delimiter is defined\n")
                .bind(ExternalPseudonym::getPosition, ExternalPseudonym::setPosition);


        binder.bindInstanceFields(this);


        add(listBox);
        add(horizontalLayoutPseudonymInDicom);
    }

    public void showStoreInDicom(boolean show){
        tag.setVisible(show);
        position.setVisible(show);
        delimiter.setVisible(show);
    }
}
