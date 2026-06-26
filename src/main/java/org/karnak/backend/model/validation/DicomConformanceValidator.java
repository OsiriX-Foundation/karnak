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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.util.TagUtils;
import org.jspecify.annotations.NullUnmarked;
import org.karnak.backend.exception.SOPNotFoundException;
import org.karnak.backend.model.standard.AttributeDetail;
import org.karnak.backend.model.standard.Module;
import org.karnak.backend.model.standard.ModuleAttribute;
import org.karnak.backend.model.standard.StandardDICOM;

/**
 * Validates a single DICOM instance against the IOD of its SOP Class, using the bundled
 * machine-readable DICOM standard ({@link StandardDICOM}) plus
 * {@link CuratedValidationRules} for enumerated values.
 *
 * <p>
 * Scope and known limits: conditions (Type 1C/2C, usage C) are free text in the standard
 * JSON. Where {@link CuratedValidationRules} provides a machine-evaluable predicate that
 * resolves to {@link Ternary#TRUE} for the instance, the conditional attribute is treated
 * as mandatory; otherwise (no curated rule, or a clinical/free-text condition) it is only
 * validated when actually present. Private tags are validated only for block structure
 * (PS3.5 §7.8: a private data element must have a non-empty private creator reserving its
 * block); their values are not validated against vendor dictionaries. Tag paths deeper
 * than one sequence level are skipped. VR checks are skipped for Implicit VR Little
 * Endian datasets (the VR would come from the dictionary, making the check circular).
 */
@NullUnmarked
public class DicomConformanceValidator {

	private static final String TYPE_1 = "1";

	private static final String TYPE_1C = "1C";

	private static final String TYPE_2 = "2";

	private static final String TYPE_2C = "2C";

	private static final Set<VR> VM_CHECK_EXCLUDED_VRS = EnumSet.of(VR.SQ, VR.OB, VR.OW, VR.OD, VR.OF, VR.OL, VR.OV,
			VR.UN);

	private static final int MAX_VALUE_SUMMARY_LENGTH = 64;

	private static final int MAX_LISTED_MODULE_ATTRIBUTES = 8;

	/**
	 * Default sequence recursion depth of the dataset sweep: the top-level dataset plus
	 * the first level of sequence items. The deep-sequence-validation option raises this
	 * to walk the whole SR content tree, enhanced-multiframe functional groups, etc.
	 */
	public static final int DEFAULT_MAX_SEQUENCE_DEPTH = 1;

	private final StandardDICOM standard;

	private final CuratedValidationRules rules;

	public DicomConformanceValidator(StandardDICOM standard, CuratedValidationRules rules) {
		this.standard = standard;
		this.rules = rules;
	}

	/**
	 * Validates one instance, without the optional value-content conformity check.
	 */
	public InstanceValidationResult validate(Attributes attrs, Set<Integer> bulkPresentTags, String transferSyntaxUid) {
		return validate(attrs, bulkPresentTags, transferSyntaxUid, false);
	}

	/**
	 * Validates one instance with the default sequence recursion depth.
	 */
	public InstanceValidationResult validate(Attributes attrs, Set<Integer> bulkPresentTags, String transferSyntaxUid,
			boolean checkValueConformity) {
		return validate(attrs, bulkPresentTags, transferSyntaxUid, checkValueConformity, DEFAULT_MAX_SEQUENCE_DEPTH);
	}

