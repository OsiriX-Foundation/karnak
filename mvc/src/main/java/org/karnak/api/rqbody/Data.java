package org.karnak.api.rqbody;

public class Data {
    private String [] idtypes;
    private Patient fields;
    private Ids ids;
    private String callback;

    public String [] get_idtypes() { return this.idtypes; }
    public Patient get_fields() { return this.fields; }
    public Ids get_ids() { return this.ids; }
    public String get_callback() { return this.callback; }


    public void set_idtypes(String [] idtypes) { this.idtypes = idtypes; }
    public void set_fields(Patient fields) { this.fields = fields; }
    public void set_ids(Ids ids) { this.ids = ids; }
    public void set_callback(String callback) { this.callback = callback; }

    public Data(String [] idtypes, Patient fields, Ids ids, String callback)
    {
        this.idtypes = idtypes;
        this.fields = fields;
        this.ids = ids;
        this.callback = callback;
    }
}