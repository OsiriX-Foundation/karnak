package org.karnak.backend.data.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.karnak.backend.data.entity.Argument;

public class ArgumentToMapConverter extends StdConverter<List<Argument>, Map<String, String>> {

    @Override
    public Map<String, String> convert(List<Argument> arguments) {
        Map<String, String> argumentMap = new HashMap<>();
        arguments.forEach(argument -> {
            argumentMap.put(argument.getKey(), argument.getValue());
        });
        return argumentMap;
    }
}