	/**
	 * Validates one instance.
	 * @param attrs the dataset actually sent (metadata only, bulk values stripped)
	 * @param bulkPresentTags top-level tags whose value was present but stripped — they
	 * are treated as present and non-empty
	 * @param transferSyntaxUid transfer syntax the instance was sent with
	 * @param checkValueConformity when true, also validates that string values obey their
	 * VR length and format rules (PS3.5 §6.2) — opt-in, as real-world data deviates often
	 * @param maxSequenceDepth how many sequence levels the dataset sweep recurses into
	 * ({@link #DEFAULT_MAX_SEQUENCE_DEPTH} = one level; higher walks SR content trees and
	 * functional groups). Must not exceed the depth the {@code MetadataSnapshot}
	 * retained.
	 */
	public InstanceValidationResult validate(Attributes attrs, Set<Integer> bulkPresentTags, String transferSyntaxUid,
			boolean checkValueConformity, int maxSequenceDepth) {
		String sopClassUid = attrs.getString(Tag.SOPClassUID);
		String sopInstanceUid = attrs.getString(Tag.SOPInstanceUID);
		List<ConformanceFinding> findings = new ArrayList<>();

		try {
			Map<Module, Map<String, ModuleAttribute>> modules = standard.getModulesBySOP(sopClassUid);
			checkModuleRequirements(attrs, bulkPresentTags, modules, findings);
		}
		catch (SOPNotFoundException e) {
			// The bundled standard has no IOD for this SOP Class. The dcm4che registry
			// distinguishes a retired class (name suffixed " (Retired)") from an unknown
			// one
			String name = sopClassUid == null ? null : UID.nameOf(sopClassUid);
			if (name != null && name.contains("(Retired)")) {
				findings.add(new ConformanceFinding(TagUtils.toString(Tag.SOPClassUID), "SOP Class UID", null,
						Severity.WARNING, CheckKind.RETIRED_SOP_CLASS, "A non-retired SOP Class",
						"%s is retired: IOD module checks skipped".formatted(name)));
			}
			else {
				findings.add(new ConformanceFinding(TagUtils.toString(Tag.SOPClassUID), "SOP Class UID", null,
						Severity.WARNING, CheckKind.UNKNOWN_SOP_CLASS, "A SOP Class known to the DICOM standard",
						"Unknown SOP Class %s: IOD module checks skipped".formatted(sopClassUid)));
			}
		}

		boolean implicitVr = UID.ImplicitVRLittleEndian.equals(transferSyntaxUid);
		sweepDataset(attrs, bulkPresentTags, "", 0, implicitVr, checkValueConformity, maxSequenceDepth, findings);

		return new InstanceValidationResult(sopClassUid, sopInstanceUid, List.copyOf(findings));
	}

	/**
	 * Checks IOD module presence and the Type requirements of the attributes of every
	 * module that applies.
	 */
	private void checkModuleRequirements(Attributes attrs, Set<Integer> bulkPresentTags,
			Map<Module, Map<String, ModuleAttribute>> modules, List<ConformanceFinding> findings) {
		// Same attribute may belong to several applicable modules: gather them and
		// resolve the effective (stricter) type afterwards
		Map<String, List<ModuleAttribute>> requirements = new HashMap<>();
		for (Map.Entry<Module, Map<String, ModuleAttribute>> entry : modules.entrySet()) {
			Module module = entry.getKey();
			Map<String, ModuleAttribute> moduleAttributes = entry.getValue();
			if (moduleAttributes == null || moduleAttributes.isEmpty()) {
				continue;
			}
			if (isModulePresent(attrs, moduleAttributes)) {
				moduleAttributes.values()
					.forEach(attribute -> requirements
						.computeIfAbsent(attribute.getTagPath(), tagPath -> new ArrayList<>())
						.add(attribute));
			}
			else if (Module.moduleIsMandatory(module)) {
				String requiredAttributes = summarizeRequiredAttributes(moduleAttributes);
				// A mandatory module whose attributes are all optional/conditional may
				// legitimately be entirely absent
				if (!requiredAttributes.isEmpty()) {
					findings.add(new ConformanceFinding(null, module.id(), module.id(), Severity.ERROR,
							CheckKind.MODULE_MISSING, "Mandatory module (usage M)",
							"None of its attributes is present. Required attributes: " + requiredAttributes));
				}
			}
			// Usage U/C modules are validated only when present: their free-text
			// conditions cannot be evaluated automatically
		}
		requirements.forEach((tagPath, moduleAttributes) -> checkAttributeRequirement(attrs, bulkPresentTags, tagPath,
				moduleAttributes, findings));
	}

