/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.frontend.dicom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.param.DicomNode;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UtilTest {

	// A loopback address with a port nothing listens on: the connection is refused
	// immediately, so the network/echo probes fail fast and deterministically.
	private static final String UNREACHABLE_HOST = "127.0.0.1";

	private static final int CLOSED_PORT = 1;

	private static final int CONNECT_TIMEOUT_MS = 500;

	@Test
	void network_response_in_xml_is_wrapped_in_dcm_network_status_tags() {
		StringBuilder result = new StringBuilder();

		// Reachability depends on the host environment, so we only assert the XML
		// framing.
		Util.getNetworkResponse(result,  UNREACHABLE_HOST, CLOSED_PORT, true, "XML");

		String xml = result.toString();
		assertTrue(xml.contains("<DcmNetworkStatus>"), xml);
		assertTrue(xml.contains("</DcmNetworkStatus>"), xml);
	}

	@Test
	void network_response_in_html_is_not_wrapped_in_xml_tags() {
		StringBuilder result = new StringBuilder();

		// The 5-argument overload defaults to the HTML format.
		Util.getNetworkResponse(result,  UNREACHABLE_HOST, CLOSED_PORT, true);

		String html = result.toString();
		assertFalse(html.isEmpty());
		assertFalse(html.contains("<DcmNetworkStatus>"), html);
	}

	@Test
	void echo_response_in_xml_is_wrapped_in_dcm_status_tags() {
		StringBuilder result = new StringBuilder();
		DicomNode calledNode = new DicomNode("AET", UNREACHABLE_HOST, CLOSED_PORT);

		boolean success = Util.getEchoResponse(result, "CALLING", calledNode, true, "XML", CONNECT_TIMEOUT_MS);

		String xml = result.toString();
		assertTrue(xml.contains("<DcmStatus>"), xml);
		assertTrue(xml.contains("</DcmStatus>"), xml);
		assertTrue(xml.contains("<DcmStatusMessage>"), xml);
		assertFalse(success);
	}

	@Test
	void echo_response_in_html_reports_dicom_status_without_xml_tags() {
		StringBuilder result = new StringBuilder();
		DicomNode calledNode = new DicomNode("AET", UNREACHABLE_HOST, CLOSED_PORT);

		boolean success = Util.getEchoResponse(result, "CALLING", calledNode, true, "HTML", CONNECT_TIMEOUT_MS);

		String html = result.toString();
		assertTrue(html.contains("DICOM"), html);
		assertFalse(html.contains("<DcmStatus>"), html);
		assertFalse(success);
	}

}
