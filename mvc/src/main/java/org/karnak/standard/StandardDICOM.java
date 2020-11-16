package org.karnak.standard;

import org.dcm4che6.util.TagUtils;
import org.karnak.data.gateway.SOPClassUIDPersistence;
import org.karnak.standard.dicominnolitics.*;
import org.karnak.ui.data.GatewayConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandardDICOM {
    private final StandardSOPS standardSOPS;
    private final StandardCIODS standardCIODS;
    private final StandardCIODtoModules standardCIODtoModules;
    private final StandardModuleToAttributes standardModuleToAttributes;
    private final SOPS sops;
    private final ModuleToAttributes moduleToAttributes;

    private SOPClassUIDPersistence sopClassUIDPersistence = GatewayConfiguration.getInstance().getSopClassUIDPersistence();

    public StandardDICOM() {
        standardSOPS = new StandardSOPS();
        standardCIODS = new StandardCIODS();
        standardCIODtoModules = new StandardCIODtoModules();
        standardModuleToAttributes = new StandardModuleToAttributes();
        sops = new SOPS(standardSOPS.getSOPS(), standardCIODS.getCIODS(), standardCIODtoModules.getCIODToModules());
        moduleToAttributes = new ModuleToAttributes(standardModuleToAttributes.getModuleToAttributes());
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

    public boolean moduleIsPresent(String sopUID, String moduleId) throws SOPNotFoundException {
        return sops.moduleIsPresent(sopUID, moduleId);
    }

    public Map<Module, Map<String, Attribute>> getModulesBySOP(String sopUID) throws SOPNotFoundException {
        return sops.getModuleToAttribute(sopUID, moduleToAttributes);
    }

    public List<String> getModulesNameBySOP(String sopUID) throws SOPNotFoundException {
        return sops.getSOPmodulesName(sopUID);
    }

    public List<Attribute> getAttributesBySOP(String sopUID, int tagPath) throws SOPNotFoundException {
        return getAttributesBySOP(sopUID, TagUtils.toHexString(tagPath));
    }

    public List<Attribute> getAttributesBySOP(String sopUID, String tagPath) throws SOPNotFoundException {
        String tagPathCleaned = cleanTagPath(tagPath);
        Map<Module, Map<String, Attribute>> HMapModuleAttributes = getModulesBySOP(sopUID);
        List<Attribute> attributes = new ArrayList<>();
        HMapModuleAttributes.forEach((module, attr) -> {
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

    public Map<String, Attribute> getModuleAttributesByType(String moduleId, String type) throws ModuleNotFoundException {
        return moduleToAttributes.getModuleAttributesByType(moduleId, type);
    }

    public static String cleanTagPath(String tagPath) {
        return tagPath.replaceAll("[(),]", "").toLowerCase();
    }
}
