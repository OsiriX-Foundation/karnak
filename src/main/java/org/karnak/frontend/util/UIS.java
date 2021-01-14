package org.karnak.frontend.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;

public interface UIS {

  static <T extends HasSize> T setWidthFull(T component) {
    component.setWidthFull();
    return component;
  }

  static <T> T configure(T base, Consumer<T> consumer) {
    consumer.accept(base);
    return base;
  }

  static void setTooltip(Component component, String tooltipText) {
    component.getElement().setAttribute("title", tooltipText);
  }

  static boolean containsNoWhitespace(CharSequence seq) {
    return !StringUtils.containsWhitespace(seq);
  }
}
