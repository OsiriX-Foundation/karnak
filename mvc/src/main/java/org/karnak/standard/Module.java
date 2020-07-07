package org.karnak.standard;

public class Module {
    private String id;
    private String usage;
    private String informationEntity;

    public Module(String id, String usage, String informationEntity) {
        this.id = id;
        this.usage = usage;
        this.informationEntity = informationEntity;
    }

    public String getId() {
        return id;
    }

    public String getUsage() {
        return usage;
    }

    public String getInformationEntity() {
        return informationEntity;
    }
}
