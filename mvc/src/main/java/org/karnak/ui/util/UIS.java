package org.karnak.ui.util;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;

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
