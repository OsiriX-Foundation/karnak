package org.karnak.profilepipe.profilebody;

import java.util.List;

public class ProfileBody {
    private String name;
    private String codename;
    private String action;
    private List<String> tags;
    private List<String> exceptedtags;

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getExceptedtags() {
        return exceptedtags;
    }

    public void setExceptedtags(List<String> exceptedtags) {
        this.exceptedtags = exceptedtags;
    }
}
