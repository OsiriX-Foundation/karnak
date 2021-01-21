/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.standard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.dcm4che6.util.TagUtils;
import org.karnak.backend.exception.ModuleNotFoundException;
import org.karnak.backend.exception.SOPNotFoundException;

public class StandardDICOM {

  private final SOPS sops;
  private final ModuleToAttributes moduleToAttributes;

  public StandardDICOM() {
    sops = new SOPS();
    moduleToAttributes = new ModuleToAttributes();
  }

  public static String cleanTagPath(String tagPath) {
    return tagPath.replaceAll("[(),]", "").toLowerCase();
  }

  public List<String> getAllSOPuids() {
    return sops.getAllUIDs();
  }

  public String getCIOD(String sopUID) throws SOPNotFoundException {
    return sops.getCIOD(sopUID);
  }

  public String getIdCIOD(String sopUID) throws SOPNotFoundException {
    return sops.getIdCIOD(sopUID);
  }

  public Optional<Module> getModuleByModuleID(String sopUID, String moduleId)
      throws SOPNotFoundException {
    return sops.getModuleByModuleID(sopUID, moduleId);
  }

  public boolean moduleIsPresent(String sopUID, String moduleId) throws SOPNotFoundException {
    return sops.moduleIsPresent(sopUID, moduleId);
  }

  public Map<Module, Map<String, Attribute>> getModulesBySOP(String sopUID)
      throws SOPNotFoundException {
    return sops.getModuleToAttribute(sopUID, moduleToAttributes);
  }

  public List<String> getModulesNameBySOP(String sopUID) throws SOPNotFoundException {
    return sops.getSOPmodulesName(sopUID);
  }

  public List<Attribute> getAttributesBySOP(String sopUID, int tagPath)
      throws SOPNotFoundException {
    return getAttributesBySOP(sopUID, TagUtils.toHexString(tagPath));
  }

  public List<Attribute> getAttributesBySOP(String sopUID, String tagPath)
      throws SOPNotFoundException {
    String tagPathCleaned = cleanTagPath(tagPath);
    Map<Module, Map<String, Attribute>> HMapModuleAttributes = getModulesBySOP(sopUID);
    List<Attribute> attributes = new ArrayList<>();
    HMapModuleAttributes.forEach(
        (module, attr) -> {
          Attribute attribute = attr.get(tagPathCleaned);
          if (attribute != null) {
            attributes.add(attribute);
          }
        });
    return attributes;
  }

  public Map<String, Attribute> getAttributesByModule(String moduleId) {
    return moduleToAttributes.getAttributesByModule(moduleId);
  }

  public List<Attribute> getAttributeListByModule(String moduleId) {
    return new ArrayList<>(moduleToAttributes.getAttributesByModule(moduleId).values());
  }

  public Map<String, Attribute> getModuleAttributesByType(String moduleId, String type)
      throws ModuleNotFoundException {
    return moduleToAttributes.getModuleAttributesByType(moduleId, type);
  }
}
