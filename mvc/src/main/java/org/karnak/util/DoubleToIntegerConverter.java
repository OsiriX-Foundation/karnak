package org.karnak.util;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DoubleToIntegerConverter implements Converter<Double, Integer> {

    private static final long serialVersionUID = 1L;

    @Override
    public Result<Integer> convertToModel(Double value, ValueContext valueContext) {
        return Result.ok(value.intValue());
    }

    @Override
    public Double convertToPresentation(Integer value, ValueContext valueContext) {
        if (value == null) {
            return -1d;
        } else {
            return value.doubleValue();
        }
    }

}
