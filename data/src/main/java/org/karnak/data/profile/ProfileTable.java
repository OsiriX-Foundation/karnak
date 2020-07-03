package org.karnak.data.profile;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "Profile")
@Table(name = "profile")
public class ProfileTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "profileTable", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TagActionTable> actions;

    @OneToMany(mappedBy = "profileTable", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<GroupTag> groupTagSet;

    public ProfileTable() {
    }

    public ProfileTable(String name) {
        this(name, null, null);
    }

    public ProfileTable(String name, Set<TagActionTable> actions, Set<GroupTag> groupTagSet) {
        this.name = name;
        this.actions = actions == null ? new HashSet<>() : actions;
        this.groupTagSet = groupTagSet == null ? new HashSet<>() : groupTagSet;
    }

    public void addAction(TagActionTable action) {
        this.actions.add(action);
    }

    public Long getId() {
        return this.id;
    }

    public Set<TagActionTable> getActions() {
        return this.actions;
    }

    public void addGroupAction(GroupTag groupTag) {
        this.groupTagSet.add(groupTag);
    }

    public Set<GroupTag> getGroupTagSet() {
        return this.groupTagSet;
    }
}