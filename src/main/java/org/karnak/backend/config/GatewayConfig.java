package org.karnak.backend.config;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.data.repo.DestinationRepo;
import org.karnak.backend.data.repo.DicomSourceNodeRepo;
import org.karnak.backend.data.repo.ForwardNodeRepo;
import org.karnak.backend.data.repo.KheopsAlbumsRepo;
import org.karnak.backend.data.repo.ProjectRepo;
import org.karnak.backend.data.repo.SOPClassUIDRepo;
import org.karnak.backend.model.dicominnolitics.StandardSOPS;
import org.karnak.backend.model.dicominnolitics.jsonSOP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
public class GatewayConfig {

    private static GatewayConfig instance;
    @Autowired
    private ForwardNodeRepo forwardNodeRepo;
    @Autowired
    private DestinationRepo destinationRepo;
    @Autowired
    private KheopsAlbumsRepo kheopsAlbumsRepo;
    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private DicomSourceNodeRepo dicomSourceNodeRepo;
    @Autowired
    private SOPClassUIDRepo sopClassUIDRepo;

    public static GatewayConfig getInstance() {
        return instance;
    }

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    @PostConstruct
    private void writeSOPSinDatabase() {
        final StandardSOPS standardSOPS = new StandardSOPS();
        Set<SOPClassUIDEntity> sopClassUIDEntitySet = new HashSet<>();
        for (jsonSOP sop : standardSOPS.getSOPS()) {
            final String ciod = sop.getCiod();
            final String uid = sop.getId();
            final String name = sop.getName();
            if (sopClassUIDRepo.existsByCiodAndUidAndName(ciod, uid, name).equals(Boolean.FALSE)) {
                sopClassUIDEntitySet.add(new SOPClassUIDEntity(ciod, uid, name));
            }
        }
        sopClassUIDRepo.saveAll(sopClassUIDEntitySet);
    }

    @Bean("GatewayPersistence")
    public ForwardNodeRepo getGatewayPersistence() {
        return forwardNodeRepo;
    }

    public DestinationRepo getDestinationPersistence() {
        return destinationRepo;
    }

    public KheopsAlbumsRepo getKheopsAlbumsPersistence() {
        return kheopsAlbumsRepo;
    }

    public ProjectRepo getProjectPersistence() {
        return projectRepo;
    }

    public DicomSourceNodeRepo getDicomSourceNodePersistence() {
        return dicomSourceNodeRepo;
    }

    public SOPClassUIDRepo getSopClassUIDPersistence() {
        return sopClassUIDRepo;
    }
}
