/*
 * Copyright (c) 2021-2026 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.karnak.backend.constant.Notification;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.TransferStatusEntity;
import org.karnak.backend.data.repo.DestinationRepo;
import org.karnak.backend.data.repo.TransferStatusRepo;
import org.karnak.backend.enums.DestinationType;
import org.karnak.backend.model.notification.SerieSummaryNotification;
import org.karnak.backend.model.notification.TransferMonitoringNotification;
import org.karnak.backend.util.SystemPropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.weasis.core.util.StringUtil;

/**
 * Handle notifications
 */
@Service
@Slf4j
public class NotificationService {

	// Maximum length of a single reason rendered in the email summary
	private static final int REASON_EMAIL_MAX_LENGTH = 200;

	@Value("${mail.sender}")
	private String mailSender;

	// Services
	private final TemplateEngine templateEngine;

	private final JavaMailSender javaMailSender;

	// Repositories
	private final TransferStatusRepo transferStatusRepo;

	private final DestinationRepo destinationRepo;

	@Autowired
	public NotificationService(final TemplateEngine templateEngine, final JavaMailSender javaMailSender,
			final TransferStatusRepo transferStatusRepo, final DestinationRepo destinationRepo) {
		this.templateEngine = templateEngine;
		this.javaMailSender = javaMailSender;
		this.transferStatusRepo = transferStatusRepo;
		this.destinationRepo = destinationRepo;
	}

	/**
	 * In a regular period of time, check for new inputs in the transfer notifications,
	 * build the notifications and send them via email.
	 */
	@Scheduled(fixedRate = 120 * 1000)
	public void determineNotificationToSend() {
		buildNotificationsToSend().stream().map(n -> {
			logNotification(n);
			return n;
		}).filter(TransferMonitoringNotification::isEmailSendingEnabled).forEach(this::prepareAndSendNotification);
	}

	/**
	 * Transfer logs
	 * @param transferMonitoringNotification Transfer to log
	 */
	private void logNotification(TransferMonitoringNotification transferMonitoringNotification) {
		// Transfer logs
		log.info("Transfer processed from {} to {}. Details: {}", transferMonitoringNotification.getSource(),
				transferMonitoringNotification.getDestination(),
				transferMonitoringNotification.getSerieSummaryNotifications()
					.stream()
					.map(SerieSummaryNotification::toString)
					.collect(Collectors.joining(",")));
	}

	/** Builds the transfer notifications to send for every eligible destination. */
	List<TransferMonitoringNotification> buildNotificationsToSend() {
		List<TransferMonitoringNotification> transferMonitoringNotifications = new ArrayList<>();
		destinationRepo
			// Retrieve all destinations
			.findAll()
			.stream()
			// Check if notification should be sent for this destinations
			.filter(this::checkDestinationLastVerification)
			.forEach(destinationEntity -> {
				// Keep previous check date
				LocalDateTime previousCheck = destinationEntity.getEmailLastCheck();
				// Update destination last check date
				destinationEntity.setEmailLastCheck(LocalDateTime.now(ZoneId.of("CET")));
				destinationRepo.save(destinationEntity);
				// Retrieve all TransferStatusEntities for this destination after the
				// last email check
				List<TransferStatusEntity> transferStatusEntitiesDestinationsLastCheck = retrieveTransferStatusDestinationLastCheck(
						destinationEntity, previousCheck);
				// Gather TransferStatus by Source and Study: <Source , <Study,
				// List<TransferStatus>>>
				Map<Long, Map<String, List<TransferStatusEntity>>> transferStatusBySourceAndStudy = gatherTransferStatusBySourceAndStudy(
						transferStatusEntitiesDestinationsLastCheck);
				// Build the notifications to send
				buildTransferMonitoringNotifications(transferMonitoringNotifications, transferStatusBySourceAndStudy);
			});
		return transferMonitoringNotifications;
	}

	/** Builds a notification for each (source, study) group and adds it to the list. */
	private void buildTransferMonitoringNotifications(
			List<TransferMonitoringNotification> transferMonitoringNotifications,
			Map<Long, Map<String, List<TransferStatusEntity>>> transferStatusBySourceAndStudy) {
		transferStatusBySourceAndStudy.values()
			.stream()
			.flatMap(byStudy -> byStudy.values().stream())
			.map(this::buildTransferMonitoringNotification)
			.filter(Objects::nonNull)
			.forEach(transferMonitoringNotifications::add);
	}

