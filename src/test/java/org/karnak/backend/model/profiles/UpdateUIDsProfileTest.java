/*
 * Copyright (c) 2020-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.profiles;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.karnak.backend.data.entity.ProfileElementEntity;
import org.karnak.backend.model.action.Keep;
import org.karnak.backend.model.action.Remove;
import org.karnak.backend.model.action.ReplaceNull;
import org.karnak.backend.model.action.UID;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UpdateUIDsProfileTest {

	private static UpdateUIDsProfile profile() {
		return new UpdateUIDsProfile(new ProfileElementEntity("name", "update.uids", null, null, null, 0, null));
	}

	@Test
	void accepts_uid_remove_and_replace_null_actions() {
		UpdateUIDsProfile profile = profile();

		profile.put(Tag.StudyInstanceUID, new UID("U"));
		profile.put(Tag.SeriesInstanceUID, new Remove("X"));
		profile.put(Tag.SOPInstanceUID, new ReplaceNull("Z"));

		assertInstanceOf(UID.class, profile.getAction(new Attributes(), new Attributes(), Tag.StudyInstanceUID, null));
		assertInstanceOf(Remove.class,
				profile.getAction(new Attributes(), new Attributes(), Tag.SeriesInstanceUID, null));
		assertInstanceOf(ReplaceNull.class,
				profile.getAction(new Attributes(), new Attributes(), Tag.SOPInstanceUID, null));
	}

	@Test
	void rejects_an_inconsistent_action() {
		UpdateUIDsProfile profile = profile();

		assertThrows(IllegalStateException.class, () -> profile.put(Tag.StudyInstanceUID, new Keep("K")));
	}

	@Test
	void returns_null_for_an_unmapped_tag() {
		assertNull(profile().getAction(new Attributes(), new Attributes(), Tag.StudyInstanceUID, null));
	}

	@Test
	void removes_and_clears_mapped_actions() {
		UpdateUIDsProfile profile = profile();
		UID uid = new UID("U");
		profile.put(Tag.StudyInstanceUID, uid);

		assertSame(uid, profile.remove(Tag.StudyInstanceUID));
		assertNull(profile.getAction(new Attributes(), new Attributes(), Tag.StudyInstanceUID, null));

		profile.put(Tag.SeriesInstanceUID, new Remove("X"));
		profile.clearTagMap();
		assertNull(profile.getAction(new Attributes(), new Attributes(), Tag.SeriesInstanceUID, null));
	}

}
