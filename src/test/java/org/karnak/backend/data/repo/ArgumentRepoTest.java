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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.data.entity.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ArgumentRepoTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentRepoTest.class);

  @Autowired
  private ArgumentRepo repository;

  @Autowired
  private ProfileElementRepo profileElementRepo;

  @Autowired
  private ProfileRepo profileRepo;

  /**
   * Test save and find record.
   */
  @Test
  void shouldSaveAndFindARecord() {
    // Create an entity to save
    ArgumentEntity entity = new ArgumentEntity();
    entity.setKey("Key");

    // Save the entity
    LOGGER.info("Saving entity with Key [{}]", entity.getKey());
    entity = repository.save(entity);

    // Test Save
    assertEquals("Key", entity.getKey());
    assertNotNull(entity.getId());
    LOGGER.info("Entity with Key [{}] and id [{}] saved", entity.getKey(), entity.getId());

    // Find By Id
    Optional<ArgumentEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    assertTrue(foundByIdOpt.isPresent());
    LOGGER.info(
        "Entity found with Key [{}] and id [{}]",
        foundByIdOpt.get().getKey(),
        foundByIdOpt.get().getId());
    assertEquals(entity.getId(), foundByIdOpt.get().getId());
  }

  /**
   * Test find all.
   */
  @Test
  void shouldFindAllRecords() {
    // Create an entity to save
    // Profile
    ProfileEntity profileEntity = new ProfileEntity();
    profileEntity.setName("name");
    profileEntity = profileRepo.saveAndFlush(profileEntity);
    // Profile element
    ProfileElementEntity profileElementEntity = new ProfileElementEntity();
    profileElementEntity.setName("name");
    profileElementEntity.setProfileEntity(profileEntity);
    profileElementEntity = profileElementRepo.saveAndFlush(profileElementEntity);
    // Argument
    ArgumentEntity entity = new ArgumentEntity();
    entity.setKey("Key");
    entity.setProfileElementEntity(profileElementEntity);

    // Save the entity
    LOGGER.info("Saving entity with Key [{}]", entity.getKey());
    repository.saveAndFlush(entity);

    // Find all
    List<ArgumentEntity> all = repository.findAll();

    // Test find all
    assertNotNull(all);
    assertTrue(all.size() > 0);
    assertEquals(1, all.size());
    LOGGER.info("Number of entities found [{}]", all.size());
  }

  /**
   * Test modification of a record.
   */
  @Test
  void shouldModifyRecord() {

    String initialText = "InitialText";
    String modifiedText = "ModifiedText";

    // Create an entity to save
    ArgumentEntity entity = new ArgumentEntity();
    entity.setKey(initialText);

    // Save the entity
    LOGGER.info("Saving entity with Key [{}]", entity.getKey());
    entity = repository.save(entity);
    LOGGER.info("Id of the entity with Key [{}]", entity.getId());

    // Test Save
    assertNotNull(entity);
    assertEquals(initialText, entity.getKey());

    // Modify the record
    entity.setKey(modifiedText);
    LOGGER.info("Modify entity Key [{}] to [{}]", initialText, modifiedText);
    ArgumentEntity entityModified = repository.save(entity);

    // Test Modify
    assertNotNull(entityModified);
    assertEquals(entity.getId(), entityModified.getId());
    assertEquals(modifiedText, entityModified.getKey());
    LOGGER.info(
        "Key of the entity with id [{}]: [{}]", entityModified.getId(), entityModified.getKey());
  }

  /**
   * Test delete record.
   */
  @Test
  void shouldDeleteRecord() {
    // Create an entity to save
    ArgumentEntity entity = new ArgumentEntity();
    String key = "Key";
    entity.setKey(key);

    // Save the entity
    LOGGER.info("Saving entity with Key [{}]", entity.getKey());
    entity = repository.save(entity);

    // Retrieve the entity
    Optional<ArgumentEntity> foundByIdOpt = repository.findById(entity.getId());

    // Test Find by Id
    assertTrue(foundByIdOpt.isPresent());

    // Delete the entity
    entity = foundByIdOpt.get();
    Long id = entity.getId();
    LOGGER.info("Deleting entity with id [{}]", id);
    repository.delete(entity);

    // Test Delete
    foundByIdOpt = repository.findById(id);
    LOGGER.info("Is deleted entity with id [{}] present: [{}]", id, foundByIdOpt.isPresent());
    assertFalse(foundByIdOpt.isPresent());
  }
}
