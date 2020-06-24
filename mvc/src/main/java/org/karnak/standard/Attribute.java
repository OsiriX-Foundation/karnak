package org.karnak.standard;

public class Attribute {
    private String tagPath;
    private String type;
    private String moduleId;

    public Attribute(String tagPath, String type, String moduleId) {
        this.tagPath = tagPath;
        this.type = type;
        this.moduleId = moduleId;
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
}
