package org.karnak.standard;

import org.karnak.data.gateway.SOPClassUIDPersistence;
import org.karnak.standard.dicominnolitics.*;
import org.karnak.ui.data.GatewayConfiguration;
import org.w3c.dom.Attr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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

        ArrayList<String> allUIDs = sops.getAllUIDs();
        SOP sop = sops.getSOP("1.2.840.10008.5.1.4.1.1.2");
        String ciod = sops.getCIOD("1.2.840.10008.5.1.4.1.1.2");
        String ciod_id = sops.getIdCIOD("1.2.840.10008.5.1.4.1.1.2");
        ArrayList<Module> modules = sops.getSOPmodules("1.2.840.10008.5.1.4.1.1.2");
        List<String> modulesName = sops.getSOPmodulesName("1.2.840.10008.5.1.4.1.1.2");
        boolean notpresent = sops.moduleIsPresent("1.2.840.10008.5.1.4.1.1.2", "patient123");
        boolean present = sops.moduleIsPresent("1.2.840.10008.5.1.4.1.1.2", "patient");

        List<Attribute> modulesAttributes = moduleToAttributes.getAttributesByModule("patient");
        List<Attribute> test = getAttributesBySOP("1.2.840.10008.5.1.4.1.1.2", "0008,0008");
    }

    public Map<Module, List<Attribute>> getModulesBySOP(String sopUID) {
        return sops.getModuleToAttribute(sopUID, moduleToAttributes);
    }

    public List<Attribute> getAttributesBySOP(String sopUID, String tagPath) {
        String tagPathCleaned = cleanTagPath(tagPath);
        Map<Module, List<Attribute>> HMapModuleAttributes = getModulesBySOP(sopUID);
        List<Attribute> attributes = new ArrayList<>();
        HMapModuleAttributes.forEach((module, attributeList) -> {
            Attribute attribute = attributeList.stream()
                    .filter(attr -> attr.getTagPath().equals(tagPathCleaned))
                    .findAny().orElse(null);
            if (attribute != null) {
                attributes.add(attribute);
            }
        });
        return attributes;
    }

    public List<Attribute> getAttributesByModule(String moduleId) {
        return moduleToAttributes.getAttributesByModule(moduleId);
    }

    public static String cleanTagPath(String tagPath) {
        return tagPath.replaceAll("[(),]", "").toLowerCase();
    }
}
