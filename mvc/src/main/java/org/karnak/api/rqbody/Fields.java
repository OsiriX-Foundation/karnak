package org.karnak.api.rqbody;

public class Fields {

    // ---------------------------------------------------------------
    // Fields  -------------------------------------------------------
    // ---------------------------------------------------------------
    private String patientID;
    private String patientName;
    private String patientBirthDate;
    private String patientSex;
    private String issuerOfPatientID;

    // ---------------------------------------------------------------
    // Getters/Setters  ------------------------------------------------
    // ---------------------------------------------------------------
    public String get_patientID() { return this.patientID; }
    public String get_patientName() { return this.patientName; }
    public String get_patientBirthDate() { return this.patientBirthDate; }
    public String get_patientSex() { return this.patientSex; }
    public String get_issuerOfPatientID() { return this.issuerOfPatientID; }

    public void set_patientID(String patientID) { this.patientID = patientID; }
    public void set_patientName(String patientName) { this.patientName = patientName; }
    public void set_patientBirthDate(String patientBirthDate) { this.patientBirthDate = patientBirthDate; }
    public void set_patientSex(String patientSex) { this.patientSex = patientSex; }
    public void set_issuerOfPatientID(String issuerOfPatientID) { this.issuerOfPatientID = issuerOfPatientID; }

    // ---------------------------------------------------------------
    // Constructors  ------------------------------------------------
    // ---------------------------------------------------------------
    public Fields(String patientID)
    {
        this.patientID = patientID;
    }
    public Fields(String patientID, String patientName, String patientBirthDate, String patientSex, String issuerOfPatientID)
    {
        this.patientID = patientID;
        this.patientName =patientName;
        this.patientBirthDate= patientBirthDate;
        this.patientSex = patientSex;
        this.issuerOfPatientID = issuerOfPatientID;
    }
}
