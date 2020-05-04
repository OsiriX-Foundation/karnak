package org.karnak.data.gateway;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity(name = "Action")
@Table(name = "action")
public class ActionTable {
    
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
    @JoinColumn(name = "id_profile", nullable = false)
    private ProfileTable profileTable;

    public ActionTable() {
    }


    public ActionTable(ProfileTable profileTable, Integer tag, String action, String attributeName){
        this.profileTable = profileTable;
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


}