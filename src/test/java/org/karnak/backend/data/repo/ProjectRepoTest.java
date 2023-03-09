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
import org.karnak.backend.data.entity.ProjectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProjectRepoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRepoTest.class);

	@Autowired
	private ProjectRepo repository;

	/**
	 * Test save and find record.
	 */
	@Test
	void shouldSaveAndFindARecord() {
		// Create an entity to save
		ProjectEntity entity = new ProjectEntity();
		entity.setName("Name");

		// Save the entity
		LOGGER.info("Saving entity with name [{}]", entity.getName());
		entity = repository.save(entity);

		// Test Save
		assertEquals("Name", entity.getName());
		assertNotNull(entity.getId());
		LOGGER.info("Entity with name [{}] and id [{}] saved", entity.getName(), entity.getId());

		// Find By Id
		Optional<ProjectEntity> foundByIdOpt = repository.findById(entity.getId());

		// Test Find by Id
		assertTrue(foundByIdOpt.isPresent());
		LOGGER.info("Entity found with name [{}] and id [{}]", foundByIdOpt.get().getName(),
				foundByIdOpt.get().getId());
		assertEquals(entity.getId(), foundByIdOpt.get().getId());
	}

	/**
	 * Test find all.
	 */
	@Test
	void shouldFindAllRecords() {
		// Create an entity to save
		ProjectEntity entity = new ProjectEntity();
		entity.setName("Name");

		// Save the entity
		LOGGER.info("Saving entity with name [{}]", entity.getName());
		repository.saveAndFlush(entity);

		// Find all
		List<ProjectEntity> all = repository.findAll();

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
		ProjectEntity entity = new ProjectEntity();
		entity.setName(initialText);

		// Save the entity
		LOGGER.info("Saving entity with name [{}]", entity.getName());
		entity = repository.save(entity);
		LOGGER.info("Id of the entity with name [{}]", entity.getId());

		// Test Save
		assertNotNull(entity);
		assertEquals(initialText, entity.getName());

		// Modify the record
		entity.setName(modifiedText);
		LOGGER.info("Modify entity name [{}] to [{}]", initialText, modifiedText);
		ProjectEntity entityModified = repository.save(entity);

		// Test Modify
		assertNotNull(entityModified);
		assertEquals(entity.getId(), entityModified.getId());
		assertEquals(modifiedText, entityModified.getName());
		LOGGER.info("Name of the entity with id [{}]: [{}]", entityModified.getId(), entityModified.getName());
	}

	/**
	 * Test delete record.
	 */
	@Test
	void shouldDeleteRecord() {
		// Create an entity to save
		ProjectEntity entity = new ProjectEntity();
		String name = "Name";
		entity.setName(name);

		// Save the entity
		LOGGER.info("Saving entity with name [{}]", entity.getName());
		entity = repository.save(entity);

		// Retrieve the entity
		Optional<ProjectEntity> foundByIdOpt = repository.findById(entity.getId());

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
