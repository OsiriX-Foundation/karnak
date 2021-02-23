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
      final int tag = dcmEl.tag();
      final String tagValue = dcm.getString(tag).orElse(null);
      final String tagPath = MetadataDICOMObject.getTagPath(dcm, tag);
      List<Attribute> attributes = standardDICOM.getAttributesBySOP(sopUID, tagPath);
      validationTagPath(sopUID, tagPath, tagValue, attributes);

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

  public boolean validationTagPath(String sopUID, String tagPath, String tagValue, List<Attribute> attributes) {
    String currentType = Attribute.getStrictedType(attributes);
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
      return false;
    }

    return true;
  }

  public boolean validationModules(String sopUID, HashSet<Module> modulesInDICOM, DicomObject dcm) {
    ArrayList<Module> modulesBySOP = this.standardDICOM.getModulesBySOP(sopUID);
    List<Module> missMandatoryModule = ValidatorModule.missingMandatoryModule(modulesBySOP, modulesInDICOM);
    if (missMandatoryModule.size() > 0) {
      LOGGER.error("Mandatories modules missing");
    }

    for (Module module : modulesBySOP) {
      List<Attribute> attributes = this.standardDICOM.getAttributeListByModule(module.getId());
      Map<String, Attribute> mandatoryAttribute = this.standardDICOM.getModuleAttributesByType(module.getId(), "1");
      Map<String, Attribute> mandatoryNullAttribute = this.standardDICOM.getModuleAttributesByType(module.getId(), "2");

      System.out.println("test");
    }

    return true;
  }

}
