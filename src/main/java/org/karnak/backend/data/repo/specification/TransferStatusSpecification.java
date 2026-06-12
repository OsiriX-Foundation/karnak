/*
 * Copyright (c) 2022 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.data.repo.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.karnak.backend.data.entity.TransferStatusEntity;
import org.karnak.backend.enums.TransferStatusType;
import org.karnak.frontend.monitoring.component.TransferStatusFilter;
import org.springframework.data.jpa.domain.Specification;

/**
 * Transfer status specification: used to look for entries depending on criteria
 */
public class TransferStatusSpecification implements Specification<TransferStatusEntity> {

	@Serial
	private static final long serialVersionUID = -939448741462690254L;

	private static final String LIKE = "%";

	private final TransferStatusFilter transferStatusFilter;

	public TransferStatusSpecification(TransferStatusFilter transferStatusFilter) {
		this.transferStatusFilter = transferStatusFilter;
	}

	@Override
	public Predicate toPredicate(Root<TransferStatusEntity> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder) {
		List<Predicate> predicates = new ArrayList<>();
		if (transferStatusFilter != null) {
			addLikePredicate(criteriaBuilder, predicates, transferStatusFilter.getStudyUid(),
					root.get("studyUidOriginal"), root.get("studyUidToSend"));
			addLikePredicate(criteriaBuilder, predicates, transferStatusFilter.getSerieUid(),
					root.get("serieUidOriginal"), root.get("serieUidToSend"));
			addLikePredicate(criteriaBuilder, predicates, transferStatusFilter.getSopInstanceUid(),
					root.get("sopInstanceUidOriginal"), root.get("sopInstanceUidToSend"));
			addStatusPredicates(criteriaBuilder, predicates, root.get("sent"), root.get("error"));
			addTransferDatePredicate(criteriaBuilder, predicates, root.get("transferDate"));
		}
		return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	}

	/** Adds a LIKE match of the value on either the original or the to-send path. */
	private void addLikePredicate(CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String value,
			Path<String> original, Path<String> toSend) {
		if (StringUtils.isNotBlank(value)) {
			String pattern = LIKE + value.trim() + LIKE;
			predicates.add(
					criteriaBuilder.or(criteriaBuilder.like(original, pattern), criteriaBuilder.like(toSend, pattern)));
		}
	}

	/**
	 * Adds the sent (and, except for NOT_SENT, error) equality predicates for the
	 * selected status.
	 */
	private void addStatusPredicates(CriteriaBuilder criteriaBuilder, List<Predicate> predicates, Path<Boolean> pSent,
			Path<Boolean> pError) {
		TransferStatusType type = transferStatusFilter.getTransferStatusType();
		if (type != null && type != TransferStatusType.ALL) {
			predicates.add(criteriaBuilder.equal(pSent, type.getSent()));
			if (type != TransferStatusType.NOT_SENT) {
				predicates.add(criteriaBuilder.equal(pError, type.getError()));
			}
		}
	}

	/** Adds a transfer-date predicate: lower bound, upper bound, or a valid range. */
	private void addTransferDatePredicate(CriteriaBuilder criteriaBuilder, List<Predicate> predicates,
			Path<LocalDateTime> pTransferDate) {
		LocalDateTime start = transferStatusFilter.getStart();
		LocalDateTime end = transferStatusFilter.getEnd();
		if (start != null && end == null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(pTransferDate, start));
		}
		else if (start == null && end != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(pTransferDate, end));
		}
		else if (start != null && end != null && start.isBefore(end)) {
			predicates.add(criteriaBuilder.between(pTransferDate, start, end));
		}
	}

}
