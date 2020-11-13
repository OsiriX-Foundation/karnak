package org.karnak.data.profile.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.karnak.data.profile.Argument;
import org.karnak.data.profile.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
