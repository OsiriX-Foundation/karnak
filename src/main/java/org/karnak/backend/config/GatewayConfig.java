package org.karnak.backend.config;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.data.repo.SOPClassUIDRepo;
import org.karnak.backend.model.dicominnolitics.StandardSOPS;
import org.karnak.backend.model.dicominnolitics.jsonSOP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
public class GatewayConfig {

  // Repositories
  private final SOPClassUIDRepo sopClassUIDRepo;

  @Autowired
  public GatewayConfig(final SOPClassUIDRepo sopClassUIDRepo) {
    this.sopClassUIDRepo = sopClassUIDRepo;
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
}
