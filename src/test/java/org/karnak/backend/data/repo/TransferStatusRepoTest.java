/*
 * Copyright (c) 2022 Karnak Team and other contributors.
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.TransferStatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class TransferStatusRepoTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransferStatusRepoTest.class);

	@Autowired
	private TransferStatusRepo repository;

	/**
	 * Test save and find record.
	 */
	@Test
	void shouldSaveAndFindARecord() {
		// Create an entity to save
		TransferStatusEntity entity = new TransferStatusEntity();
		entity.setPatientIdOriginal("Id");

		// Save the entity
		LOGGER.info("Saving entity with PatientIdOriginal [{}]", entity.getPatientIdOriginal());
		entity = repository.save(entity);

		// Test Save
		assertEquals("Id", entity.getPatientIdOriginal());
		assertNotNull(entity.getId());
		LOGGER.info("Entity with PatientIdOriginal [{}] and id [{}] saved", entity.getPatientIdOriginal(),
				entity.getId());

		// Find By Id
		Optional<TransferStatusEntity> foundByIdOpt = repository.findById(entity.getId());

		// Test Find by Id
		assertTrue(foundByIdOpt.isPresent());
		LOGGER.info("Entity found with PatientId [{}] and id [{}]", foundByIdOpt.get().getPatientIdOriginal(),
				foundByIdOpt.get().getId());
		assertEquals(entity.getId(), foundByIdOpt.get().getId());
	}

	/**
	 * Test find all.
	 */
	@Test
	void shouldFindAllRecords() {
		// Create an entity to save
		TransferStatusEntity entity = new TransferStatusEntity();
		entity.setPatientIdToSend("Id");

		// Save the entity
		LOGGER.info("Saving entity with PatientIdToSend [{}]", entity.getPatientIdToSend());
		repository.saveAndFlush(entity);

		// Find all
		List<TransferStatusEntity> all = repository.findAll();

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
		TransferStatusEntity entity = new TransferStatusEntity();
		entity.setStudyDescriptionOriginal(initialText);

		// Save the entity
		LOGGER.info("Saving entity with description [{}]", entity.getStudyDescriptionOriginal());
		entity = repository.save(entity);
		LOGGER.info("Id of the entity with description [{}]", entity.getStudyDescriptionOriginal());

		// Test Save
		assertNotNull(entity);
		assertEquals(initialText, entity.getStudyDescriptionOriginal());

		// Modify the record
		entity.setStudyDescriptionOriginal(modifiedText);
		LOGGER.info("Modify entity description [{}] to [{}]", initialText, modifiedText);
		TransferStatusEntity entityModified = repository.save(entity);

		// Test Modify
		assertNotNull(entityModified);
		assertEquals(entity.getId(), entityModified.getId());
		assertEquals(modifiedText, entityModified.getStudyDescriptionOriginal());
		LOGGER.info("Description of the entity with id [{}]: [{}]", entityModified.getId(),
				entityModified.getStudyDescriptionOriginal());
	}

	/**
	 * Test delete record.
	 */
	@Test
	void shouldDeleteRecord() {
		// Create an entity to save
		TransferStatusEntity entity = new TransferStatusEntity();
		String description = "description";
		entity.setStudyDescriptionToSend(description);

		// Save the entity
		LOGGER.info("Saving entity with description [{}]", entity.getStudyDescriptionToSend());
		entity = repository.save(entity);

		// Retrieve the entity
		Optional<TransferStatusEntity> foundByIdOpt = repository.findById(entity.getId());

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

	/**
	 * Test method findByDestinationIdAndTransferDateAfter
	 */
	@Test
	void shouldFindByDestinationId() {
		// Init data
		TransferStatusEntity transferStatusEntity = new TransferStatusEntity();
		TransferStatusEntity toTest = repository.saveAndFlush(transferStatusEntity);

		// Call method with date before
		List<TransferStatusEntity> byDestinationId = repository.findByDestinationId(toTest.getDestinationId());

		// Test results
		assertFalse(byDestinationId.isEmpty());
		assertEquals(toTest.getId(), byDestinationId.get(0).getId());
	}

	/**
	 * Test method findByDestinationIdAndTransferDateAfter
	 */
	@Test
	void shouldFindByDestinationIdAndTransferDateAfter() {
		// Init data
		TransferStatusEntity transferStatusEntity = new TransferStatusEntity();
		transferStatusEntity.setTransferDate(LocalDateTime.of(2022, 1, 14, 15, 16, 17));
		TransferStatusEntity toTest = repository.saveAndFlush(transferStatusEntity);

		// Call method with date before
		List<TransferStatusEntity> byDestinationIdAndTransferDateAfter = repository
			.findByDestinationIdAndTransferDateAfter(toTest.getDestinationId(), LocalDateTime.of(2022, 1, 14, 0, 0, 0));

		// Test results
		assertFalse(byDestinationIdAndTransferDateAfter.isEmpty());

		// Call method with date after
		byDestinationIdAndTransferDateAfter = repository
			.findByDestinationIdAndTransferDateAfter(toTest.getDestinationId(), LocalDateTime.of(2022, 1, 15, 0, 0, 0));

		// Test results
		assertTrue(byDestinationIdAndTransferDateAfter.isEmpty());
	}

	/**
	 * Test method findAllByOrderByTransferDateAsc
	 */
	@Test
	void shouldFindAllByOrderByTransferDateAsc() {
		// Init data
		TransferStatusEntity transferStatusEntityAfter = new TransferStatusEntity();
		transferStatusEntityAfter.setTransferDate(LocalDateTime.of(2022, 1, 2, 1, 2, 3));
		transferStatusEntityAfter = repository.saveAndFlush(transferStatusEntityAfter);
		TransferStatusEntity transferStatusEntityBefore = new TransferStatusEntity();
		transferStatusEntityBefore.setTransferDate(LocalDateTime.of(2022, 1, 1, 1, 2, 3));
		transferStatusEntityBefore = repository.saveAndFlush(transferStatusEntityBefore);
		TransferStatusEntity transferStatusEntityShouldBeFiltered = new TransferStatusEntity();
		transferStatusEntityShouldBeFiltered.setTransferDate(LocalDateTime.of(2022, 1, 3, 1, 2, 3));
		transferStatusEntityShouldBeFiltered = repository.saveAndFlush(transferStatusEntityShouldBeFiltered);

		// Limit to 2 results
		Pageable pageable = PageRequest.of(0, 2);

		// Call method with date before
		List<TransferStatusEntity> transferStatusEntities = repository.findAllByOrderByTransferDateAsc(pageable)
			.toList();

		// Test results
		assertFalse(transferStatusEntities.isEmpty());
		assertEquals(2, transferStatusEntities.size());
		assertEquals(transferStatusEntityBefore, transferStatusEntities.get(0));
		assertEquals(transferStatusEntityAfter, transferStatusEntities.get(1));
	}

}
