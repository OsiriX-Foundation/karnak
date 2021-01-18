package org.karnak.backend.data.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "Project")
@Table(name = "project")
public class ProjectEntity implements Serializable {

    private static final long serialVersionUID = 8809562914582842501L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private byte[] secret;

    @OneToMany(mappedBy = "projectEntity")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<DestinationEntity> destinationEntities;

    @ManyToOne
    @JoinColumn(name = "profile_pipe_id")
    private ProfileEntity profileEntity;

    public ProjectEntity() {
        this.destinationEntities = new ArrayList<>();
    }

    public ProjectEntity(String name, byte[] secret) {
        this.name = name;
        this.secret = secret;
        this.destinationEntities = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getSecret() {
        return secret;
    }

    public void setSecret(byte[] secret) {
        this.secret = secret;
    }

    public List<DestinationEntity> getDestinationEntities() {
        return destinationEntities;
    }

    public void setDestinationEntities(List<DestinationEntity> destinationEntities) {
        this.destinationEntities = destinationEntities;
    }

    public ProfileEntity getProfileEntity() {
        return profileEntity;
    }

    public void setProfileEntity(ProfileEntity profileEntity) {
        this.profileEntity = profileEntity;
    }

    public boolean isNewData() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectEntity that = (ProjectEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Arrays.equals(secret, that.secret) &&
            Objects.equals(destinationEntities, that.destinationEntities) &&
            Objects.equals(profileEntity, that.profileEntity);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, destinationEntities, profileEntity);
        result = 31 * result + Arrays.hashCode(secret);
        return result;
    }
}
