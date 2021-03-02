package org.karnak.backend.model.standard;

public class AttributeDetail {
  private final String id;
  private final String keyword;
  private final String name;
  private final String retired;
  private final String tag;
  private final String valueMultiplicity;
  private final String valueRepresentation;

  public AttributeDetail(
      String id,
      String keyword,
      String name,
      String retired,
      String tag,
      String valueMultiplicity,
      String valueRepresentation) {
    this.id = id;
    this.keyword = keyword;
    this.name = name;
    this.retired = retired;
    this.tag = tag;
    this.valueMultiplicity = valueMultiplicity;
    this.valueRepresentation = valueRepresentation;
  }

  public String getId() {
    return id;
  }

  public String getKeyword() {
    return keyword;
  }

  public String getName() {
    return name;
  }

  public String getRetired() {
    return retired;
  }

  public String getTag() {
    return tag;
  }

  public String getValueMultiplicity() {
    return valueMultiplicity;
  }

  public String getValueRepresentation() {
    return valueRepresentation;
  }
}
