/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.karnak.backend.model.action.Keep;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ExpressionResultTest {

	private static ExprCondition conditionWithPatientName() {
		Attributes dcm = new Attributes();
		dcm.setString(Tag.PatientName, VR.PN, "Doe^John");
		return new ExprCondition(dcm);
	}

	@Nested
	class Get {

		@Test
		void evaluates_a_boolean_condition_to_true() {
			Object result = ExpressionResult.get("tagIsPresent('00100010')", conditionWithPatientName(), Boolean.class);

			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void evaluates_a_boolean_condition_to_false() {
			Object result = ExpressionResult.get("tagIsPresent('00080020')", conditionWithPatientName(), Boolean.class);

			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void resolves_the_Tag_helper_variable() {
			Attributes dcm = new Attributes();
			dcm.setString(Tag.PatientName, VR.PN, "Doe^John");
			ExprAction action = new ExprAction(Tag.PatientName, VR.PN, dcm);

			Object result = ExpressionResult.get("getString(#Tag.PatientName)", action, String.class);

			assertEquals("Doe^John", result);
		}

		@Test
		void returns_an_action_item_from_an_action_expression() {
			Attributes dcm = new Attributes();
			ExprAction action = new ExprAction(Tag.PatientName, VR.PN, dcm);

			Object result = ExpressionResult.get("Keep()", action, org.karnak.backend.model.action.ActionItem.class);

			assertInstanceOf(Keep.class, result);
		}

		@Test
		void throws_illegal_state_on_an_invalid_expression() {
			assertThrows(IllegalStateException.class,
					() -> ExpressionResult.get("this is ((( not valid", conditionWithPatientName(), Boolean.class));
		}

	}

	@Nested
	class IsValid {

		@Test
		void reports_a_valid_expression() {
			ExpressionError error = ExpressionResult.isValid("tagIsPresent('00100010')", conditionWithPatientName(),
					Boolean.class);

			assertTrue(error.isValid());
			assertNull(error.getMsg());
		}

		@Test
		void reports_an_invalid_expression_with_a_message() {
			ExpressionError error = ExpressionResult.isValid("this is ((( not valid", conditionWithPatientName(),
					Boolean.class);

			assertFalse(error.isValid());
			assertTrue(error.getMsg().startsWith("Expression is not valid"));
		}

	}

}