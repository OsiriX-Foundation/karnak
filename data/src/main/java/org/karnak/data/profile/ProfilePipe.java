package org.karnak.data.profile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ProfilePipe")
@Table(name = "profile_pipe")
public class ProfilePipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String version;
    private String karnakversionmin;
    private String defaultissueropatientid;

    @OneToMany(mappedBy = "profilePipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Profile> profiles = new ArrayList<>();

    public ProfilePipe() {
    }

    public ProfilePipe(String name, String version, String karnakversionmin, String defaultissueropatientid) {
        this.name = name;
        this.version = version;
        this.karnakversionmin = karnakversionmin;
        this.defaultissueropatientid = defaultissueropatientid;
    }

    public void addProfilePipe(Profile profile){
        this.profiles.add(profile);
    }

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

    public String getKarnakversionmin() {
        return karnakversionmin;
    }

    public void setKarnakversionmin(String karnakversionmin) {
        this.karnakversionmin = karnakversionmin;
    }

    public String getDefaultissueropatientid() {
        return defaultissueropatientid;
    }

    public void setDefaultissueropatientid(String defaultissueropatientid) {
        this.defaultissueropatientid = defaultissueropatientid;
    }

   /* public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }*/
}