	/** A module is considered present when any of its top-level attributes exists. */
	private boolean isModulePresent(Attributes attrs, Map<String, ModuleAttribute> moduleAttributes) {
		return moduleAttributes.keySet()
			.stream()
			.filter(tagPath -> !tagPath.contains(":"))
			.map(DicomConformanceValidator::parseTag)
			.anyMatch(tag -> tag != null && attrs.contains(tag));
	}

	private String summarizeRequiredAttributes(Map<String, ModuleAttribute> moduleAttributes) {
		List<String> required = moduleAttributes.values()
			.stream()
			.filter(attribute -> TYPE_1.equals(attribute.getType()) || TYPE_2.equals(attribute.getType()))
			.map(attribute -> attributeName(parseTag(firstSegment(attribute.getTagPath()))))
			.distinct()
			.sorted()
			.toList();
		if (required.size() <= MAX_LISTED_MODULE_ATTRIBUTES) {
			return String.join(", ", required);
		}
		return String.join(", ", required.subList(0, MAX_LISTED_MODULE_ATTRIBUTES))
				+ ", … (%d more)".formatted(required.size() - MAX_LISTED_MODULE_ATTRIBUTES);
	}

	private void checkAttributeRequirement(Attributes attrs, Set<Integer> bulkPresentTags, String tagPath,
			List<ModuleAttribute> moduleAttributes, List<ConformanceFinding> findings) {
		String[] segments = tagPath.split(":");
		if (segments.length > 2) {
			// Deeper nesting is out of scope
			return;
		}
		String type = ModuleAttribute.getStricterType(moduleAttributes);
		if (type == null) {
			return;
		}
		// A Type 1C/2C attribute whose curated condition is satisfied is mandatory for
		// this
		// instance: promote it to the equivalent unconditional Type so the checks below
		// fire
		type = effectiveConditionalType(attrs, type, moduleAttributes);
		String moduleId = moduleAttributes.stream()
			.map(ModuleAttribute::getModuleId)
			.distinct()
			.sorted()
			.collect(Collectors.joining(", "));
		Integer tag = parseTag(segments[0]);
		if (tag == null) {
			// Repeating-group placeholder paths like 60xx3000 are skipped
			return;
		}
		if (segments.length == 1) {
			checkType(attrs, bulkPresentTags, tag, null, type, moduleId, findings);
			return;
		}
		// Path inside a sequence: checked in every item when the sequence is present
		Integer childTag = parseTag(segments[1]);
		Sequence sequence = attrs.getSequence(tag);
		if (childTag == null || sequence == null || sequence.isEmpty()) {
			return;
		}
		for (Attributes item : sequence) {
			if (checkType(item, Set.of(), childTag, tag, type, moduleId, findings)) {
				// One finding per instance is enough, whatever the number of items
				break;
			}
		}
	}

	/**
	 * Resolves the effective Type of a conditional (1C/2C) attribute for this instance.
	 * When a curated condition tied to a module that contributes the conditional type
	 * evaluates to {@link Ternary#TRUE}, the attribute is mandatory, so 1C is promoted to
	 * Type 1 and 2C to Type 2. Otherwise (no curated rule, or the condition is
	 * FALSE/UNKNOWN) the conditional type is kept and the attribute is only validated
	 * when present.
	 */
	private String effectiveConditionalType(Attributes attrs, String type, List<ModuleAttribute> moduleAttributes) {
		if (!TYPE_1C.equals(type) && !TYPE_2C.equals(type)) {
			return type;
		}
		for (ModuleAttribute attribute : moduleAttributes) {
			if (!type.equals(attribute.getType())) {
				continue;
			}
			ConditionalRequirement requirement = rules.getConditionalRequirements()
				.get(attribute.getModuleId() + "/" + attribute.getTagPath());
			if (requirement == null) {
				continue;
			}
			if (ConditionEvaluator.evaluate(attrs, requirement.getRequiredWhen()) == Ternary.TRUE) {
				return TYPE_1C.equals(type) ? TYPE_1 : TYPE_2;
			}
		}
		return type;
	}

