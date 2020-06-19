package org.karnak.data.gateway;

import javax.persistence.*;

@Entity(name = "FilterBySOPClass")
@Table(name = "filter_by_sop_class")
public class FilterBySOPClass {
    @Id
    Long id;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    Destination destination;

    @ManyToOne
    @JoinColumn(name = "sop_class_uid_id")
    SOPClassUID sopClassUID;
}
