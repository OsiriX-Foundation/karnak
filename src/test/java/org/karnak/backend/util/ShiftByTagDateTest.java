/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.model.profilepipe.HMAC;

/**
 * {@link ShiftByTagDate} reads the shift amounts from other tags of the same object,
 * named by the {@code days_tag} / {@code seconds_tag} arguments.
 */
@DisplayNameGeneration(ReplaceUnderscores.class)
class ShiftByTagDateTest {

	private static final HMAC HMAC_KEY = new HMAC(HMAC.hexToByte("0123456789abcdef0123456789abcdef"));

	@Test
	void shifts_a_date_by_the_value_held_in_another_tag() {
		Attributes dcm = new Attributes();
		dcm.setString(Tag.StudyDate, VR.DA, "20200110");
		// SeriesNumber (0020,0011) holds the number of days to shift.
		dcm.setString(Tag.SeriesNumber, VR.IS, "5");
		dcm.setString(Tag.AcquisitionNumber, VR.IS, "0");

		List<ArgumentEntity> args = List.of(new ArgumentEntity("days_tag", "00200011"),
				new ArgumentEntity("seconds_tag", "00200012"));

		assertEquals("20200105", ShiftByTagDate.shift(dcm, Tag.StudyDate, args, HMAC_KEY));
	}

	@Test
	void missing_shift_tags_default_to_no_shift() {
		Attributes dcm = new Attributes();
		dcm.setString(Tag.StudyDate, VR.DA, "20200110");
		dcm.setString(Tag.SeriesNumber, VR.IS, "0");
		dcm.setString(Tag.AcquisitionNumber, VR.IS, "0");

		List<ArgumentEntity> args = List.of(new ArgumentEntity("days_tag", "00200011"),
				new ArgumentEntity("seconds_tag", "00200012"));

		assertEquals("20200110", ShiftByTagDate.shift(dcm, Tag.StudyDate, args, HMAC_KEY));
	}

}