	/**
	 * Builds the notification for one study (counts, reasons, original vs de-identified
	 * values).
	 */
	private TransferMonitoringNotification buildTransferMonitoringNotification(
			List<TransferStatusEntity> transferStatusEntities) {
		TransferMonitoringNotification transferMonitoringNotification = null;
		Optional<TransferStatusEntity> firstTransferStatusOpt = transferStatusEntities.stream().findFirst();

		if (firstTransferStatusOpt.isPresent()) {
			TransferStatusEntity firstTransferStatus = firstTransferStatusOpt.get();
			transferMonitoringNotification = new TransferMonitoringNotification();

			// If email notification should be sent
			transferMonitoringNotification
				.setEmailSendingEnabled(firstTransferStatus.getDestinationEntity().isActivateNotification());

			// Build the serie notification part
			transferMonitoringNotification.setSerieSummaryNotifications(buildSeriesSummaryNotification(
					transferStatusEntities, firstTransferStatus.getDestinationEntity().isDesidentification()));

			// Has at least one file not transferred
			boolean hasAtLeastOneFileInError = transferMonitoringNotification.getSerieSummaryNotifications()
				.stream()
				.anyMatch(SerieSummaryNotification::isContainsError);

			boolean hasAtLeastOneFileNotSent = transferMonitoringNotification.getSerieSummaryNotifications()
				.stream()
				.anyMatch(ssm -> ssm.getNbTransferNotSent() > 0);

			// Temporary disable send of notification de-identified and in error
			// TODO: have a discussion with business to know how to handle such cases
			if (hasAtLeastOneFileNotSent && firstTransferStatus.getDestinationEntity().isDesidentification()
					&& firstTransferStatus.getDestinationEntity().isActivateNotification()) {
				return null;
			}

			// Check if we should use original or de-identified values
			boolean useOriginalValues = determineUseOfOriginalOrDeIdentifyValues(hasAtLeastOneFileNotSent,
					firstTransferStatus.getDestinationEntity().isDesidentification());

			// Set values in transferMonitoringNotification
			buildTransferMonitoringNotificationSetValues(transferMonitoringNotification, firstTransferStatus,
					hasAtLeastOneFileInError, hasAtLeastOneFileNotSent, useOriginalValues);
		}
		return transferMonitoringNotification;
	}

	/** Populates the notification fields (recipients, study identifiers, subject). */
	private void buildTransferMonitoringNotificationSetValues(
			TransferMonitoringNotification transferMonitoringNotification, TransferStatusEntity transferStatusEntity,
			boolean hasAtLeastOneFileInError, boolean hasAtLeastOneFileRejected, boolean useOriginalValues) {
		transferMonitoringNotification
			.setFrom(SystemPropertyUtil.retrieveSystemProperty("MAIL_SMTP_SENDER", mailSender));
		transferMonitoringNotification.setTo(transferStatusEntity.getDestinationEntity().getNotify());
		transferMonitoringNotification.setPatientId(useOriginalValues ? transferStatusEntity.getPatientIdOriginal()
				: transferStatusEntity.getPatientIdToSend());
		transferMonitoringNotification.setStudyUid(useOriginalValues ? transferStatusEntity.getStudyUidOriginal()
				: transferStatusEntity.getStudyUidToSend());
		transferMonitoringNotification.setAccessionNumber(useOriginalValues
				? transferStatusEntity.getAccessionNumberOriginal() : transferStatusEntity.getAccessionNumberToSend());
		transferMonitoringNotification
			.setStudyDescription(useOriginalValues ? transferStatusEntity.getStudyDescriptionOriginal()
					: transferStatusEntity.getStudyDescriptionToSend());
		transferMonitoringNotification.setStudyDate(useOriginalValues ? transferStatusEntity.getStudyDateOriginal()
				: transferStatusEntity.getStudyDateToSend());
		transferMonitoringNotification.setSource(transferStatusEntity.getForwardNodeEntity().getFwdAeTitle());
		transferMonitoringNotification.setDestination(
				Objects.equals(transferStatusEntity.getDestinationEntity().getDestinationType(), DestinationType.dicom)
						? transferStatusEntity.getDestinationEntity().toStringDicomNotificationDestination()
						: transferStatusEntity.getDestinationEntity().getUrl());
		transferMonitoringNotification.setSubject(buildSubject(hasAtLeastOneFileInError, hasAtLeastOneFileRejected,
				useOriginalValues, transferStatusEntity));
	}

