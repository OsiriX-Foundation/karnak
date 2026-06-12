/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.dicom;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Runs node-query tasks on a bounded pool (30s timeout) and concatenates their results.
 */
final class NodeQueryExecutor {

	private static final int TIMEOUT_SECONDS = 30;

	private NodeQueryExecutor() {
	}

	static String invokeAndAggregate(List<? extends Callable<String>> tasks)
			throws InterruptedException, ExecutionException {
		int poolSize = Math.min(tasks.size(), Runtime.getRuntime().availableProcessors());
		List<Future<String>> futures;
		try (ExecutorService executorService = Executors.newFixedThreadPool(poolSize)) {
			futures = executorService.invokeAll(tasks, TIMEOUT_SECONDS, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw e;
		}

		StringBuilder result = new StringBuilder();
		for (Future<String> future : futures) {
			result.append(future.get());
		}
		return result.toString();
	}

}