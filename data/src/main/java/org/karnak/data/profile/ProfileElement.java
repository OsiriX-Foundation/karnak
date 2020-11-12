package org.karnak.data.profile;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.karnak.data.profile.converter.TagListToStringListConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ProfileElement")
@Table(name = "profile_element")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    private String name;
    private String codename;
    private String condition;
    private String action;
    private String option;
    @JsonIgnore
    private Integer position;

    @ManyToOne()
    @JoinColumn(name = "profile_id", nullable = false)
    @JsonIgnore
    private Profile profile;

    @OneToMany(mappedBy = "profileElement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonSerialize(converter = TagListToStringListConverter.class)
    private List<IncludedTag> includedtag = new ArrayList<>();

    @OneToMany(mappedBy = "profileElement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonSerialize(converter = TagListToStringListConverter.class)
    private List<ExcludedTag> exceptedtags = new ArrayList<>();

    @OneToMany(mappedBy = "profileElement", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Argument> arguments = new ArrayList<>();

    public ProfileElement() {
    }

    public ProfileElement(String name, String codename, String condition, String action, String option, Integer position, Profile profile) {
        this.name = name;
        this.codename = codename;
        this.condition = condition;
        this.action = action;
        this.option = option;
        this.position = position;
        this.profile = profile;
    }

    public ProfileElement(String name, String codename, String condition, String action, String option, List<Argument> arguments, Integer position, Profile profile) {
        this.name = name;
        this.codename = codename;
        this.condition = condition;
        this.action = action;
        this.option = option;
        this.arguments = arguments;
        this.position = position;
        this.profile = profile;
    }

    public void addIncludedTag(IncludedTag includedtag){
        this.includedtag.add(includedtag);
    }

    public void addExceptedtags(ExcludedTag exceptedtags){
        this.exceptedtags.add(exceptedtags);
    }

    public void addArgument(Argument argument) { this.arguments.add(argument); }

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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
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

    @JsonProperty("tags")
    public List<IncludedTag> getIncludedtag() {
        return includedtag;
    }

    public void setIncludedtag(List<IncludedTag> includedtag) {
        this.includedtag = includedtag;
    }

    @JsonProperty("excludedTags")
    public List<ExcludedTag> getExceptedtags() {
        return exceptedtags;
    }

    public void setExceptedtags(List<ExcludedTag> exceptedtags) {
        this.exceptedtags = exceptedtags;
    }
}