	/**
	 * Builds the email subject: optional error/rejection prefix followed by the
	 * configured pattern.
	 */
	private String buildSubject(boolean hasAtLeastOneFileInError, boolean hasAtLeastOneFileRejected,
			boolean useOriginalValues, TransferStatusEntity transferStatusEntity) {
		StringBuilder subject = new StringBuilder();
		if (hasAtLeastOneFileInError) {
			String errorPrefix = transferStatusEntity.getDestinationEntity().getNotifyObjectErrorPrefix();
			if (errorPrefix != null && !errorPrefix.isEmpty()) {
				subject.append(errorPrefix);
				subject.append(Notification.SPACE);
			}
		}
		else if (hasAtLeastOneFileRejected) {
			String rejectPrefix = transferStatusEntity.getDestinationEntity().getNotifyObjectRejectionPrefix();
			if (rejectPrefix != null && !rejectPrefix.isEmpty()) {
				subject.append(rejectPrefix);
				subject.append(Notification.SPACE);
			}
		}
		subject.append(String.format(transferStatusEntity.getDestinationEntity().getNotifyObjectPattern(),
				buildSubjectValues(useOriginalValues, transferStatusEntity)));
		return subject.toString();
	}

	/**
	 * Builds the subject placeholder values selected by the destination's
	 * notifyObjectValues.
	 */
	private Object[] buildSubjectValues(boolean useOriginalValues, TransferStatusEntity transferStatusEntity) {
		List<String> subjectValues = new ArrayList<>();

		// Determine the values to add to the subject depending on the params set in the
		// destination
		String[] notifyObjectValues = (transferStatusEntity.getDestinationEntity().getNotifyObjectValues() == null
				? Notification.DEFAULT_SUBJECT_VALUES
				: transferStatusEntity.getDestinationEntity().getNotifyObjectValues())
			.split(Notification.COMMA_SEPARATOR);

		// Select the values to add
		for (String notifyObjectValue : notifyObjectValues) {
			if (Objects.equals(Notification.PARAM_PATIENT_ID, notifyObjectValue)) {
				// Patient ID
				subjectValues.add(buildSubjectValue(useOriginalValues, transferStatusEntity.getPatientIdOriginal(),
						transferStatusEntity.getPatientIdToSend()));
			}
			else if (Objects.equals(Notification.PARAM_STUDY_DESCRIPTION, notifyObjectValue)) {
				// Study description
				subjectValues
					.add(buildSubjectValue(useOriginalValues, transferStatusEntity.getStudyDescriptionOriginal(),
							transferStatusEntity.getStudyDescriptionToSend()));
			}
			else if (Objects.equals(Notification.PARAM_STUDY_INSTANCE_UID, notifyObjectValue)) {
				// Study uid
				subjectValues.add(buildSubjectValue(useOriginalValues, transferStatusEntity.getStudyUidOriginal(),
						transferStatusEntity.getStudyUidToSend()));
			}
			else if (Objects.equals(Notification.PARAM_STUDY_DATE, notifyObjectValue)) {
				// Study date
				subjectValues.add(useOriginalValues
						? transferStatusEntity.getStudyDateOriginal() == null ? Notification.EMPTY_STRING
								: transferStatusEntity.getStudyDateOriginal().toString()
						: transferStatusEntity.getStudyDateToSend() == null ? Notification.EMPTY_STRING
								: transferStatusEntity.getStudyDateToSend().toString());
			}
		}
		return subjectValues.toArray();
	}

	/** Returns the original or to-send value, or an empty string when null. */
	private String buildSubjectValue(boolean useOriginalValues, String original, String toSend) {
		return useOriginalValues ? original == null ? Notification.EMPTY_STRING : original
				: toSend == null ? Notification.EMPTY_STRING : toSend;
	}

	/** Builds one summary notification per series (grouped by original series UID). */
	private List<SerieSummaryNotification> buildSeriesSummaryNotification(
			List<TransferStatusEntity> transferStatusEntities, boolean isDestinationDeIdentify) {
		// Group by serie uid; a group is never empty, so getFirst() is safe
		return transferStatusEntities.stream()
			.collect(Collectors.groupingBy(TransferStatusEntity::getSerieUidOriginal))
			.values()
			.stream()
			.map(transfers -> buildSerieSummaryNotification(isDestinationDeIdentify, transfers, transfers.getFirst()))
			.toList();
	}

