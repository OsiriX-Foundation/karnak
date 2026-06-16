/*
 * Copyright (c) 2021-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.standard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.karnak.backend.model.dicominnolitics.StandardAttributes;
import org.karnak.backend.model.dicominnolitics.JsonAttributes;

public class AttributeDetails {

	private final Map<String, AttributeDetail> mapAttributeDetail;

	public AttributeDetails() {
		mapAttributeDetail = initializeAttributes(StandardAttributes.readJsonAttributes());
	}

	private Map<String, AttributeDetail> initializeAttributes(JsonAttributes[] attributes) {
		Map<String, AttributeDetail> mapAttribute = new HashMap<>();

		for (JsonAttributes attribute : attributes) {
			AttributeDetail attributeDetail = new AttributeDetail(attribute.getId(), attribute.getKeyword(),
					attribute.getName(), attribute.getRetired(), attribute.getTag(), attribute.getValueMultiplicity(),
					attribute.getValueRepresentation());

			String attributeKey = attributeDetail.id();
			mapAttribute.put(attributeKey, attributeDetail);
		}

		return mapAttribute;
	}

	public AttributeDetail getAttributeDetail(String id) {
		return mapAttributeDetail.get(id);
	}

	public List<AttributeDetail> getListAttributeDetail(List<String> listId) {
		return mapAttributeDetail.entrySet()
			.stream()
			.filter(attributeDetail -> listId.contains(attributeDetail.getKey()))
			.map(Map.Entry::getValue)
			.toList();
	}

}
