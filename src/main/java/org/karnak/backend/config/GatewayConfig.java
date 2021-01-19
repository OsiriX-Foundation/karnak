/*
* Copyright (c) 2021 Weasis Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.config;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.data.repo.DestinationRepo;
import org.karnak.backend.data.repo.DicomSourceNodeRepo;
import org.karnak.backend.data.repo.GatewayRepo;
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
  @Autowired private GatewayRepo gatewayRepo;
  @Autowired private DestinationRepo destinationRepo;
  @Autowired private KheopsAlbumsRepo kheopsAlbumsRepo;
  @Autowired private ProjectRepo projectRepo;
  @Autowired private DicomSourceNodeRepo dicomSourceNodeRepo;
  @Autowired private SOPClassUIDRepo sopClassUIDRepo;

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
  public GatewayRepo getGatewayPersistence() {
    return gatewayRepo;
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
