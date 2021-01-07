package org.karnak.backend.model.standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.karnak.backend.exception.SOPNotFoundException;
import org.karnak.backend.model.dicominnolitics.StandardCIODS;
import org.karnak.backend.model.dicominnolitics.StandardCIODtoModules;
import org.karnak.backend.model.dicominnolitics.StandardSOPS;
import org.karnak.backend.model.dicominnolitics.jsonCIOD;
import org.karnak.backend.model.dicominnolitics.jsonCIODtoModule;
import org.karnak.backend.model.dicominnolitics.jsonSOP;

public class SOPS {
    private static HashMap<String, SOP> HMapSOPS;

    public SOPS() {
        HashMap<String, String> HMapCIODS = initializeCIODS(StandardCIODS.readJsonCIODS());
        HashMap<String, ArrayList<Module>> HMapCIODModules = initializeHMapCIODModules(StandardCIODtoModules.readJsonCIODToModules());
        HMapSOPS = initializeSOPS(StandardSOPS.readJsonSOPS(), HMapCIODS, HMapCIODModules);
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

    public String getName(String uid) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get name. Could not find the SOP UID %s", uid));
        }
        return sop.getName();
    }

    public String getCIOD(String uid) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get CIOD. Could not find the SOP UID %s", uid));
        }
        return sop.getCiod();
    }

    public String getIdCIOD(String uid) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get ID CIOD. Could not find the SOP UID %s", uid));
        }
        return sop.getCiod_id();
    }

    public Optional<Module> getModuleByModuleID(String uid, String moduleId) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get if module %s is present. Could not find the SOP UID %s", moduleId, uid));
        }
        return sop.getModules().stream()
                .filter(module -> moduleId.equals(module.getId()))
                .findFirst();
    }

    public ArrayList<Module> getSOPmodules(String uid) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get SOP modules. Could not find the SOP UID %s", uid));
        }
        return sop.getModules();
    }

    public List<String> getSOPmodulesName(String uid) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get SOP modules name. Could not find the SOP UID %s", uid));
        }
        return sop.getModules().stream()
                .map(module -> module.getId())
                .collect(Collectors.toList());
    }

    public Boolean moduleIsPresent(String uid, String moduleId) throws SOPNotFoundException {
        SOP sop = HMapSOPS.get(uid);
        if (sop == null) {
            throw new SOPNotFoundException(String.format("Unable to get if module %s is present. Could not find the SOP UID %s", moduleId, uid));
        }
        Predicate<Module> modulePredicate = module -> moduleId.equals(module.getId());
        return sop.getModules().stream().anyMatch(modulePredicate);
    }

    public Map<Module, Map<String, Attribute>> getModuleToAttribute(String uid, ModuleToAttributes moduleToAttributes) throws SOPNotFoundException {
        Map<Module, Map<String, Attribute>> HMapModuleAttributes = new HashMap<>();
        try {
            getSOPmodules(uid).forEach(module -> {
                HMapModuleAttributes.put(module, moduleToAttributes.getAttributesByModule(module.getId()));
            });
        } catch (SOPNotFoundException sopNotFoundException) {
            throw new SOPNotFoundException(String.format("Unable to get module attributes. Could not find the SOP UID %s", uid));
        }
        return HMapModuleAttributes;
    }
}
