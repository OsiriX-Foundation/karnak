package org.karnak.data.profile.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.karnak.data.profile.Tag;

import java.util.ArrayList;
import java.util.List;

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
