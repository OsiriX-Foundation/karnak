/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.dicom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class NodeQueryExecutorTest {

	@Test
	void aggregates_task_results_in_order() throws Exception {
		List<Callable<String>> tasks = List.of(() -> "a", () -> "b", () -> "c");

		assertEquals("abc", NodeQueryExecutor.invokeAndAggregate(tasks));
	}

	@Test
	void aggregates_a_single_task() throws Exception {
		assertEquals("only", NodeQueryExecutor.invokeAndAggregate(List.of(() -> "only")));
	}

	@Test
	void propagates_a_task_failure_as_an_execution_exception() {
		List<Callable<String>> tasks = List.of(() -> {
			throw new IllegalStateException("boom");
		});

		assertThrows(ExecutionException.class, () -> NodeQueryExecutor.invokeAndAggregate(tasks));
	}

}
