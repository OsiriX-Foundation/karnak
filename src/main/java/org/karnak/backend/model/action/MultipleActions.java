/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.model.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dcm4che6.data.DicomElement;
import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.karnak.backend.config.AppConfig;
import org.karnak.backend.exception.StandardDICOMException;
import org.karnak.backend.model.profilepipe.HMAC;
import org.karnak.backend.model.standard.Attribute;
import org.karnak.backend.model.standard.Module;
import org.karnak.backend.model.standard.StandardDICOM;
import org.karnak.backend.util.MetadataDICOMObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleActions extends AbstractAction {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultipleActions.class);

  final StandardDICOM standardDICOM;
  final ActionItem defaultDummyValue;
  final ActionItem actionUID;
  final ActionItem actionReplaceNull;
  final ActionItem actionRemove;
  final ActionItem actionKeep;

  public MultipleActions(String symbol) {
    super(symbol);
    standardDICOM = AppConfig.getInstance().getStandardDICOM();
    defaultDummyValue = new DefaultDummy(symbol);
    actionUID = new UID("U");
    actionReplaceNull = new ReplaceNull("Z");
    actionRemove = new Remove("X");
    actionKeep = new Keep("K");
  }

  @Override
  public void execute(DicomObject dcm, int tag, Iterator<DicomElement> iterator, HMAC hmac) {
    final String sopUID = MetadataDICOMObject.getValue(dcm, Tag.SOPClassUID);
    final String tagPath = MetadataDICOMObject.getTagPath(dcm, tag);
    try {
      List<Attribute> attributes = standardDICOM.getAttributesBySOP(sopUID, tagPath);
      if (attributes.size() == 1) {
        String currentType = attributes.get(0).getType();
        ActionItem actionItem = chooseAction(sopUID, currentType);
        actionItem.execute(dcm, tag, iterator, hmac);
      } else if (attributes.size() > 1) {
        ActionItem action = multipleAttributes(sopUID, attributes);
        action.execute(dcm, tag, iterator, hmac);
      } else {
        ActionItem action = defaultAction();
        action.execute(dcm, tag, iterator, hmac);
        LOGGER.warn(
            String.format(
                "Could not found the attribute %s in the SOP %s. The most strictest action will be choose (%s).",
                tagPath, sopUID, symbol));
      }
    } catch (StandardDICOMException standardDICOMException) {
      LOGGER.error(
          String.format(
              "Could not execute the action %s with the SOP %s and the attribute %s",
              symbol, sopUID, tagPath),
          standardDICOMException);
    }
  }

  private ActionItem multipleAttributes(String sopUID, List<Attribute> attributes) {
    List<Attribute> mandatoryAttributes = getMandatoryAttributes(sopUID, attributes);

    if (mandatoryAttributes.size() == 0) {
      String currentType = Attribute.getStrictedType(attributes);
      return chooseAction(sopUID, currentType);
    }

    if (mandatoryAttributes.size() == 1) {
      String currentType = mandatoryAttributes.get(0).getType();
      return chooseAction(sopUID, currentType);
    }

    String currentType = Attribute.getStrictedType(mandatoryAttributes);
    return chooseAction(sopUID, currentType);
  }

  private List<Attribute> getMandatoryAttributes(String sopUID, List<Attribute> attributes) {
    List<Attribute> mandatoryAttributes = new ArrayList<>();
    attributes.forEach(
        attribute -> {
          Module module =
              standardDICOM.getModuleByModuleID(sopUID, attribute.getModuleId()).orElse(null);
          if (module != null && Module.moduleIsMandatory(module)) {
            mandatoryAttributes.add(attribute);
          }
        });
    return mandatoryAttributes;
  }

  private ActionItem chooseAction(String sopUID, String currentType) {
    return switch (symbol) {
      case "Z/D" -> DummyOrReplaceNull(currentType);
      case "X/D" -> DummyOrRemove(currentType);
      case "X/Z/D" -> DummyOrReplaceNullOrRemove(currentType);
      case "X/Z" -> ReplaceNullOrRemove(currentType);
      case "X/Z/U", "X/Z/U*" -> UIDorReplaceNullOrRemove(currentType);
      default -> defaultDummyValue;
    };
  }

  private ActionItem defaultAction() {
    return switch (symbol) {
      case "X/Z" -> actionReplaceNull;
      case "X/Z/U", "X/Z/U*" -> actionUID;
      default -> defaultDummyValue;
    };
  }

  private ActionItem DummyOrReplaceNull(String currentType) {
    if (currentType.equals("1") || currentType.equals("1C")) {
      return defaultDummyValue;
    }
    return actionReplaceNull;
  }

  private ActionItem DummyOrRemove(String currentType) {
    if (currentType.equals("3")) {
      return actionRemove;
    }
    return defaultDummyValue;
  }

  private ActionItem DummyOrReplaceNullOrRemove(String currentType) {
    if (currentType.equals("1") || currentType.equals("1C")) {
      return defaultDummyValue;
    }
    if (currentType.equals("2") || currentType.equals("2C")) {
      return actionReplaceNull;
    }
    return actionRemove;
  }

  private ActionItem ReplaceNullOrRemove(String currentType) {
    /* TODO: throw exception ?
    if (currentType.equals("1") || currentType.equals("1C")) {
        throw new Exception(For the current SOP, the tag must type 1. Impossible to execute and respect the standard);
    }
     */
    if (currentType.equals("2") || currentType.equals("2C")) {
      return actionReplaceNull;
    }
    return actionRemove;
  }

  private ActionItem UIDorReplaceNullOrRemove(String currentType) {
    if (currentType.equals("1") || currentType.equals("1C")) {
      return actionUID;
    }
    if (currentType.equals("2") || currentType.equals("2C")) {
      return actionReplaceNull;
    }
    return actionRemove;
  }
}
