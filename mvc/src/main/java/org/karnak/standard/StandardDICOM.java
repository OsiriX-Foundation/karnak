package org.karnak.standard;

import org.karnak.standard.dicominnolitics.*;

import java.util.HashMap;
import java.util.stream.Stream;

public class StandardDICOM {
    private static StandardSOPS standardSOPS;
    private static StandardCIODS standardCIODS;
    private static StandardCIODtoModules standardCIODtoModules;
    private static StandardModuleToAttributes standardModuleToAttributes;

    private static HashMap<String, ModuleToAttribute> HMapAttributes;
    private static HashMap<String, HashMap<String, CIODtoModule>> HMapCIODtoModules;
    private static HashMap<String, CIOD> HMapCIODS;
    private static HashMap<String, SOP> HMapSOPS;

    public StandardDICOM() {
        standardSOPS = new StandardSOPS();
        standardCIODS = new StandardCIODS();
        standardCIODtoModules = new StandardCIODtoModules();
        standardModuleToAttributes = new StandardModuleToAttributes();

        HMapAttributes = generateHMapAttribute(standardModuleToAttributes.getModuleToAttributes());
        HMapCIODtoModules = generateHMapCIODtoModule(standardCIODtoModules.getCIODToModules());
        HMapCIODS = generateHMapCIOD(standardCIODS.getCIODS());
        HMapSOPS = generateHMapSOP(standardSOPS.getSOPS());

        getSomeThing("1.2.840.10008.5.1.4.1.1.2");
    }

    private HashMap<String, ModuleToAttribute> generateHMapAttribute(ModuleToAttribute[] moduleToAttributes) {
        HashMap<String, ModuleToAttribute> HMapAttribute = new HashMap<>();
        for (ModuleToAttribute moduleToAttribute: moduleToAttributes) {
            HMapAttribute.put(moduleToAttribute.getPath(), moduleToAttribute);
        }
        return HMapAttribute;
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

    public void getSomeThing(String SOPclassUID) {
        if (HMapSOPS.containsKey(SOPclassUID)) {
            SOP sop = HMapSOPS.get(SOPclassUID);
            if (HMapCIODS.containsKey(sop.getCiod())) {
                CIOD ciod = HMapCIODS.get(sop.getCiod());
                if (HMapCIODtoModules.containsKey(ciod.getId())) {
                    HashMap<String, CIODtoModule> ciodtoModules = HMapCIODtoModules.get(ciod.getId());
                    System.out.println("hello");
                }
            }
        }
    }
}
