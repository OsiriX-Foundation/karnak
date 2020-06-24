package org.karnak.standard;

import org.karnak.standard.dicominnolitics.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StandardDICOM {
    private static StandardSOPS standardSOPS;
    private static StandardCIODS standardCIODS;
    private static StandardCIODtoModules standardCIODtoModules;
    private static StandardModuleToAttributes standardModuleToAttributes;

    private static HashMap<String, HashMap<String, jsonModuleToAttribute>> HMapModuleToAttributes;
    private static HashMap<String, HashMap<String, jsonCIODtoModule>> HMapCIODtoModules;
    private static HashMap<String, jsonCIOD> HMapCIODS;
    private static HashMap<String, jsonSOP> HMapSOPS;

    public StandardDICOM() {
        standardSOPS = new StandardSOPS();
        standardCIODS = new StandardCIODS();
        standardCIODtoModules = new StandardCIODtoModules();
        standardModuleToAttributes = new StandardModuleToAttributes();

        HMapModuleToAttributes = generateHMapAttribute(standardModuleToAttributes.getModuleToAttributes());
        HMapCIODtoModules = generateHMapCIODtoModule(standardCIODtoModules.getCIODToModules());
        HMapCIODS = generateHMapCIOD(standardCIODS.getCIODS());
        HMapSOPS = generateHMapSOP(standardSOPS.getSOPS());

        getAttributesSOP("1.2.840.10008.5.1.4.1.1.2");
    }

    private HashMap<String, HashMap<String, jsonModuleToAttribute>> generateHMapAttribute(jsonModuleToAttribute[] moduleToAttributes) {
        HashMap<String, HashMap<String, jsonModuleToAttribute>> HMapModulesToAttribute = new HashMap<>();
        for (jsonModuleToAttribute moduleToAttribute: moduleToAttributes) {
            String attributePath = moduleToAttribute.getPath();
            String module = splitModuleAttribute(attributePath);

            if (HMapModulesToAttribute.containsKey(module)) {
                HashMap<String, jsonModuleToAttribute> HMapAttribute = HMapModulesToAttribute.get(module);
                HMapAttribute.put(attributePath, moduleToAttribute);
            } else {
                HashMap<String, jsonModuleToAttribute> HMapAttribute = new HashMap<>();
                HMapAttribute.put(attributePath, moduleToAttribute);
                HMapModulesToAttribute.put(module, HMapAttribute);
            }
        }
        return HMapModulesToAttribute;
    }

    private String splitModuleAttribute(String moduleAttribute) {
        String[] moduleAttributesSplit = moduleAttribute.split(":", 2);
        String module = moduleAttributesSplit[0];
        String[] attributesSplit = moduleAttributesSplit[1].split(":");
        List<String> attributesList = Arrays.asList(attributesSplit);
        return module;
    }

    private HashMap<String, HashMap<String, jsonCIODtoModule>> generateHMapCIODtoModule(jsonCIODtoModule[] ciodToModules) {
        HashMap<String, HashMap<String, jsonCIODtoModule>> HMapCIODtoModule = new HashMap<>();
        for (jsonCIODtoModule ciodToModule: ciodToModules) {
            String ciodKey = ciodToModule.getCiodId();
            String moduleKey = ciodToModule.getModuleId();

            if (HMapCIODtoModule.containsKey(ciodKey)) {
                HashMap<String, jsonCIODtoModule> HMapModule = HMapCIODtoModule.get(ciodKey);
                HMapModule.put(moduleKey, ciodToModule);
            } else {
                HashMap<String, jsonCIODtoModule> HMapModule = new HashMap<>();
                HMapModule.put(moduleKey, ciodToModule);
                HMapCIODtoModule.put(ciodKey, HMapModule);
            }
        }
        return HMapCIODtoModule;
    }

    private HashMap<String, jsonCIOD> generateHMapCIOD(jsonCIOD[] ciods) {
        HashMap<String, jsonCIOD> HMapCIOD = new HashMap<>();
        for (jsonCIOD ciod: ciods) {
            HMapCIOD.put(ciod.getName(), ciod);
        }
        return HMapCIOD;
    }

    private HashMap<String, jsonSOP> generateHMapSOP(jsonSOP[] sops) {
        HashMap<String, jsonSOP> HMapSOP = new HashMap<>();
        for (jsonSOP sop: sops) {
            HMapSOP.put(sop.getId(), sop);
        }
        return HMapSOP;
    }

    public jsonSOP getSOP(String SOPclassUID) {
        if (HMapSOPS.containsKey(SOPclassUID)) {
            return HMapSOPS.get(SOPclassUID);
        }
        return null;
    }

    public jsonCIOD getCIOD(String CIODname) {
        if (HMapCIODS.containsKey(CIODname)) {
            return HMapCIODS.get(CIODname);
        }
        return null;
    }

    public HashMap<String, jsonCIODtoModule> getHMapCIODtoModule(String CIODname) {
        if (HMapCIODtoModules.containsKey(CIODname)) {
            return HMapCIODtoModules.get(CIODname);
        }
        return null;
    }

    public HashMap<String, jsonModuleToAttribute> getHMapModuleToAttribute(String moduleID) {
        if (HMapModuleToAttributes.containsKey(moduleID)) {
            return HMapModuleToAttributes.get(moduleID);
        }
        return null;
    }

    public void getAttributesSOP(String SOPclassUID) {

        jsonSOP sop = getSOP(SOPclassUID);
        if (sop != null) {
            jsonCIOD ciod = getCIOD(sop.getCiod());
            if (ciod != null) {
                HashMap<String, jsonCIODtoModule> modulesInCIOD = getHMapCIODtoModule(ciod.getId());
                if (modulesInCIOD != null) {
                    for (String module: modulesInCIOD.keySet()) {
                        HashMap<String, jsonModuleToAttribute> attributes = getHMapModuleToAttribute(module);
                        if (attributes != null) {
                            for (String attributePath: attributes.keySet()) {
                                System.out.println(attributePath);
                            }
                        }

                    }
                }
            }
        }
    }
}