	/**
	 * @return true when a finding was added
	 */
	private boolean checkType(Attributes attrs, Set<Integer> bulkPresentTags, int tag, Integer parentTag, String type,
			String moduleId, List<ConformanceFinding> findings) {
		boolean present = attrs.contains(tag);
		boolean nonEmpty = attrs.containsValue(tag) || bulkPresentTags.contains(tag);
		String display = displayPath(parentTag, tag);
		String name = attributeName(tag);
		switch (type) {
			case TYPE_1 -> {
				if (!present) {
					findings.add(new ConformanceFinding(display, name, moduleId, Severity.ERROR,
							CheckKind.TYPE1_MISSING, "Type 1: present with a value", "Attribute is missing"));
					return true;
				}
				if (!nonEmpty) {
					findings.add(new ConformanceFinding(display, name, moduleId, Severity.ERROR, CheckKind.TYPE1_EMPTY,
							"Type 1: present with a value", "Attribute is present but empty"));
					return true;
				}
			}
			case TYPE_2 -> {
				if (!present) {
					findings.add(new ConformanceFinding(display, name, moduleId, Severity.WARNING,
							CheckKind.TYPE2_MISSING, "Type 2: present, possibly empty", "Attribute is missing"));
					return true;
				}
			}
			case TYPE_1C -> {
				if (present && !nonEmpty) {
					findings.add(new ConformanceFinding(display, name, moduleId, Severity.ERROR, CheckKind.TYPE1_EMPTY,
							"Type 1C: non-empty when present", "Attribute is present but empty"));
					return true;
				}
			}
			default -> {
				// Type 2C missing/empty and Type 3 are conformant in absence of an
				// evaluable condition
			}
		}
		return false;
	}

	/**
	 * Sweeps the actual dataset content: VR correctness, VM bounds, enumerated values,
	 * retired attributes, private-block structure and (optionally) value-content
	 * conformity, recursing into sequence items down to {@code maxSequenceDepth} levels.
	 */
	private void sweepDataset(Attributes attrs, Set<Integer> bulkPresentTags, String parentPath, int depth,
			boolean implicitVr, boolean checkValueConformity, int maxSequenceDepth, List<ConformanceFinding> findings) {
		checkPrivateBlocks(attrs, parentPath, findings);
		for (int tag : attrs.tags()) {
			if (TagUtils.isGroupLength(tag) || TagUtils.isPrivateGroup(tag) || bulkPresentTags.contains(tag)) {
				continue;
			}
			VR vr = attrs.getVR(tag);
			String display = parentPath + TagUtils.toString(tag);
			String id = TagUtils.toHexString(tag).toLowerCase(Locale.ROOT);
			var detail = standard.getAttributeDetail(id);

			if (detail != null && "Y".equalsIgnoreCase(detail.retired())) {
				findings.add(
						new ConformanceFinding(display, detail.name(), null, Severity.INFO, CheckKind.RETIRED_ATTRIBUTE,
								"Attribute is retired in the current DICOM standard", "Attribute is used"));
			}

			checkVr(detail, vr, display, implicitVr, findings);

			if (!VM_CHECK_EXCLUDED_VRS.contains(vr) && attrs.containsValue(tag)) {
				String[] values = attrs.getStrings(tag);
				if (values != null && values.length > 0) {
					checkVm(detail, values.length, display, findings);
					checkEnumeratedValues(id, values, display, findings);
					if (checkValueConformity) {
						checkValueFormat(tag, vr, values, display, findings);
					}
				}
			}

			if (vr == VR.SQ && depth < maxSequenceDepth) {
				Sequence sequence = attrs.getSequence(tag);
				if (sequence != null) {
					for (Attributes item : sequence) {
						sweepDataset(item, Set.of(), display + " > ", depth + 1, implicitVr, checkValueConformity,
								maxSequenceDepth, findings);
					}
				}
			}
		}
	}

