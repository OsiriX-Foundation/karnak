package org.karnak.standard;

import org.karnak.standard.dicominnolitics.jsonCIOD;
import org.karnak.standard.dicominnolitics.jsonCIODtoModule;
import org.karnak.standard.dicominnolitics.jsonModuleToAttribute;
import org.karnak.standard.dicominnolitics.jsonSOP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Attributes {
    private static ArrayList<Attribute> attributes;

    public Attributes(jsonModuleToAttribute[] moduleToAttributes) {
        attributes = initializeAttributes(moduleToAttributes);
    }

    private ArrayList<Attribute> initializeAttributes(jsonModuleToAttribute[] moduleToAttributes) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        for (jsonModuleToAttribute moduleToAttribute: moduleToAttributes) {
            Attribute attribute = new Attribute(
                    moduleToAttribute.getPath(),
                    moduleToAttribute.getType(),
                    moduleToAttribute.getModuleId()
            );

            attributes.add(attribute);
        }

        return attributes;
    }

    public List<Attribute> getAttributesByModule(String module) {
        Predicate<Attribute> modulePredicate = attribute -> module.equals(attribute.getModuleId());
        return attributes.stream().filter(modulePredicate).collect(Collectors.toList());
    }
}
