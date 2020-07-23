package org.karnak.profilepipe.profilebody;

import java.util.List;

public class ProfilePipeBody {
    private String name;
    private String version;
    private String minimumKarnakVersion;
    private String defaultIssuerOfPatientID;
    private List<ProfileElementBody> profiles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMinimumKarnakVersion() {
        return minimumKarnakVersion;
    }

    public void setMinimumKarnakVersion(String minimumKarnakVersion) {
        this.minimumKarnakVersion = minimumKarnakVersion;
    }

    public List<ProfileElementBody> getProfileElements() {
        return profiles;
    }

    public void setProfileElements(List<ProfileElementBody> profiles) {
        this.profiles = profiles;
    }

    public String getDefaultIssuerOfPatientID() {
        return defaultIssuerOfPatientID;
    }

    public void setDefaultIssuerOfPatientID(String defaultIssuerOfPatientID) {
        this.defaultIssuerOfPatientID = defaultIssuerOfPatientID;
    }
}
