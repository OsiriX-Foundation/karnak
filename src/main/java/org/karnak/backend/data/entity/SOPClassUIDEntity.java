package org.karnak.backend.data.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "SOPClassUID")
@Table(name = "sop_class_uid")
public class SOPClassUIDEntity implements Serializable {

    private static final long serialVersionUID = 2885426916053925842L;

    private Long id;
    private String ciod;
    private String uid;
    private String name;

    public SOPClassUIDEntity() {
    }

    public SOPClassUIDEntity(String ciod, String uid, String name) {
        this.ciod = ciod;
        this.uid = uid;
        this.name = name;
    }

    public SOPClassUIDEntity(String ciod) {
        this.ciod = ciod;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCiod() {
        return ciod;
    }

    public void setCiod(String ciod) {
        this.ciod = ciod;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SOPClassUIDEntity that = (SOPClassUIDEntity) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(ciod, that.ciod) &&
            Objects.equals(uid, that.uid) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ciod, uid, name);
    }
}
