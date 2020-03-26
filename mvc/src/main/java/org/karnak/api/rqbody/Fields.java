package org.karnak.api.rqbody;

public class Fields {

    // ---------------------------------------------------------------
    // Fields  -------------------------------------------------------
    // ---------------------------------------------------------------
    private String patientID;
    private String patientName;
    private String patientBirthDate;
    private String patientBirthTime;
    private String patientAge;
    private String patientSex;
    private String patientAddress;

    // ---------------------------------------------------------------
    // Getters/Setters  ------------------------------------------------
    // ---------------------------------------------------------------
    public String get_patientID() { return this.patientID; }
    public String get_patientName() { return this.patientName; }
    public String get_patientBirthDate() { return this.patientBirthDate; }
    public String get_patientBirthTime() { return this.patientBirthTime; }
    public String get_patientAge() { return this.patientAge; }
    public String get_patientSex() { return this.patientSex; }
    public String get_patientAddress() { return this.patientAddress; }

    public void set_patientID(String patientID) { this.patientID = patientID; }
    public void set_patientName(String patientName) { this.patientName = patientName; }
    public void set_patientBirthDate(String patientBirthDate) { this.patientBirthDate = patientBirthDate; }
    public void set_patientBirthTime(String patientBirthTime) { this.patientBirthTime = patientBirthTime; }
    public void set_patientAge(String patientAge) { this.patientAge = patientAge; }
    public void set_patientSex(String patientSex) { this.patientSex = patientSex; }
    public void set_patientAddress(String patientAddress) { this.patientAddress = patientAddress; }

    // ---------------------------------------------------------------
    // Constructors  ------------------------------------------------
    // ---------------------------------------------------------------
    public Fields(String patientID)
    {
        this.patientID = patientID;
    }
    public Fields(String patientID, String patientName, String patientBirthDate, String patientBirthTime, String patientAge, String patientSex, String patientAddress)
    {
        this.patientID = patientID;
        this.patientName =patientName;
        this.patientBirthDate= patientBirthDate;
        this.patientBirthTime= patientBirthTime;
        this.patientAge= patientAge;
        this.patientSex = patientSex;
        this.patientAddress = patientAddress;
    }
}
