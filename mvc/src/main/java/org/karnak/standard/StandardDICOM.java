package org.karnak.standard;

import org.karnak.data.gateway.SOPClassUIDPersistence;
import org.karnak.standard.dicominnolitics.*;
import org.karnak.ui.data.GatewayConfiguration;

import java.util.ArrayList;
import java.util.List;

public class StandardDICOM {
    private static StandardSOPS standardSOPS;
    private static StandardCIODS standardCIODS;
    private static StandardCIODtoModules standardCIODtoModules;
    private static StandardModuleToAttributes standardModuleToAttributes;

    private SOPClassUIDPersistence sopClassUIDPersistence = GatewayConfiguration.getInstance().getSopClassUIDPersistence();

    public StandardDICOM() {
        standardSOPS = new StandardSOPS();
        standardCIODS = new StandardCIODS();
        standardCIODtoModules = new StandardCIODtoModules();
        standardModuleToAttributes = new StandardModuleToAttributes();

        SOPS sops = new SOPS(standardSOPS.getSOPS(), standardCIODS.getCIODS(), standardCIODtoModules.getCIODToModules());
        Attributes attributes = new Attributes(standardModuleToAttributes.getModuleToAttributes());
        ArrayList<String> allUIDs = sops.getAllUIDs();
        SOP sop = sops.getSOP("1.2.840.10008.5.1.4.1.1.2");
        String ciod = sops.getCIOD("1.2.840.10008.5.1.4.1.1.2");
        String ciod_id = sops.getIdCIOD("1.2.840.10008.5.1.4.1.1.2");
        ArrayList<Module> modules = sops.getSOPmodules("1.2.840.10008.5.1.4.1.1.2");
        List<String> modulesName = sops.getSOPmodulesName("1.2.840.10008.5.1.4.1.1.2");
        boolean notpresent = sops.moduleIsPresent("1.2.840.10008.5.1.4.1.1.2", "patient123");
        boolean present = sops.moduleIsPresent("1.2.840.10008.5.1.4.1.1.2", "patient");

        List<Attribute> moduleAttributes = attributes.getAttributesByModule("patient");
    }
}