	/**
	 * Optional value-content conformity check: verifies that each string value obeys the
	 * length and format/character-repertoire rules of its VR ({@link VrValueRules}). At
	 * most one length and one format finding is raised per attribute to keep reports
	 * readable.
	 */
	private void checkValueFormat(int tag, VR vr, String[] values, String display, List<ConformanceFinding> findings) {
		boolean lengthReported = false;
		boolean formatReported = false;
		for (String raw : values) {
			if (raw == null) {
				continue;
			}
			String value = trimPadding(raw);
			if (value.isEmpty()) {
				continue;
			}
			if (!lengthReported) {
				String expectation = VrValueRules.lengthExpectation(vr, value);
				if (expectation != null) {
					// Small / structured fields are commonly rejected by receivers when
					// too
					// long, so they are an ERROR; long free-text overflow stays a WARNING
					Severity severity = VrValueRules.lengthOverflowIsError(vr) ? Severity.ERROR : Severity.WARNING;
					findings.add(new ConformanceFinding(display, attributeName(tag), null, severity,
							CheckKind.VALUE_TOO_LONG, expectation, value.length() + " characters"));
					lengthReported = true;
				}
			}
			if (!formatReported) {
				String expectation = VrValueRules.formatExpectation(vr, value);
				if (expectation != null) {
					findings.add(new ConformanceFinding(display, attributeName(tag), null, Severity.WARNING,
							CheckKind.VALUE_FORMAT, expectation, summarize(value)));
					formatReported = true;
				}
			}
			if (lengthReported && formatReported) {
				break;
			}
		}
	}

	/** Strips DICOM trailing padding (space / null) before applying value rules. */
	private static String trimPadding(String value) {
		int end = value.length();
		while (end > 0 && (value.charAt(end - 1) == ' ' || value.charAt(end - 1) == '\0')) {
			end--;
		}
		return value.substring(0, end);
	}

	/**
	 * Checks the structure of the private blocks of one dataset (PS3.5 §7.8): every
	 * private data element must belong to a block reserved by a non-empty Private Creator
	 * Data Element. Vendor-specific values are not interpreted — only the block structure
	 * is verified, which needs no private dictionary.
	 */
	private void checkPrivateBlocks(Attributes attrs, String parentPath, List<ConformanceFinding> findings) {
		Set<Integer> reportedOrphanBlocks = new LinkedHashSet<>();
		for (int tag : attrs.tags()) {
			if (TagUtils.isGroupLength(tag) || !isPrivateTag(tag)) {
				continue;
			}
			int element = tag & 0xFFFF;
			if (isPrivateCreatorElement(element)) {
				// Private Creator Data Element (gggg,0010-00FF) must carry an identifier
				if (!attrs.containsValue(tag)) {
					findings.add(new ConformanceFinding(parentPath + TagUtils.toString(tag), "Private Creator", null,
							Severity.WARNING, CheckKind.PRIVATE_CREATOR_INVALID,
							"A non-empty private creator identifier", "Private Creator is present but empty"));
				}
				continue;
			}
			int block = (element >>> 8) & 0xFF;
			if (block < 0x10) {
				// (gggg,0001-000F) and blocks below 0x10 are reserved, not standard
				// private
				// data element blocks — out of scope
				continue;
			}
			int creatorTag = (tag & 0xFFFF0000) | block;
			// Presence (not value): a present-but-empty creator is reported separately as
			// PRIVATE_CREATOR_INVALID when its own element is reached
			if (!attrs.contains(creatorTag) && reportedOrphanBlocks.add(creatorTag)) {
				findings.add(new ConformanceFinding(parentPath + TagUtils.toString(creatorTag), "Private Creator", null,
						Severity.WARNING, CheckKind.PRIVATE_CREATOR_MISSING, "A Private Creator reserving this block",
						"No Private Creator defines this private block"));
			}
		}
	}

	/** Private data elements live in odd groups. */
	private static boolean isPrivateTag(int tag) {
		return (tag & 0x00010000) != 0;
	}

