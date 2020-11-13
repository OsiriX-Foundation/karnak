package org.karnak.standard;

import org.karnak.standard.dicominnolitics.jsonModuleToAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleToAttributes {
    private static HashMap<String, List<Attribute>> HMapAttributes;

    public ModuleToAttributes(jsonModuleToAttribute[] moduleToAttributes) {
        HMapAttributes = initializeAttributes(moduleToAttributes);
    }

    private HashMap<String, List<Attribute>> initializeAttributes(jsonModuleToAttribute[] moduleToAttributes) {
        HashMap<String, List<Attribute>> HMapAttributes = new HashMap<>();

        for (jsonModuleToAttribute moduleToAttribute: moduleToAttributes) {
            Attribute attribute = new Attribute(
                    moduleToAttribute.getPath(),
                    moduleToAttribute.getType(),
                    moduleToAttribute.getModuleId()
            );

            String moduleKey = moduleToAttribute.getModuleId();
            if (!HMapAttributes.containsKey(moduleKey)) {
                HMapAttributes.put(moduleKey, new ArrayList<>());
            }
            HMapAttributes.get(moduleKey).add(attribute);
        }

        return HMapAttributes;
    }

    public List<Attribute> getAttributesByModule(String moduleID) {
        return HMapAttributes.get(moduleID);
    }
}
