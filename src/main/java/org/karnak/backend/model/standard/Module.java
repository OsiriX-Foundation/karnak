package org.karnak.backend.model.standard;

public class Module {

  public static final String MANDATORY = "M";

  private final String id;
  private final String usage;
  private final String informationEntity;

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

    public static boolean moduleIsMandatory(Module module) {
        return module.getUsage().equals(MANDATORY);
    }
}
