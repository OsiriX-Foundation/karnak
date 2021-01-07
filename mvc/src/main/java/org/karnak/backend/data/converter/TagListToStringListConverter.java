package org.karnak.backend.data.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.util.ArrayList;
import java.util.List;
import org.karnak.backend.data.entity.Tag;

//https://www.logicbig.com/tutorials/misc/jackson/json-serialize-deserialize-converter.html
public class TagListToStringListConverter extends StdConverter <List<Tag>, List<String>> {

    @Override
    public List<String> convert(List<Tag> tags) {
        List strArray = new ArrayList();
        tags.forEach(tag -> {
            strArray.add(tag.getTagValue());
        });
        return strArray;
    }
}
