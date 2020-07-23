package org.karnak.data.profile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ProfileElement")
@Table(name = "profile_element")
public class ProfileElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String codename;
    private String action;
    private Integer position;

    @ManyToOne()
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @OneToMany(mappedBy = "profileElement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<IncludedTag> includedtag = new ArrayList<>();

    @OneToMany(mappedBy = "profileElement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ExcludedTag> exceptedtags = new ArrayList<>();

    public ProfileElement() {
    }

    public ProfileElement(String name, String codename, String action, Integer position, Profile profile) {
        this.name = name;
        this.codename = codename;
        this.action = action;
        this.position = position;
        this.profile = profile;
    }

    public void addIncludedTag(IncludedTag includedtag){
        this.includedtag.add(includedtag);
    }

    public void addExceptedtags(ExcludedTag exceptedtags){
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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<IncludedTag> getIncludedtag() {
        return includedtag;
    }

    public void setIncludedtag(List<IncludedTag> includedtag) {
        this.includedtag = includedtag;
    }

    public List<ExcludedTag> getExceptedtags() {
        return exceptedtags;
    }

    public void setExceptedtags(List<ExcludedTag> exceptedtags) {
        this.exceptedtags = exceptedtags;
    }
}
