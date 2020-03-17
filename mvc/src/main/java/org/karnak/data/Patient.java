package org.karnak.data;

public class Patient {

    // ---------------------------------------------------------------
    // Fields  -------------------------------------------------------
    // ---------------------------------------------------------------
    private String pseudonym;
    private String firstname;
    private String lastname;
    private String geburtsname;
    private String geburtstag;
    private String geburtsmonat;
    private String geburtsjahr;
    private String plz;
    private String ort;
    private String test;

    // ---------------------------------------------------------------
    // Getters/Setters  ------------------------------------------------
    // ---------------------------------------------------------------
    public String get_pseudonym() { return this.pseudonym; }
    public String get_firstname() { return this.firstname; }
    public String get_lastname() { return this.lastname; }
    public String get_geburtsname() { return this.geburtsname; }
    public String get_geburtstag() { return this.geburtstag; }
    public String get_geburtsmonat() { return this.geburtsmonat; }
    public String get_geburtsjahr() { return this.geburtsjahr; }
    public String get_plz() { return this.plz; }
    public String get_ort() { return this.ort; }
    public String get_test() { return this.test; }

    public void set_pseudonym(String pseudonym) { this.pseudonym = pseudonym; }
    public void set_firstname(String firstname) { this.firstname = firstname; }
    public void set_lastname(String lastname) { this.lastname = lastname; }
    public void set_geburtsname(String geburtsname) { this.geburtsname = geburtsname; }
    public void set_geburtstag(String geburtstag) { this.geburtstag = geburtstag; }
    public void set_geburtsmonat(String geburtsmonat) { this.geburtsmonat = geburtsmonat; }
    public void set_geburtsjahr(String geburtsjahr) { this.geburtsjahr = geburtsjahr; }
    public void set_plz(String plz) { this.plz = plz; }
    public void set_ort(String ort) { this.ort = ort; }
    public void set_test(String test) { this.test = test; }

    // ---------------------------------------------------------------
    // Constructors  ------------------------------------------------
    // ---------------------------------------------------------------

    public Patient(String pseudonym, String firstname, String lastname, String geburtstag, String geburtsmonat, String geburtsjahr)
    {
        this.pseudonym = "";
        this.firstname = "";
        this.lastname ="";
        this.geburtstag= "";
        this.geburtsmonat= "";
        this.geburtsjahr= "";
    }

    public Patient(String pseudonym, String firstname, String lastname, String geburtsname, String geburtstag, String geburtsmonat, String geburtsjahr,
                        String plz, String ort, String test)
    {
        this.pseudonym = "";
        this.firstname = "";
        this.lastname ="";
        this.geburtsname= "";
        this.geburtstag= "";
        this.geburtsmonat= "";
        this.geburtsjahr= "";
        this.plz= "";
        this.ort= "";
        this.test= "";
    }

    // ---------------------------------------------------------------
    // Methods  ------------------------------------------------------
    // ---------------------------------------------------------------
}