	/** Private Creator Data Elements occupy elements (gggg,0010) to (gggg,00FF). */
	private static boolean isPrivateCreatorElement(int element) {
		return (element & 0xFF00) == 0 && (element & 0x00FF) >= 0x10;
	}

	private void checkVr(AttributeDetail detail, VR vr, String display, boolean implicitVr,
			List<ConformanceFinding> findings) {
		if (detail == null || detail.valueRepresentation() == null || implicitVr) {
			return;
		}
		Set<String> expected = Set.of(detail.valueRepresentation().split(" or "));
		if (expected.contains(vr.name())) {
			return;
		}
		if (vr == VR.UN) {
			findings.add(new ConformanceFinding(display, detail.name(), null, Severity.INFO, CheckKind.VR_MISMATCH,
					"VR " + detail.valueRepresentation(), "VR UN (unknown)"));
		}
		else {
			findings.add(new ConformanceFinding(display, detail.name(), null, Severity.ERROR, CheckKind.VR_MISMATCH,
					"VR " + detail.valueRepresentation(), "VR " + vr.name()));
		}
	}

	private void checkVm(AttributeDetail detail, int count, String display, List<ConformanceFinding> findings) {
		if (detail == null) {
			return;
		}
		VmSpec vmSpec = VmSpec.parse(detail.valueMultiplicity());
		if (vmSpec != null && !vmSpec.matches(count)) {
			findings.add(new ConformanceFinding(display, detail.name(), null, Severity.ERROR, CheckKind.VM_VIOLATION,
					"VM " + detail.valueMultiplicity(), count + " value(s)"));
		}
	}

	private void checkEnumeratedValues(String id, String[] values, String display, List<ConformanceFinding> findings) {
		EnumeratedRule rule = rules.getEnumeratedValues().get(id);
		if (rule == null) {
			return;
		}
		List<String> allowed = rule.getValues();
		Set<String> invalid = new LinkedHashSet<>();
		for (String value : values) {
			String trimmed = value == null ? "" : value.trim();
			if (!trimmed.isEmpty() && !allowed.contains(trimmed)) {
				invalid.add(trimmed);
			}
		}
		if (!invalid.isEmpty()) {
			// A value outside a closed Enumerated Values set is a standard violation
			// (ERROR);
			// an unexpected Defined Term is only a deviation worth a WARNING
			Severity severity = rule.isClosed() ? Severity.ERROR : Severity.WARNING;
			findings.add(new ConformanceFinding(display, attributeName(parseTag(id)), null, severity,
					CheckKind.ENUMERATED_VALUE, "One of " + allowed, summarize(String.join("\\", invalid))));
		}
	}

	private String attributeName(Integer tag) {
		if (tag == null) {
			return "Unknown attribute";
		}
		var detail = standard.getAttributeDetail(TagUtils.toHexString(tag).toLowerCase(Locale.ROOT));
		if (detail != null && detail.name() != null) {
			return detail.name();
		}
		String keyword = ElementDictionary.keywordOf(tag, null);
		return keyword == null || keyword.isEmpty() ? TagUtils.toString(tag) : keyword;
	}

	private static String displayPath(Integer parentTag, int tag) {
		return parentTag == null ? TagUtils.toString(tag)
				: TagUtils.toString(parentTag) + " > " + TagUtils.toString(tag);
	}

	private static String firstSegment(String tagPath) {
		int separator = tagPath.indexOf(':');
		return separator < 0 ? tagPath : tagPath.substring(0, separator);
	}

	private static Integer parseTag(String hexTag) {
		if (hexTag == null) {
			return null;
		}
		try {
			return (int) Long.parseLong(hexTag, 16);
		}
		catch (NumberFormatException e) {
			// Repeating-group placeholders like "60xx3000" cannot be parsed
			return null;
		}
	}

	private static String summarize(String value) {
		return value.length() <= MAX_VALUE_SUMMARY_LENGTH ? value : value.substring(0, MAX_VALUE_SUMMARY_LENGTH) + "…";
	}

}
