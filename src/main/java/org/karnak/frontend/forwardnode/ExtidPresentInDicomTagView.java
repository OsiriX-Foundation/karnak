package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.util.DoubleToIntegerConverter;
import org.karnak.frontend.util.UIS;

public class ExtidPresentInDicomTagView extends Div {

  private final Binder<DestinationEntity> destinationBinder;
  private TextField delimiter;
  private TextField tag;
  private NumberField position;
  private Checkbox savePseudonym;

  public ExtidPresentInDicomTagView(Binder<DestinationEntity> destinationBinder) {
    this.destinationBinder = destinationBinder;
    setWidthFull();
    setElements();
    setBinderWithValidator();
    add(UIS.setWidthFull(new HorizontalLayout(tag, delimiter, position, savePseudonym)));
  }

  public void setElements() {
    delimiter = new TextField("Delimiter");
    tag = new TextField("Tag");
    position = new NumberField("Position");
    position.setHasControls(true);
    position.setMin(0);
    position.setStep(1);
    savePseudonym = new Checkbox("Save pseudonym");
    savePseudonym.getStyle().set("margin-top", "30px");
    savePseudonym.setValue(true);
  }

  public void clear() {
    removeAllBinding();
    setBinderWithoutValidator();
    tag.clear();
    delimiter.clear();
    position.clear();
    savePseudonym.clear();
  }

  public void enableComponent() {
    removeAllBinding();
    setBinderWithValidator();
  }

  private void removeAllBinding() {
    final boolean tagExist = destinationBinder.getFields().anyMatch(field -> field.equals(tag));
    final boolean positionExist =
        destinationBinder.getFields().anyMatch(field -> field.equals(position));
    final boolean delimiterExist =
        destinationBinder.getFields().anyMatch(field -> field.equals(delimiter));
    final boolean savePseudonymExist =
        destinationBinder.getFields().anyMatch(field -> field.equals(savePseudonym));

    if (tagExist && positionExist && delimiterExist && savePseudonymExist) {
      destinationBinder.removeBinding(tag);
      destinationBinder.removeBinding(delimiter);
      destinationBinder.removeBinding(position);
      destinationBinder.removeBinding(savePseudonym);
    }
  }

  private void setBinderWithValidator() {
    destinationBinder
        .forField(tag)
        .withConverter(String::valueOf, value -> (value == null) ? "" : value, "Must be a tag")
        .withValidator(
            tag -> {
              final String cleanTag = tag.replaceAll("[(),]", "").toUpperCase();
              try {
                TagUtils.intFromHexString(cleanTag);
              } catch (Exception e) {
                return false;
              }
              return (tag != null && !tag.equals("") && cleanTag.length() == 8);
            },
            "Choose a valid tag\n")
        .bind(DestinationEntity::getTag, DestinationEntity::setTag);

    destinationBinder
        .forField(delimiter)
        .withConverter(
            String::valueOf, value -> (value == null) ? "" : value, "Must be a delimiter")
        .withValidator(
            delimiter -> {
              if (position.getValue() != null && position.getValue() > 0) {
                return delimiter != null && !delimiter.equals("");
              }
              return true;
            },
            "A delimiter must be defined, when a position is present")
        .bind(DestinationEntity::getDelimiter, DestinationEntity::setDelimiter);

    destinationBinder
        .forField(position)
        .withConverter(new DoubleToIntegerConverter())
        .withValidator(
            position -> {
              if (delimiter.getValue() != null && !delimiter.getValue().equals("")) {
                return position != null && position >= 0;
              }
              return true;
            },
            "A position must be defined, when a delimiter is present")
        .bind(DestinationEntity::getPosition, DestinationEntity::setPosition);

    destinationBinder
        .forField(savePseudonym)
        .bind(DestinationEntity::getSavePseudonym, DestinationEntity::setSavePseudonym);
  }

  private void setBinderWithoutValidator() {
    destinationBinder
        .forField(tag)
        .bind(DestinationEntity::getTag, (destination, s) -> destination.setTag(null));

    destinationBinder
        .forField(delimiter)
        .bind(DestinationEntity::getDelimiter, (destination, s) -> destination.setDelimiter(null));

    destinationBinder
        .forField(position)
        .withConverter(new DoubleToIntegerConverter())
        .bind(DestinationEntity::getPosition, (destination, s) -> destination.setPosition(null));

    destinationBinder
        .forField(savePseudonym)
        .bind(
            DestinationEntity::getSavePseudonym,
            (destination, s) -> destination.setSavePseudonym(null));
  }
}
