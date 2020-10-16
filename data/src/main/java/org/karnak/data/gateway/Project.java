package org.karnak.data.gateway;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.karnak.data.profile.Profile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Project")
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String secret;

    @OneToMany(mappedBy="project")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Destination> destinations;

    @ManyToOne
    @JoinColumn(name="profile_pipe_id")
    private Profile profile;

    public Project() {
        this.destinations = new ArrayList<>();
    }

    public Project(String name, String secret) {
        this.name = name;
        this.secret = secret;
        this.destinations = new ArrayList<>();
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public boolean isNewData() {
        return id == null;
    }
}
