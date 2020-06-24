package org.karnak.standard;

import java.util.ArrayList;

public class SOP {
    private String UID;
    private String name;
    private String ciod;
    private String ciod_id;
    private ArrayList<Module> modules;

    SOP(String UID, String name, String ciod, String ciod_id, ArrayList<Module> modules) {
        this.UID = UID;
        this.name = name;
        this.ciod = ciod;
        this.ciod_id = ciod_id;
        this.modules = modules;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public String getCiod() {
        return ciod;
    }

    public String getCiod_id() {
        return ciod_id;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