	/**
	 * Builds the summary (counts, distinct reasons/modalities/SOP classes) for a single
	 * series.
	 */
	private SerieSummaryNotification buildSerieSummaryNotification(boolean isDestinationDeIdentify,
			List<TransferStatusEntity> transfersToEvaluate, TransferStatusEntity transferStatusEntity) {
		SerieSummaryNotification serieSummaryNotification = new SerieSummaryNotification();
		// Number transfers sent
		serieSummaryNotification
			.setNbTransferSent(transfersToEvaluate.stream().filter(TransferStatusEntity::isSent).count());
		// Number transfers not sent
		serieSummaryNotification.setNbTransferNotSent(transfersToEvaluate.stream().filter(t -> !t.isSent()).count());
		// Any of the transfer contain an error
		serieSummaryNotification.setContainsError(transfersToEvaluate.stream().anyMatch(TransferStatusEntity::isError));
		// Distinct reasons (truncated for the email summary: the full reason stays in the
		// database and is shown in the monitoring view)
		serieSummaryNotification.setUnTransferedReasons(transfersToEvaluate.stream()
			.map(TransferStatusEntity::getReason)
			.filter(Objects::nonNull)
			.map(reason -> StringUtil.getTruncatedString(reason, REASON_EMAIL_MAX_LENGTH, StringUtil.Suffix.THREE_PTS))
			.collect(Collectors.toSet()));
		// Distinct modalities
		serieSummaryNotification.setTransferredModalities(transfersToEvaluate.stream()
			.map(TransferStatusEntity::getModality)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet()));
		// Distinct transfer syntax
		serieSummaryNotification.setTransferredSopClassUid(transfersToEvaluate.stream()
			.map(TransferStatusEntity::getSopClassUid)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet()));

		// Flag to know if we should use original or de-identify values
		boolean useOriginalValues = determineUseOfOriginalOrDeIdentifyValues(
				serieSummaryNotification.getNbTransferNotSent() > 0, isDestinationDeIdentify);
		serieSummaryNotification.setSerieUid(useOriginalValues ? transferStatusEntity.getSerieUidOriginal()
				: transferStatusEntity.getSerieUidToSend());
		serieSummaryNotification
			.setSerieDescription(useOriginalValues ? transferStatusEntity.getSerieDescriptionOriginal()
					: transferStatusEntity.getSerieDescriptionToSend());
		serieSummaryNotification.setSerieDate(useOriginalValues ? transferStatusEntity.getSerieDateOriginal()
				: transferStatusEntity.getSerieDateToSend());
		return serieSummaryNotification;
	}

	/**
	 * Original values are used when a transfer failed or the destination is not
	 * de-identified.
	 */
	private boolean determineUseOfOriginalOrDeIdentifyValues(boolean hasTransferNotSent,
			boolean isDestinationDeIdentify) {
		return hasTransferNotSent || !isDestinationDeIdentify;
	}

	/** Groups transfer statuses by source (forward node) then by original study UID. */
	private Map<Long, Map<String, List<TransferStatusEntity>>> gatherTransferStatusBySourceAndStudy(
			List<TransferStatusEntity> transferStatusEntities) {
		return transferStatusEntities.stream()
			.collect(Collectors.groupingBy(TransferStatusEntity::getForwardNodeId,
					Collectors.groupingBy(TransferStatusEntity::getStudyUidOriginal)));
	}

	/**
	 * Returns the transfer statuses for the destination created after the last email
	 * check.
	 */
	private List<TransferStatusEntity> retrieveTransferStatusDestinationLastCheck(DestinationEntity destinationEntity,
			LocalDateTime lastCheck) {
		return lastCheck == null ? transferStatusRepo.findByDestinationId(destinationEntity.getId())
				: transferStatusRepo.findByDestinationIdAndTransferDateAfter(destinationEntity.getId(), lastCheck);
	}

	/**
	 * True when notifications are enabled, no transfer is in progress, and both the extra
	 * series-completion delay and the destination's notify interval have elapsed.
	 */
	private boolean checkDestinationLastVerification(DestinationEntity destinationEntity) {
		return !destinationEntity.isTransferInProgress() && destinationEntity.getLastTransfer() != null
		// Add extra timer delay in order to insure transfer of serie is over
				&& destinationEntity.getLastTransfer()
					.plusSeconds(Notification.EXTRA_TIMER_DELAY)
					.isBefore(LocalDateTime.now(ZoneId.of("CET")))
				&& (destinationEntity.getEmailLastCheck() == null || destinationEntity.getEmailLastCheck()
					.plusSeconds(destinationEntity.getNotifyInterval().longValue())
					.isBefore(LocalDateTime.now(ZoneId.of("CET"))));
	}

	/** Renders the Thymeleaf template and sends the notification email. */
	private void prepareAndSendNotification(TransferMonitoringNotification transferMonitoringNotification) {
		Context context = new Context();
		context.setVariable(Notification.CONTEXT_THYMELEAF, transferMonitoringNotification);
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			helper.setSubject(transferMonitoringNotification.getSubject());
			helper.setText(templateEngine.process(Notification.TEMPLATE_THYMELEAF, context), true);
			helper.setTo(InternetAddress.parse(transferMonitoringNotification.getTo()));
			helper.setFrom(transferMonitoringNotification.getFrom());
		}
		catch (MessagingException e) {
			log.error("Notification error when preparing email to send: {}", e.getMessage());
		}

		try {
			javaMailSender.send(mimeMessage);
		}
		catch (Exception e) {
			log.error("Notification error when sending email: {}", e.getMessage());
		}

	}

}
