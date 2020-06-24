package org.karnak.standard;

import org.karnak.standard.dicominnolitics.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SOPS {
    private static HashMap<String, SOP> HMapSOPS;

    public SOPS(jsonSOP[] sops, jsonCIOD[] ciods, jsonCIODtoModule[] ciodToModule) {
        HashMap<String, String> HMapCIODS = initializeCIODS(ciods);
        HashMap<String, ArrayList<Module>> HMapCIODModules = initializeHMapCIODModules(ciodToModule);
        HMapSOPS = initializeSOPS(sops, HMapCIODS, HMapCIODModules);
    }

    private HashMap<String, SOP> initializeSOPS(jsonSOP[] sops, HashMap<String, String> HMapCIODS, HashMap<String, ArrayList<Module>> HMapCIODModules) {
        HashMap<String, SOP> HMapSOPS = new HashMap<>();
        for (jsonSOP sop: sops) {
            String ciod_id = HMapCIODS.get(sop.getCiod());
            ArrayList<Module> modules = HMapCIODModules.get(ciod_id);
            SOP newSOP = new SOP(sop.getId(), sop.getName(), sop.getCiod(), ciod_id, modules);
            HMapSOPS.put(sop.getId(), newSOP);
        }
        return HMapSOPS;
    }

    private HashMap<String, String> initializeCIODS(jsonCIOD[] ciods) {
        HashMap<String, String> HMapCIODS = new HashMap<>();
        for (jsonCIOD ciod: ciods) {
            HMapCIODS.put(ciod.getName(), ciod.getId());
        }
        return HMapCIODS;
    }

    private HashMap<String, ArrayList<Module>> initializeHMapCIODModules(jsonCIODtoModule[] ciodToModules) {
        HashMap<String, ArrayList<Module>> HMapCIODModules = new HashMap<>();

        for (jsonCIODtoModule ciodToModule: ciodToModules) {
            String ciod_id = ciodToModule.getCiodId();
            Module module = new Module(ciodToModule.getModuleId(), ciodToModule.getUsage(), ciodToModule.getInformationEntity());
            if (HMapCIODModules.containsKey(ciod_id)) {
                ArrayList<Module> ListModules = HMapCIODModules.get(ciod_id);
                ListModules.add(module);
            } else {
                ArrayList<Module> ListModules = new ArrayList<>();
                ListModules.add(module);
                HMapCIODModules.put(ciod_id, ListModules);
            }
        }
        return HMapCIODModules;
    }

    public ArrayList<String> getAllUIDs() {
        ArrayList<String> uidList = new ArrayList<>(HMapSOPS.keySet());
        return uidList;
    }

    public SOP getSOP(String uid) {
        SOP sop = HMapSOPS.get(uid);
        return sop;
    }

    public String getName(String uid) {
        SOP sop = HMapSOPS.get(uid);
        return sop.getName();
    }

    public String getCIOD(String uid) {
        SOP sop = HMapSOPS.get(uid);
        return sop.getCiod();
    }

    public String getIdCIOD(String uid) {
        SOP sop = HMapSOPS.get(uid);
        return sop.getCiod_id();
    }

    public ArrayList<Module> getSOPmodules(String uid) {
        SOP sop = HMapSOPS.get(uid);
        return sop.getModules();
    }

    public List<String> getSOPmodulesName(String uid) {
        SOP sop = HMapSOPS.get(uid);
        return sop.getModules().stream()
                .map(module -> module.getId())
                .collect(Collectors.toList());
    }

    public Boolean moduleIsPresent(String uid, String moduleId) {
        SOP sop = HMapSOPS.get(uid);
        Predicate<Module> modulePredicate = module -> moduleId.equals(module.getId());
        return sop.getModules().stream().anyMatch(modulePredicate);
    }
}
