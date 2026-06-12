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
import org.karnak.backend.model.dicom.WadoNode;
import org.karnak.backend.service.thread.WadoResponse;
import org.springframework.stereotype.Service;
import org.weasis.core.util.annotations.Generated;

@Service
@Generated()
public class WadoService {

	public String checkWado(List<WadoNode> nodes) throws InterruptedException, ExecutionException {
		if (nodes == null || nodes.isEmpty()) {
			throw new IllegalArgumentException("The nodes list cannot be null or empty");
		}
		List<WadoResponse> tasks = new ArrayList<>(nodes.size());
		for (WadoNode node : nodes) {
			if (node == null || node.getUrl() == null) {
				throw new IllegalArgumentException("Invalid WadoNode detected");
			}
			tasks.add(new WadoResponse(node));
		}
		return NodeQueryExecutor.invokeAndAggregate(tasks);
	}

}