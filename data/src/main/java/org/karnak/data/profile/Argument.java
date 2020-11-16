package org.karnak.data.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity(name = "Arguments")
@Table(name = "arguments")
public class Argument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "profile_element_id", nullable = false)
    private ProfileElement profileElement;

    private String key;
    private String value;

    public Argument() {
    }

    public Argument(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Argument(String key, String value, ProfileElement profileElement) {
        this.key = key;
        this.value = value;
        this.profileElement = profileElement;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
