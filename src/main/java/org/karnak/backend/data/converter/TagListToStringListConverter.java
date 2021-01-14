package org.karnak.backend.data.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.TagEntity;

//https://www.logicbig.com/tutorials/misc/jackson/json-serialize-deserialize-converter.html
public class TagListToStringListConverter extends StdConverter<List<TagEntity>, List<String>> {

    @Override
    public List<String> convert(List<TagEntity> tagEntities) {
        List strArray = new ArrayList();
        tagEntities.forEach(tagEntity -> {
            strArray.add(tagEntity.getTagValue());
        });
        return strArray;
    }
}
