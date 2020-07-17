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
    private String minimumkarnakversion;
    private String defaultissueropatientid;
    private Boolean bydefault;

    @OneToMany(mappedBy = "profilePipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Profile> profiles = new ArrayList<>();

    public ProfilePipe() {
    }

    public ProfilePipe(String name, String version, String minimumkarnakversion, String defaultissueropatientid) {
        this.name = name;
        this.version = version;
        this.minimumkarnakversion = minimumkarnakversion;
        this.defaultissueropatientid = defaultissueropatientid;
        this.bydefault = false;
    }

    public ProfilePipe(String name, String version, String minimumkarnakversion, String defaultissueropatientid, Boolean bydefault) {
        this.name = name;
        this.version = version;
        this.minimumkarnakversion = minimumkarnakversion;
        this.defaultissueropatientid = defaultissueropatientid;
        this.bydefault = bydefault;
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

    public String getMinimumkarnakversion() {
        return minimumkarnakversion;
    }

    public void setMinimumkarnakversion(String minimumkarnakversion) {
        this.minimumkarnakversion = minimumkarnakversion;
    }

    public String getDefaultissueropatientid() {
        return defaultissueropatientid;
    }

    public void setDefaultissueropatientid(String defaultissueropatientid) {
        this.defaultissueropatientid = defaultissueropatientid;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public Long getId() {
        return id;
    }
}
