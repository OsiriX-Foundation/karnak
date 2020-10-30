package org.karnak.ui.data;

import javax.annotation.PostConstruct;

import org.karnak.data.gateway.*;
import org.karnak.standard.dicominnolitics.StandardSOPS;
import org.karnak.standard.dicominnolitics.jsonSOP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableJpaRepositories
public class GatewayConfiguration {
    private static GatewayConfiguration instance;

    public static GatewayConfiguration getInstance() {
        return instance;
    }

    @Autowired
    private GatewayPersistence gatewayPersistence;

    @Autowired
    private DestinationPersistence destinationPersistence;

    @Autowired
    private KheopsAlbumsPersistence kheopsAlbumsPersistence;

    @Autowired
    private ProjectPersistence projectPersistence;

    @Autowired
    private DicomSourceNodePersistence dicomSourceNodePersistence;

    @Autowired
    private SOPClassUIDPersistence sopClassUIDPersistence;

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    @PostConstruct
    private void writeSOPSinDatabase() {
        final StandardSOPS standardSOPS = new StandardSOPS();
        Set<SOPClassUID> sopClassUIDSet = new HashSet<>();
        for (jsonSOP sop : standardSOPS.getSOPS()) {
            final String ciod = sop.getCiod();
            final String uid = sop.getId();
            final String name = sop.getName();
            if (sopClassUIDPersistence.existsByCiodAndUidAndName(ciod, uid, name).equals(Boolean.FALSE)) {
                sopClassUIDSet.add(new SOPClassUID(ciod, uid, name));
            }
        }
        sopClassUIDPersistence.saveAll(sopClassUIDSet);
    }

    @Bean("GatewayPersistence")
    public GatewayPersistence getGatewayPersistence() {
        return gatewayPersistence;
    }

    public DestinationPersistence getDestinationPersistence() {
        return destinationPersistence;
    }

    public KheopsAlbumsPersistence getKheopsAlbumsPersistence() {
        return kheopsAlbumsPersistence;
    }

    public ProjectPersistence getProjectPersistence() {
        return projectPersistence;
    }

    public DicomSourceNodePersistence getDicomSourceNodePersistence() {
        return dicomSourceNodePersistence;
    }

    public SOPClassUIDPersistence getSopClassUIDPersistence() {return sopClassUIDPersistence; }
}
