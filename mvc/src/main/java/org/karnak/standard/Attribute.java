package org.karnak.standard;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Attribute {
    private static final List<String> strictedTypes = Arrays.asList("1", "1C", "2", "2C", "3");
    private final String moduleTagPath;
    private final String tagPath;
    private final String type;
    private final String moduleId;

    public Attribute(String moduleTagPath, String type, String moduleId) {
        this.moduleTagPath = moduleTagPath;
        this.type = type;
        this.moduleId = moduleId;
        this.tagPath = generateTagPath(moduleTagPath, moduleId);
    }

    private String generateTagPath(String tagPath, String moduleId) {
        List<String> tagPathFiltered = Arrays.stream(tagPath.split(":"))
                .filter(value -> !value.equals(moduleId))
                .collect(Collectors.toList());

        return StringUtils.join(tagPathFiltered, ":");
    }

    public String getModuleTagPath() {
        return moduleTagPath;
    }

    public String getTagPath() {
        return tagPath;
    }

    public String getType() {
        return type;
    }

    public String getModuleId() {
        return moduleId;
    }

    public static String getStrictedType(List<Attribute> attributes) {
        for (String strictedType : strictedTypes) {
            if (attributes.stream()
                    .anyMatch(attribute -> strictedType.equals(attribute.getType())) == true) {
                return strictedType;
            }
        }
        return null;
    }
}
