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
    private List<Tag> tags = new ArrayList<>();

    /*@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Tag> exceptedtags = new ArrayList<>();
    */
    public Profile() {
    }

    public Profile(String name, String codename, String action, Integer position, ProfilePipe profilePipe) {
        this.name = name;
        this.codename = codename;
        this.action = action;
        this.position = position;
        this.profilePipe = profilePipe;
    }

    public void addTag(Tag tag){
        this.tags.add(tag);
    }

    /*public void addExceptedtags(Tag tag){
        //this.exceptedtags.add(tag);
    }*/

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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /*public List<Tag> getExceptedtags() {
        return exceptedtags;
    }

    public void setExceptedtags(List<Tag> exceptedtags) {
        this.exceptedtags = exceptedtags;
    }*/
}
