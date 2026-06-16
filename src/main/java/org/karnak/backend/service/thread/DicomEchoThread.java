/*
 * Copyright (c) 2020-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service.thread;

import java.util.concurrent.Callable;
import org.karnak.backend.model.dicom.ConfigNode;
import org.karnak.frontend.dicom.Util;
import org.weasis.dicom.param.DicomNode;
import org.weasis.core.util.annotations.Generated;

@Generated()
public class DicomEchoThread implements Callable<String> {

	private final ConfigNode node;

	public DicomEchoThread(ConfigNode node) {
		this.node = node;
	}

	@Override
	public String call() throws Exception {
		DicomNode dcmNode = node.getCalledNode();
		StringBuilder result = new StringBuilder();
		result.append("<P><h6>DICOM Echo: ").append(node).append("</h6>").append(dcmNode).append("<br><small>");

		boolean success = Util.getEchoResponse(result, "PACSMONITOR", dcmNode, true, "HTML", 3000);
		if (!success) {
			Util.getNetworkResponse(result, dcmNode.getHostname(), dcmNode.getPort(), true);
		}

		result.append("</small></P><hr>");
		return result.toString();
	}

}
