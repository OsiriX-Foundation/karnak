/*
 * Copyright (c) 2020-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.dicom;

import java.util.List;
import java.util.concurrent.ExecutionException;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.backend.service.thread.DicomEchoThread;
import org.springframework.stereotype.Service;
import org.weasis.core.util.annotations.Generated;

@Service
@Generated()
public class DicomEchoService {

	public String dicomEcho(List<ConfigNode> nodes) throws InterruptedException, ExecutionException {
		List<DicomEchoThread> tasks = nodes.stream().map(DicomEchoThread::new).toList();
		return NodeQueryExecutor.invokeAndAggregate(tasks);
	}

}