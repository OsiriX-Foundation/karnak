package org.karnak.ui.forwardnode;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.Destination;
import org.karnak.ui.util.UIS;
import org.karnak.util.DoubleToIntegerConverter;

public class ADD_EXTIDView extends Div {

    private Binder<Destination> destinationBinder;
    private TextField delimiter;
    private TextField tag;
    private NumberField position;
    private boolean setAllValidator;

    public ADD_EXTIDView(Binder<Destination> destinationBinder) {
        this.destinationBinder = destinationBinder;
        setWidthFull();
        setElements();
        setBinder();
        setAllValidator = false;
        add(UIS.setWidthFull(new HorizontalLayout(tag, delimiter, position)));
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
        tag.setValue("");
        delimiter.setValue("");
        position.setValue(-1d);
        setAllValidator = false;
    }

    public void enableComponent(){
        setAllValidator = true;
    }

    private void setBinder() {

        destinationBinder.forField(tag)
                .withConverter(String::valueOf, value -> (value == null) ? "" : String.valueOf(value), "Must be a tag")
                .withValidator(tag -> {
                            final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
                            try {
                                TagUtils.intFromHexString(cleanTag);
                            } catch (Exception e) {
                                return false;
                            }
                            return !setAllValidator || (tag != null && !tag.equals("") && cleanTag.length() == 8);
                        },
                        "Choose a valid tag\n")
                .bind(Destination::getTag, (destination, value) -> {
                    if(value.equals("")){
                        destination.setTag(null);
                    } else {
                        destination.setTag(value);
                    }
                });

        destinationBinder.forField(delimiter)
                .withConverter(String::valueOf, value -> (value == null) ? "" : String.valueOf(value), "Must be a delimiter")
                .withValidator(delimiter -> (!setAllValidator || !(delimiter.equals("") && position.getValue() > 0d)),
                        "Choose a delimiter when a position is defined\n")
                .bind(Destination::getDelimiter, (destination, value) -> {
                    if(value.equals("")){
                        destination.setDelimiter(null);
                    } else {
                        destination.setDelimiter(value);
                    }
                });

        destinationBinder.forField(position)
                .withConverter(new DoubleToIntegerConverter())
                .withValidator(position -> !setAllValidator || !(position == null && !delimiter.equals("") && position < 0),
                        "Choose a position when a delimiter is defined\n")
                .bind(Destination::getPosition, (destination, value) -> {
                    if(value < 0){
                        destination.setPosition(null);
                    } else {
                        destination.setPosition(value);
                    }
                });

    }
}
