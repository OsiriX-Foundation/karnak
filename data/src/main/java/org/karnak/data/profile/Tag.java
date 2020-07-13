package org.karnak.data.profile;

import javax.persistence.*;

@Entity(name = "Tag")
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    String tagValue;

    @Enumerated(EnumType.STRING)
    TagType type;

    public Tag() {
    }

    public Tag(String tagValue, Profile profile) {
        this.tagValue = tagValue;
        this.profile = profile;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }
}
