/*
 * Copyright (c) 2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.model.validation;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Curated validation rules supplementing the innolitics DICOM standard JSON (which has no
 * machine-readable enumerated values or SOP Class / Modality coherence data). Loaded from
 * the bundled {@code curated-validation-rules.json} resource — see
 * {@code doc/dicom-standard-json.md} for the source and update procedure.
 */
public class CuratedValidationRules {

	/**
	 * Fallback source label used when the bundled JSON resource omits the {@code source}
	 * field (e.g. an older rules file).
	 */
	public static final String DEFAULT_DICOM_STANDARD_SOURCE = "innolitics/dicom-standard JSON";

	private static final String RESOURCE = "curated-validation-rules.json";

	/**
	 * Source and vintage of the bundled DICOM standard JSON files, shown in report
	 * headers. Read from the {@code source} field of
	 * {@code curated-validation-rules.json} so it lives next to the data it describes —
	 * update it there when re-vendoring the standard files (see
	 * doc/dicom-standard-json.md).
	 */
	private String source;

	private Map<String, EnumeratedRule> enumeratedValues;

	private Map<String, List<String>> sopClassToModalities;

	private List<String> retiredTransferSyntaxes;

	private Map<String, ConditionalRequirement> conditionalRequirements;

	public static CuratedValidationRules load() {
		try (InputStream in = CuratedValidationRules.class.getResourceAsStream(RESOURCE);
				Reader reader = new InputStreamReader(Objects.requireNonNull(in, RESOURCE), StandardCharsets.UTF_8)) {
			return new Gson().fromJson(reader, CuratedValidationRules.class);
		}
		catch (Exception e) {
			throw new JsonParseException("Cannot parse json %s correctly".formatted(RESOURCE), e);
		}
	}

	/**
	 * Source and vintage label of the bundled DICOM standard JSON, for report headers.
	 */
	public String getDicomStandardSource() {
		return source == null || source.isBlank() ? DEFAULT_DICOM_STANDARD_SOURCE : source;
	}

	public Map<String, EnumeratedRule> getEnumeratedValues() {
		return enumeratedValues == null ? Map.of() : enumeratedValues;
	}

	public Map<String, List<String>> getSopClassToModalities() {
		return sopClassToModalities == null ? Map.of() : sopClassToModalities;
	}

	public List<String> getRetiredTransferSyntaxes() {
		return retiredTransferSyntaxes == null ? List.of() : retiredTransferSyntaxes;
	}

	/**
	 * Machine-evaluable Type 1C/2C conditions keyed by {@code moduleId/tagPath},
	 * supplementing the free-text conditions of the standard JSON. See
	 * {@link org.karnak.backend.model.validation.ConditionEvaluator}.
	 */
	public Map<String, ConditionalRequirement> getConditionalRequirements() {
		return conditionalRequirements == null ? Map.of() : conditionalRequirements;
	}

}
