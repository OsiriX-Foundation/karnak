package org.karnak.backend.api.rqbody;

public class SearchIds {
    private String idType;
    private String idString;

    public String get_idType() { return this.idType; }
    public String get_idString() { return this.idString; }

    public void set_idType(String idType) { this.idType = idType; }
    public void set_idString(String idString) { this.idString = idString; }

    public SearchIds(String idType, String idString)
    {
        this.idType = idType;
        this.idString = idString;
    }

    
}