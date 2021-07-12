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

import java.awt.Rectangle;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.MaskEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MaskRepoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(MaskRepoTest.class);

  @Autowired private MaskRepo repository;
  @Autowired private ProfileRepo profileRepo;

  /** Test save and find record. */
  @Test
  void shouldSaveAndFindARecord() {
    // Create an entity to save
    MaskEntity entity = new MaskEntity();
    entity.setStationName("Name");

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getStationName());
    entity = repository.save(entity);

    // Test Save
    Assert.assertEquals("Name", entity.getStationName());
    Assert.assertNotNull(entity.getId());
    LOGGER.info("Entity with name [{}] and id [{}] saved", entity.getStationName(), entity.getId());

    // Find By Id
    Optional<MaskEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    Assert.assertTrue(foundByIdOpt.isPresent());
    LOGGER.info(
        "Entity found with name [{}] and id [{}]",
        foundByIdOpt.get().getStationName(),
        foundByIdOpt.get().getId());
    Assert.assertEquals(entity.getId(), foundByIdOpt.get().getId());
  }

  /** Test find all. */
  @Test
  void shouldFindAllRecords() {
    // Create an entity to save
    // Profile
    ProfileEntity profileEntity = new ProfileEntity();
    profileEntity.setName("name");
    profileEntity = profileRepo.saveAndFlush(profileEntity);
    // Mask
    MaskEntity entity = new MaskEntity();
    entity.setStationName("Name");
    entity.addRectangle(new Rectangle());
    entity.setProfileEntity(profileEntity);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getStationName());
    repository.saveAndFlush(entity);

    // Find all
    List<MaskEntity> all = repository.findAll();

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
    MaskEntity entity = new MaskEntity();
    entity.setStationName(initialText);

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getStationName());
    entity = repository.save(entity);
    LOGGER.info("Id of the entity with name [{}]", entity.getId());

    // Test Save
    Assert.assertNotNull(entity);
    Assert.assertEquals(initialText, entity.getStationName());

    // Modify the record
    entity.setStationName(modifiedText);
    LOGGER.info("Modify entity name [{}] to [{}]", initialText, modifiedText);
    MaskEntity entityModified = repository.save(entity);

    // Test Modify
    Assert.assertNotNull(entityModified);
    Assert.assertEquals(entity.getId(), entityModified.getId());
    Assert.assertEquals(modifiedText, entityModified.getStationName());
    LOGGER.info(
        "Name of the entity with id [{}]: [{}]",
        entityModified.getId(),
        entityModified.getStationName());
  }

  /** Test delete record. */
  @Test
  void shouldDeleteRecord() {
    // Create an entity to save
    MaskEntity entity = new MaskEntity();
    String name = "Name";
    entity.setStationName(name);
    entity.addRectangle(new Rectangle());

    // Save the entity
    LOGGER.info("Saving entity with name [{}]", entity.getStationName());
    entity = repository.save(entity);

    // Retrieve the entity
    Optional<MaskEntity> foundByIdOpt = repository.findById(entity.getId());

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
