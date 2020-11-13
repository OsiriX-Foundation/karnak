package org.karnak.standard;

import org.karnak.standard.dicominnolitics.jsonModuleToAttribute;

import java.util.HashMap;
import java.util.Map;

public class ModuleToAttributes {
    private final Map<String, Map<String, Attribute>> HMapModuleAttributes;

    public ModuleToAttributes(jsonModuleToAttribute[] moduleToAttributes) {
        HMapModuleAttributes = initializeAttributes(moduleToAttributes);
    }

    private Map<String, Map<String, Attribute>> initializeAttributes(jsonModuleToAttribute[] moduleToAttributes) {
        Map<String, Map<String, Attribute>> HMapModuleAttributes = new HashMap<>();

        for (jsonModuleToAttribute moduleToAttribute: moduleToAttributes) {
            Attribute attribute = new Attribute(
                    moduleToAttribute.getPath(),
                    moduleToAttribute.getType(),
                    moduleToAttribute.getModuleId()
            );

            String moduleKey = moduleToAttribute.getModuleId();
            if (!HMapModuleAttributes.containsKey(moduleKey)) {
                HMapModuleAttributes.put(moduleKey, new HashMap<>());
            }
            HMapModuleAttributes.get(moduleKey).put(attribute.getTagPath(), attribute);
        }

        return HMapModuleAttributes;
    }

    public Map<String, Attribute> getAttributesByModule(String moduleID) {
        return HMapModuleAttributes.get(moduleID);
    }
}
