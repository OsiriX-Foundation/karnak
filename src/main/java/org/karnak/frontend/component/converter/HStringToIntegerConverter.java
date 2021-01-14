package org.karnak.frontend.component.converter;

import com.vaadin.flow.data.converter.StringToIntegerConverter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

@SuppressWarnings("serial")
public class HStringToIntegerConverter extends StringToIntegerConverter {

  public HStringToIntegerConverter() {
    super(0, "Could not convert value to " + Integer.class.getName() + ".");
  }

  @Override
  protected NumberFormat getFormat(Locale locale) {
    // Do not use a thousands separator, as HTML5 input type number expects a fixed
    // wire/DOM number format regardless of how the browser presents it to the user
    // (which could depend on the browser locale).
    DecimalFormat format = new DecimalFormat();
    format.setMaximumFractionDigits(0);
    format.setDecimalSeparatorAlwaysShown(false);
    format.setParseIntegerOnly(true);
    format.setGroupingUsed(false);
    return format;
  }
}
