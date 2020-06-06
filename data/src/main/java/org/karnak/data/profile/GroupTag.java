package org.karnak.data.profile;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "GroupTag")
@Table(name = "group_tag")
public class GroupTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(name = "group_type")
    private Policy policy;

    @Basic(optional = false)
    @Column(name = "tag_pattern")
    private String tagPattern;

    @Basic(optional = false)
    @Column(name = "group_name")
    private String groupName;

    @ManyToOne( optional = false)
    @JoinColumn(name = "id_profile", nullable = false)
    private ProfileTable profileTable;

    @OneToMany(mappedBy = "groupTag", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<GroupTagAction> tagActions;

    public GroupTag() {
    }

    public GroupTag(ProfileTable profileTable, String groupName, Policy policy, String tagPattern){
        this.profileTable = profileTable;
        this.groupName = groupName;
        this.policy = policy;
        this.tagPattern = tagPattern;
        this.tagActions = new HashSet<>();
    }

    public String getTagPattern(){
        return this.tagPattern;
    }

    public Long getId() {
        return id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public String getGroupName() {
        return groupName;
    }


    public ProfileTable getProfileTable() {
        return profileTable;
    }

    public Set<GroupTagAction> getTagActions() {
        return tagActions;
    }
}