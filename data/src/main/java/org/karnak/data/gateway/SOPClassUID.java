package org.karnak.data.gateway;

import org.hibernate.validator.group.GroupSequenceProvider;
import org.karnak.data.profile.ActionTable;
import org.karnak.data.profile.ProfileTable;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "SOPClassUID")
@Table(name = "sop_class_uid")
public class SOPClassUID {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String sopClassUIDValue;


    @OneToMany(mappedBy = "sopClassUID")
    private Set<FilterBySOPClass> filterBySOPClasses;

    public SOPClassUID(String sopClassUIDValue){
        this.sopClassUIDValue = sopClassUIDValue;
    }

    public String getSopClassUIDValue() {
        return sopClassUIDValue;
    }

    public void setSopClassUIDValue(String sopClassUIDValue) {
        this.sopClassUIDValue = sopClassUIDValue;
    }

}
