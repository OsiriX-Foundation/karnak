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
    private String codename;
    private String action;
    private Integer position;

    @ManyToOne()
    @JoinColumn(name = "profile_pipe_id", nullable = false)
    private ProfilePipe profilePipe;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<IncludedTag> includedtag = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ExceptedTag> exceptedtags = new ArrayList<>();

    public Profile() {
    }

    public Profile(String name, String codename, String action, Integer position, ProfilePipe profilePipe) {
        this.name = name;
        this.codename = codename;
        this.action = action;
        this.position = position;
        this.profilePipe = profilePipe;
    }

    public void addIncludedTag(IncludedTag includedtag){
        this.includedtag.add(includedtag);
    }

    public void addExceptedtags(ExceptedTag exceptedtags){
        this.exceptedtags.add(exceptedtags);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodename() {
        return codename;
    }

    public void setCodename(String codename) {
        this.codename = codename;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public ProfilePipe getProfilePipe() {
        return profilePipe;
    }

    public void setProfilePipe(ProfilePipe profilePipe) {
        this.profilePipe = profilePipe;
    }

    public List<IncludedTag> getIncludedtag() {
        return includedtag;
    }

    public void setIncludedtag(List<IncludedTag> includedtag) {
        this.includedtag = includedtag;
    }

    public List<ExceptedTag> getExceptedtags() {
        return exceptedtags;
    }

    public void setExceptedtags(List<ExceptedTag> exceptedtags) {
        this.exceptedtags = exceptedtags;
    }
}
