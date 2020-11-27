package org.karnak.data.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.karnak.data.gateway.Project;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Profile")
@Table(name = "profile")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "version", "minimumKarnakVersion", "defaultIssuerOfPatientID","profileElements", "masks"})
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    private String name;
    private String version;
    private String minimumKarnakVersion;
    private String defaultissueropatientid;

    @JsonIgnore
    private Boolean bydefault;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ProfileElement> profileElements = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Mask> masks = new HashSet<>();

    @OneToMany(mappedBy="profile")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<Project> project;

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

    public Long getId() {
        return id;
    }
    public void addProfilePipe(ProfileElement profileElement){
        this.profileElements.add(profileElement);
    }

    public void addMask(Mask mask){
        this.masks.add(mask);
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

    @JsonProperty("minimumKarnakVersion")
    public String getMinimumkarnakversion() {
        return minimumKarnakVersion;
    }

    public void setMinimumkarnakversion(String minimumKarnakVersion) {
        this.minimumKarnakVersion = minimumKarnakVersion;
    }

    @JsonProperty("defaultIssuerOfPatientID")
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

    public Set<Mask> getMasks() {
        return masks;
    }

    public void setMasks(Set<Mask> masks) {
        this.masks = masks;
    }

    public List<Project> getProject() {
        return project;
    }
}
