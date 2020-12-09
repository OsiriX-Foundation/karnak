package org.karnak.cache;

import java.time.LocalDate;

public interface PseudonymPatient {
    public String getPseudonym();

    public String getPatientId();

    public String getPatientName();

    public String getIssuerOfPatientId();

    public LocalDate getPatientBirthDate();

    public String getPatientSex();
}
