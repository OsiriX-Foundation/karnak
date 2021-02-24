package org.karnak.backend.service.report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.model.standard.Attribute;
import org.karnak.backend.model.standard.Module;
import org.karnak.backend.model.standard.StandardDICOM;
import org.karnak.backend.model.validation.ValidatorAttribute;
import org.karnak.backend.model.validation.ValidatorModule;
import org.karnak.backend.util.MetadataDICOMObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reporting {
  private static final Logger LOGGER = LoggerFactory.getLogger(Reporting.class);

  final StandardDICOM standardDICOM;

  public Reporting() {
    standardDICOM = AppConfig.getInstance().getStandardDICOM();
  }

  public void apply(DicomObject dcm) {
    final String sopUID = MetadataDICOMObject.getValue(dcm, Tag.SOPClassUID);
    final HashSet<Module> modulesInDICOM = new HashSet<>();
    // TODO: Missing sequences
    for (Iterator<DicomElement> iterator = dcm.iterator(); iterator.hasNext(); ) {
      final DicomElement dcmEl = iterator.next();
      final String tagPath = MetadataDICOMObject.getTagPath(dcm, dcmEl.tag());
      List<Attribute> attributes = standardDICOM.getAttributesBySOP(sopUID, tagPath);
      validationTagPath(sopUID, dcm, dcmEl, attributes);

      modulesInDICOM.addAll(getModulesByAttributes(sopUID, attributes));
    }
    validationModules(sopUID, modulesInDICOM, dcm);
  }

  private HashSet<Module> getModulesByAttributes(String sopUID, List<Attribute> attributes) {
    HashSet<Module> modules = new HashSet<>();
    attributes.forEach(
        attribute -> {
          Module module =
              standardDICOM.getModuleByModuleID(sopUID, attribute.getModuleId()).orElse(null);
          if (!modules.contains(module)) {
            modules.add(module);
          }
        }
    );
    return modules;
  }

  public boolean validationTagPath(String sopUID, DicomObject dcm, DicomElement dcmEl, List<Attribute> attributes) {
    final String tagValue = dcm.getString(dcmEl.tag()).orElse(null);
    final String tagPath = MetadataDICOMObject.getTagPath(dcm, dcmEl.tag());
    final String currentType = Attribute.getStrictedType(attributes);
    boolean valid = true;
    if (ValidatorAttribute.attributeNotExist(attributes)) {
      LOGGER.error(
          String.format("Attribute %s doesn't exist in the SOP %s.", tagPath, sopUID)
      );
      return false;
    }

    if (ValidatorAttribute.typeNotValid(currentType, tagValue)) {
      LOGGER.warn(
          String.format("The type %s for %s is not valid in the SOP %s", currentType, tagPath, sopUID)
      );
      valid = false;
    }
    /*
    if (ValidatorAttribute.vrNotValid(tagValue, dcmEl.tag(), dcmEl.vr())) {

    }
    */
    return valid;
  }

  public boolean validationModules(String sopUID, HashSet<Module> modulesInDICOM, DicomObject dcm) {
    ArrayList<Module> modulesBySOP = this.standardDICOM.getModulesBySOP(sopUID);
    List<Module> missMandatoryModule = ValidatorModule.missingMandatoryModule(modulesBySOP, modulesInDICOM);
    if (missMandatoryModule.size() > 0) {
      LOGGER.error("Mandatories modules missing");
    }
    for (Module module : modulesInDICOM) {
      Map<String, Attribute> mandatoryAttribute = this.standardDICOM.getModuleAttributesByType(module.getId(), "1");
      verifyAttributePresent(module.getId(), mandatoryAttribute, dcm, "1");
      Map<String, Attribute> mandatoryEmptyAttribute = this.standardDICOM.getModuleAttributesByType(module.getId(), "2");
      verifyAttributePresent(module.getId(), mandatoryEmptyAttribute, dcm, "2");
    }

    return true;
  }

  private void verifyAttributePresent(String moduleName, Map<String, Attribute> mandatoryAttribute, DicomObject dcm, String type) {
    List<String> notFromSeqAttributes = mandatoryAttribute.keySet().stream()
        .filter(tagPath -> notFromSeq(tagPath))
        .collect(Collectors.toList());
    for (String tagPath : notFromSeqAttributes) {
      if (!tagPath.contains("x") && !mandatoryAttribute.get(tagPath).getType().contains("c")) {
        int tag = TagUtils.intFromHexString(tagPath);
        String tagValue = dcm.getString(tag).orElse(null);

        if (ValidatorAttribute.typeNotValid(type, tagValue)) {
          LOGGER.error(
              String.format("The attribute %s don't respect the type %s define in the module %s",
                  tagPath, mandatoryAttribute.get(tagPath).getType(), moduleName)
          );
        }
      }
    }
    List<String> fromSeqAttributes = mandatoryAttribute.keySet().stream()
        .filter(tagPath -> fromSeq(tagPath))
        .collect(Collectors.toList());
  }

  private boolean notFromSeq(String tagPath) {
    return tagPath.split(":").length == 1;
  }

  private boolean fromSeq(String tagPath) {
    return tagPath.split(":").length > 1;
  }
}
