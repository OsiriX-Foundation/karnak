/*
 * Copyright (c) 2020-2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.util;

import java.util.List;
import org.dcm4che3.data.Attributes;
import org.karnak.backend.data.entity.ArgumentEntity;
import org.karnak.backend.model.expression.ExprCondition;
import org.karnak.backend.model.profilepipe.HMAC;

public class ShiftByTagDate {

	private ShiftByTagDate() {
	}

	public static void verifyShiftArguments(List<ArgumentEntity> argumentEntities) {
		// All arguments are optional
	}

	public static String shift(Attributes dcm, int tag, List<ArgumentEntity> argumentEntities, HMAC hmac) {
		verifyShiftArguments(argumentEntities);

		String dcmElValue = dcm.getString(tag);
		String shiftDaysTag = ArgumentUtil.stringValue(argumentEntities, "days_tag", "");
		String shiftSecondsTag = ArgumentUtil.stringValue(argumentEntities, "seconds_tag", "");

		int shiftDays = ArgumentUtil.parseInt(dcm.getString(ExprCondition.intFromHexString(shiftDaysTag)), 0);
		int shiftSeconds = ArgumentUtil.parseInt(dcm.getString(ExprCondition.intFromHexString(shiftSecondsTag)), 0);

		return ShiftDate.shiftValue(dcm, tag, dcmElValue, shiftDays, shiftSeconds);
	}

}
