package org.karnak.data.profile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Profile")
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String version;
    private String minimumKarnakVersion;
    private String defaultissueropatientid;
    private Boolean bydefault;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProfileElement> profileElements = new ArrayList<>();

    public Profile() {
    }

    public Profile(String name, String version, String minimumKarnakVersion, String defaultissueropatientid) {
        this.name = name;
        this.version = version;
        this.minimumKarnakVersion = minimumKarnakVersion;
        this.defaultissueropatientid = defaultissueropatientid;
        this.bydefault = false;
    }

    public Profile(String name, String version, String minimumKarnakVersion, String defaultissueropatientid, Boolean bydefault) {
        this.name = name;
        this.version = version;
        this.minimumKarnakVersion = minimumKarnakVersion;
        this.defaultissueropatientid = defaultissueropatientid;
        this.bydefault = bydefault;
    }

    public void addProfilePipe(ProfileElement profileElement){
        this.profileElements.add(profileElement);
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
        return minimumKarnakVersion;
    }

    public void setMinimumkarnakversion(String minimumKarnakVersion) {
        this.minimumKarnakVersion = minimumKarnakVersion;
    }

    public String getDefaultissueropatientid() {
        return defaultissueropatientid;
    }

    public void setDefaultissueropatientid(String defaultissueropatientid) {
        this.defaultissueropatientid = defaultissueropatientid;
    }

    public List<ProfileElement> getProfileElements() {
        return profileElements;
    }

    public void setProfileElements(List<ProfileElement> profileElements) {
        this.profileElements = profileElements;
    }

    public Boolean getBydefault() {
        return bydefault;
    }

    public void setBydefault(Boolean bydefault) {
        this.bydefault = bydefault;
    }

    public Long getId() {
        return id;
    }
}
