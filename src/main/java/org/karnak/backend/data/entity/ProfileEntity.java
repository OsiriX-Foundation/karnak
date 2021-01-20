package org.karnak.backend.data.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "Profile")
@Table(name = "profile")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "version", "minimumKarnakVersion", "defaultIssuerOfPatientID",
    "profileElementEntities", "maskEntities"})
public class ProfileEntity implements Serializable {

    private static final long serialVersionUID = -7178858361090900170L;

    private Long id;
    private String name;
    private String version;
    private String minimumKarnakVersion;
    private String defaultIssuerOfPatientId;
    private Boolean byDefault;
    private List<ProfileElementEntity> profileElementEntities = new ArrayList<>();
    private Set<MaskEntity> maskEntities = new HashSet<>();
    private List<ProjectEntity> projectEntities;

    public ProfileEntity() {
    }

    public ProfileEntity(String name, String version, String minimumKarnakVersion,
        String defaultIssuerOfPatientId) {
        this.name = name;
        this.version = version;
        this.minimumKarnakVersion = minimumKarnakVersion;
        this.defaultIssuerOfPatientId = defaultIssuerOfPatientId;
        this.byDefault = false;
    }

    public ProfileEntity(String name, String version, String minimumKarnakVersion,
        String defaultIssuerOfPatientId, Boolean byDefault) {
        this.name = name;
        this.version = version;
        this.minimumKarnakVersion = minimumKarnakVersion;
        this.defaultIssuerOfPatientId = defaultIssuerOfPatientId;
        this.byDefault = byDefault;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addProfilePipe(ProfileElementEntity profileElementEntity) {
        this.profileElementEntities.add(profileElementEntity);
    }

    public void addMask(MaskEntity maskEntity) {
        this.maskEntities.add(maskEntity);
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

    @JsonGetter("minimumKarnakVersion")
    public String getMinimumKarnakVersion() {
        return minimumKarnakVersion;
    }

    @JsonSetter("minimumKarnakVersion")
    public void setMinimumKarnakVersion(String minimumKarnakVersion) {
        this.minimumKarnakVersion = minimumKarnakVersion;
    }

    @JsonGetter("defaultIssuerOfPatientID")
    @Column(name = "defaultissueropatientid")
    public String getDefaultIssuerOfPatientId() {
        return defaultIssuerOfPatientId;
    }

    @JsonSetter("defaultIssuerOfPatientID")
    public void setDefaultIssuerOfPatientId(String defaultIssuerOfPatientId) {
        this.defaultIssuerOfPatientId = defaultIssuerOfPatientId;
    }

    @OneToMany(mappedBy = "profileEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<ProfileElementEntity> getProfileElementEntities() {
        return profileElementEntities;
    }

    public void setProfileElementEntities(List<ProfileElementEntity> profileElementEntities) {
        this.profileElementEntities = profileElementEntities;
    }

    @JsonIgnore
    @Column(name = "bydefault")
    public Boolean getByDefault() {
        return byDefault;
    }

    public void setByDefault(Boolean bydefault) {
        this.byDefault = bydefault;
    }

    @OneToMany(mappedBy = "profileEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<MaskEntity> getMaskEntities() {
        return maskEntities;
    }

    public void setMaskEntities(Set<MaskEntity> maskEntities) {
        this.maskEntities = maskEntities;
    }

    @OneToMany(mappedBy = "profileEntity")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    public List<ProjectEntity> getProjectEntities() {
        return projectEntities;
    }

    public void setProjectEntities(List<ProjectEntity> projectEntities) {
        this.projectEntities = projectEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProfileEntity that = (ProfileEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(version, that.version) &&
            Objects.equals(minimumKarnakVersion, that.minimumKarnakVersion) &&
            Objects.equals(defaultIssuerOfPatientId, that.defaultIssuerOfPatientId) &&
            Objects.equals(byDefault, that.byDefault) &&
            Objects.equals(profileElementEntities, that.profileElementEntities) &&
            Objects.equals(maskEntities, that.maskEntities) &&
            Objects.equals(projectEntities, that.projectEntities);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(id, name, version, minimumKarnakVersion, defaultIssuerOfPatientId, byDefault,
                profileElementEntities, maskEntities, projectEntities);
    }
}
