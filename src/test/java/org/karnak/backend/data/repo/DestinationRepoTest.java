/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.repo;

import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.enums.DestinationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DestinationRepoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DestinationRepoTest.class);

  @Autowired private DestinationRepo repository;

  /** Test save and find record. */
  @Test
  void shouldSaveAndFindARecord() {
    // Create an entity to save
    DestinationEntity entity = new DestinationEntity();
    entity.setAeTitle("Name");

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getAeTitle());
    entity = repository.save(entity);

    // Test Save
    Assert.assertEquals("Name", entity.getAeTitle());
    Assert.assertNotNull(entity.getId());
    LOGGER.info("Entity with name [{}] and id [{}] saved", entity.getAeTitle(), entity.getId());

    // Find By Id
    Optional<DestinationEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    Assert.assertTrue(foundByIdOpt.isPresent());
    LOGGER.info(
        "Entity found with name [{}] and id [{}]",
        foundByIdOpt.get().getAeTitle(),
        foundByIdOpt.get().getId());
    Assert.assertEquals(entity.getId(), foundByIdOpt.get().getId());
  }

  /** Test find all. */
  @Test
  void shouldFindAllRecords() {
    // Create an entity to save
    DestinationEntity entity = new DestinationEntity();
    entity.setAeTitle("Name");
    entity.setDestinationType(DestinationType.dicom);
    entity.setHostname("hostName");
    entity.setPort(1);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getAeTitle());
    repository.saveAndFlush(entity);

    // Find all
    List<DestinationEntity> all = repository.findAll();

    // Test find all
    Assert.assertNotNull(all);
    Assert.assertTrue(all.size() > 0);
    Assert.assertEquals(1, all.size());
    LOGGER.info("Number of entities found [{}]", all.size());
  }

  /** Test modification of a record. */
  @Test
  void shouldModifyRecord() {

    String initialText = "InitialText";
    String modifiedText = "ModifiedText";

    // Create an entity to save
    DestinationEntity entity = new DestinationEntity();
    entity.setAeTitle(initialText);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getAeTitle());
    entity = repository.save(entity);
    LOGGER.info("Id of the entity with name [{}]", entity.getId());

    // Test Save
    Assert.assertNotNull(entity);
    Assert.assertEquals(initialText, entity.getAeTitle());

    // Modify the record
    entity.setAeTitle(modifiedText);
    LOGGER.info("Modify entity name [{}] to [{}]", initialText, modifiedText);
    DestinationEntity entityModified = repository.save(entity);

    // Test Modify
    Assert.assertNotNull(entityModified);
    Assert.assertEquals(entity.getId(), entityModified.getId());
    Assert.assertEquals(modifiedText, entityModified.getAeTitle());
    LOGGER.info(
        "Name of the entity with id [{}]: [{}]",
        entityModified.getId(),
        entityModified.getAeTitle());
  }

  /** Test delete record. */
  @Test
  void shouldDeleteRecord() {
    // Create an entity to save
    DestinationEntity entity = new DestinationEntity();
    String name = "Name";
    entity.setAeTitle(name);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getAeTitle());
    entity = repository.save(entity);

    // Retrieve the entity
    Optional<DestinationEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    Assert.assertTrue(foundByIdOpt.isPresent());

    // Delete the entity
    entity = foundByIdOpt.get();
    Long id = entity.getId();
    LOGGER.info("Deleting entity with id [{}]", id);
    repository.delete(entity);

    // Test Delete
    foundByIdOpt = repository.findById(id);
    LOGGER.info("Is deleted entity with id [{}] present: [{}]", id, foundByIdOpt.isPresent());
    Assert.assertFalse(foundByIdOpt.isPresent());
  }
}
