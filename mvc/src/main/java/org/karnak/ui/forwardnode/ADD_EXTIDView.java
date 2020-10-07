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

    public ADD_EXTIDView(Binder<Destination> destinationBinder) {
        this.destinationBinder = destinationBinder;
        setWidthFull();
        setElements();
        setBinderWithValidator();
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
        removeAllBinding();
        setBinderWithoutValidator();
        tag.clear();
        delimiter.clear();
        position.clear();
    }

    public void enableComponent(){
        removeAllBinding();
        setBinderWithValidator();

    }

    private void removeAllBinding(){
        final boolean tagExist = destinationBinder.getFields().anyMatch(field -> field.equals(tag));
        final boolean positionExist = destinationBinder.getFields().anyMatch(field -> field.equals(position));
        final boolean delimiterExist = destinationBinder.getFields().anyMatch(field -> field.equals(delimiter));

        if(tagExist && positionExist && delimiterExist){
            destinationBinder.removeBinding(tag);
            destinationBinder.removeBinding(delimiter);
            destinationBinder.removeBinding(position);
        }
    }

    private void setBinderWithValidator() {
        destinationBinder.forField(tag)
                .withConverter(String::valueOf, value -> (value == null) ? "" : String.valueOf(value), "Must be a tag")
                .withValidator(tag -> {
                            final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
                            try {
                                TagUtils.intFromHexString(cleanTag);
                            } catch (Exception e) {
                                return false;
                            }
                            return (tag != null && !tag.equals("") && cleanTag.length() == 8);
                        },
                        "Choose a valid tag\n")
                .bind(Destination::getTag, Destination::setTag);

        destinationBinder.forField(delimiter)
                .withConverter(String::valueOf, value -> (value == null) ? "" : String.valueOf(value), "Must be a delimiter")
                .withValidator(delimiter -> (!(delimiter.equals("") && position.getValue() > 0d)),
                        "Choose a delimiter when a position is defined\n")
                .bind(Destination::getDelimiter, Destination::setDelimiter);

        destinationBinder.forField(position)
                .withConverter(new DoubleToIntegerConverter())
                .withValidator(position -> !(position == null && !delimiter.equals("") && position < 0),
                        "Choose a position when a delimiter is defined\n")
                .bind(Destination::getPosition, Destination::setPosition);

    }


    private void setBinderWithoutValidator() {
        destinationBinder.forField(tag).bind(Destination::getTag, (destination, s) -> destination.setTag(null));

        destinationBinder.forField(delimiter).bind(Destination::getDelimiter, (destination, s) -> destination.setDelimiter(null));

        destinationBinder.forField(position)
                .withConverter(new DoubleToIntegerConverter())
                .bind(Destination::getPosition, (destination, s) -> destination.setPosition(null));
    }
}
