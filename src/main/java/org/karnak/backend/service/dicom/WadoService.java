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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.karnak.backend.model.dicom.WadoNode;
import org.karnak.backend.service.thread.WadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WadoService {

	@Autowired
	public WadoService() {
	}

	public String checkWado(List<WadoNode> nodes) throws InterruptedException, ExecutionException {
		StringBuilder result = new StringBuilder();

		List<Future<String>> threadsResult = createThreadsResult(nodes);
		for (Future<String> threadResult : threadsResult) {
			result.append(threadResult.get());
		}

		return result.toString();
	}

	private List<Future<String>> createThreadsResult(List<WadoNode> nodes) throws InterruptedException {
		if (nodes == null || nodes.isEmpty()) {
			throw new IllegalArgumentException("The nodes list cannot be null or empty");
		}

		int poolSize = Math.min(nodes.size(), Runtime.getRuntime().availableProcessors());

		try (ExecutorService executorService = Executors.newFixedThreadPool(poolSize)) {
			List<WadoResponse> tasks = new ArrayList<>();
			for (WadoNode node : nodes) {
				if (node == null || node.getUrl() == null) {
					throw new IllegalArgumentException("Invalid WadoNode detected");
				}
				tasks.add(new WadoResponse(node));
			}
			return executorService.invokeAll(tasks, 30, TimeUnit.SECONDS);
		}
	}

}
