package org.karnak.data.gateway;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


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
    private Set<ActionTable> actions;


    public ProfileTable() {
    }

    public ProfileTable(String name) {
        this.name = name;
        this.actions = new HashSet<>();
    }

    public ProfileTable(String name, Set<ActionTable> actions) {
        this.name = name;
        this.actions = actions;
    }

    public void addAction(ActionTable action){
        this.actions.add(action);
    }

    public Long getId(){
        return this.id;
    }

    public Set<ActionTable> getActions(){
        return this.actions;
    }

}