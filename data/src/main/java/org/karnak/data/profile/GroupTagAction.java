package org.karnak.data.profile;

import javax.persistence.*;


@Entity(name = "GroupAction")
@Table(name = "group_action")
public class GroupTagAction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    @Column(name = "tag")
    private Integer tag;

    @Basic(optional = false)
    @Column(name = "action")
    private String action;

    @Basic(optional = true)
    @Column(name = "attributeName")
    private String attributeName;

    @ManyToOne( optional = false)
    @JoinColumn(name = "id_group", nullable = false)
    private GroupTag groupTag;

    public GroupTagAction() {
    }


    public GroupTagAction(GroupTag groupTag, Integer tag, String action, String attributeName){
        this.groupTag = groupTag;
        this.tag = tag;
        this.action = action;
        this.attributeName = attributeName;
    }

    public Integer getTag(){
        return this.tag;
    }

    public String getAction(){
        return this.action;
    }

    public Long getId() {
        return id;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public GroupTag getGroupTag() {
        return groupTag;
    }
}