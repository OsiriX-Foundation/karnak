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

    private static HashMap<String, HashMap<String, ModuleToAttribute>> HMapModuleToAttributes;
    private static HashMap<String, HashMap<String, CIODtoModule>> HMapCIODtoModules;
    private static HashMap<String, CIOD> HMapCIODS;
    private static HashMap<String, SOP> HMapSOPS;

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

    private HashMap<String, HashMap<String, ModuleToAttribute>> generateHMapAttribute(ModuleToAttribute[] moduleToAttributes) {
        HashMap<String, HashMap<String, ModuleToAttribute>> HMapModulesToAttribute = new HashMap<>();
        for (ModuleToAttribute moduleToAttribute: moduleToAttributes) {
            String attributePath = moduleToAttribute.getPath();
            String module = splitModuleAttribute(attributePath);

            if (HMapModulesToAttribute.containsKey(module)) {
                HashMap<String, ModuleToAttribute> HMapAttribute = HMapModulesToAttribute.get(module);
                HMapAttribute.put(attributePath, moduleToAttribute);
            } else {
                HashMap<String, ModuleToAttribute> HMapAttribute = new HashMap<>();
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

    private HashMap<String, HashMap<String, CIODtoModule>> generateHMapCIODtoModule(CIODtoModule[] ciodToModules) {
        HashMap<String, HashMap<String, CIODtoModule>> HMapCIODtoModule = new HashMap<>();
        for (CIODtoModule ciodToModule: ciodToModules) {
            String ciodKey = ciodToModule.getCiodId();
            String moduleKey = ciodToModule.getModuleId();

            if (HMapCIODtoModule.containsKey(ciodKey)) {
                HashMap<String, CIODtoModule> HMapModule = HMapCIODtoModule.get(ciodKey);
                HMapModule.put(moduleKey, ciodToModule);
            } else {
                HashMap<String, CIODtoModule> HMapModule = new HashMap<>();
                HMapModule.put(moduleKey, ciodToModule);
                HMapCIODtoModule.put(ciodKey, HMapModule);
            }
        }
        return HMapCIODtoModule;
    }

    private HashMap<String, CIOD> generateHMapCIOD(CIOD[] ciods) {
        HashMap<String, CIOD> HMapCIOD = new HashMap<>();
        for (CIOD ciod: ciods) {
            HMapCIOD.put(ciod.getName(), ciod);
        }
        return HMapCIOD;
    }

    private HashMap<String, SOP> generateHMapSOP(SOP[] sops) {
        HashMap<String, SOP> HMapSOP = new HashMap<>();
        for (SOP sop: sops) {
            HMapSOP.put(sop.getId(), sop);
        }
        return HMapSOP;
    }

    public SOP getSOP(String SOPclassUID) {
        if (HMapSOPS.containsKey(SOPclassUID)) {
            return HMapSOPS.get(SOPclassUID);
        }
        return null;
    }

    public CIOD getCIOD(String CIODname) {
        if (HMapCIODS.containsKey(CIODname)) {
            return HMapCIODS.get(CIODname);
        }
        return null;
    }

    public HashMap<String, CIODtoModule> getHMapCIODtoModule(String CIODname) {
        if (HMapCIODtoModules.containsKey(CIODname)) {
            return HMapCIODtoModules.get(CIODname);
        }
        return null;
    }

    public HashMap<String, ModuleToAttribute> getHMapModuleToAttribute(String moduleID) {
        if (HMapModuleToAttributes.containsKey(moduleID)) {
            return HMapModuleToAttributes.get(moduleID);
        }
        return null;
    }

    public void getAttributesSOP(String SOPclassUID) {

        SOP sop = getSOP(SOPclassUID);
        if (sop != null) {
            CIOD ciod = getCIOD(sop.getCiod());
            if (ciod != null) {
                HashMap<String, CIODtoModule> modulesInCIOD = getHMapCIODtoModule(ciod.getId());
                if (modulesInCIOD != null) {
                    for (String module: modulesInCIOD.keySet()) {
                        HashMap<String, ModuleToAttribute> attributes = getHMapModuleToAttribute(module);
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
