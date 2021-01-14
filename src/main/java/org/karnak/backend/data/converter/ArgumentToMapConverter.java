package org.karnak.backend.data.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.karnak.backend.data.entity.ArgumentEntity;

public class ArgumentToMapConverter
    extends StdConverter<List<ArgumentEntity>, Map<String, String>> {

  @Override
  public Map<String, String> convert(List<ArgumentEntity> argumentEntities) {
    Map<String, String> argumentMap = new HashMap<>();
    argumentEntities.forEach(
        argument -> {
          argumentMap.put(argument.getKey(), argument.getValue());
        });
    return argumentMap;
  }
}
