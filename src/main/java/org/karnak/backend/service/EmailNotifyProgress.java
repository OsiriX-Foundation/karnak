/*
* Copyright (c) 2020-2021 Karnak Team and other contributors.
*
* This program and the accompanying materials are made available under the terms of the Eclipse
* Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0, or the Apache
* License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*/
package org.karnak.backend.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.karnak.backend.model.NotificationSetUp;
import org.karnak.backend.model.Series;
import org.karnak.backend.model.Study;
import org.karnak.backend.service.gateway.GatewaySetUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.util.StringUtil;
import org.weasis.dicom.param.DicomProgress;
import org.weasis.dicom.param.ForwardDestination;
import org.weasis.dicom.param.ProgressListener;

public class EmailNotifyProgress implements ProgressListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotifyProgress.class);

  private final ScheduledThreadPoolExecutor checkProcess;
  private final StreamRegistry streamRegistry;
  private final ForwardDestination forwardDestination;
  private final String[] emailList;
  private final GatewaySetUp config;
  private final NotificationSetUp notificationSetUp;

  public EmailNotifyProgress(
      StreamRegistry streamRegistry,
      ForwardDestination forwardDestination,
      String[] emails,
      GatewaySetUp config,
      NotificationSetUp notificationSetUp) {
    this.streamRegistry = Objects.requireNonNull(streamRegistry);
    this.forwardDestination = Objects.requireNonNull(forwardDestination);
    this.config = Objects.requireNonNull(config);
    this.emailList = emails;
    this.notificationSetUp = notificationSetUp;
    if (emails != null && emails.length > 0) {
      this.streamRegistry.setEnable(true);
      this.checkProcess = new ScheduledThreadPoolExecutor(1);
      int interval =
          notificationSetUp == null
              ? config.getNotificationSetUp().getNotifyInterval()
              : notificationSetUp.getNotifyInterval();
      this.checkProcess.scheduleAtFixedRate(
          this::checkNotification, interval, interval, TimeUnit.SECONDS);
    } else {
      this.checkProcess = null;
    }
  }

  @Override
  public void handleProgression(DicomProgress progress) {
    streamRegistry.update(progress);
  }

  public ForwardDestination getForwardDestination() {
    return forwardDestination;
  }

  public String[] getEmailList() {
    return emailList;
  }

  protected void checkNotification() {
    if (streamRegistry.isEnable()) {
      Iterator<Entry<String, Study>> studyIt = streamRegistry.getEntrySet().iterator();
      while (studyIt.hasNext()) {
        Study study = studyIt.next().getValue();
        long currentTime = System.currentTimeMillis();
        long lastnotif = study.getTimeStamp();
        int interval =
            notificationSetUp == null
                ? config.getNotificationSetUp().getNotifyInterval()
                : notificationSetUp.getNotifyInterval();
        boolean notify = (currentTime - lastnotif) > (interval * 1000);

        if (notify) {
          StringBuilder message = new StringBuilder("\nPatientID: ");
          write(message, study.getPatientID());
          // Do not send by email patient name
          if (study.getOtherPatientIDs() != null && study.getOtherPatientIDs().length > 0) {
            message.append("\nOtherPatientIDs: ");
            message.append(Arrays.toString(study.getOtherPatientIDs()));
          }
          message.append("\nStudy UID: ");
          write(message, study.getStudyInstanceUID());
          message.append("\nAccessionNumber: ");
          write(message, study.getAccessionNumber());
          message.append("\nStudy description: ");
          write(message, study.getStudyDescription());
          message.append("\nStudy date: ");
          write(message, study.getStudyDate());
          message.append("\n\nList of Series transfered from [");
          write(message, forwardDestination.getForwardDicomNode());
          message.append("] to [");
          write(message, forwardDestination);
          message.append("]:");
          boolean warn = false;
          Collection<Series> seriesList = study.getSeries();
          int msgLength = message.length();
          for (Series series : seriesList) {
            message.append("\n\n\tSeries UID: ");
            write(message, series.getSeriesInstanceUID());
            message.append("\n\tSeries description: ");
            write(message, series.getSeriesDescription());
            message.append("\n\tSeries date: ");
            write(message, series.getSeriesDate());
            message.append("\n\tNumber of transmitted DICOM files: ");
            int unsent = (int) series.getSopInstances().stream().filter(s -> !s.isSent()).count();
            message.append(series.getSopInstances().size() - unsent);
            if (unsent > 0) {
              warn = true;
              message.append("\n\t******** WARNING ******** ");
              message.append(unsent);
              message.append(" file(s) has not been forwarded to the final destination");
            }
          }
          if (message.length() > msgLength) {
            NotificationSetUp notif =
                notificationSetUp == null ? config.getNotificationSetUp() : notificationSetUp;
            StringBuilder title = new StringBuilder();
            if (warn) {
              title.append(notif.getNotifyObjectErrorPrefix());
              title.append(" ");
            }
            Object[] args = buildObjectValue(notif.getNotifyObjectValues(), study);
            title.append(String.format(notif.getNotifyObjectPattern(), args));

            try {
              postMail(title.toString(), message.toString());
            } catch (Exception e) {
              LOGGER.info(
                  "Cannot send email notification to {}. Subject: {}",
                  Arrays.toString(emailList),
                  title,
                  e);
            }
          }
          studyIt.remove();
        }
      }
    }
  }

  private void write(StringBuilder message, Object value) {
    if (value != null) {
      message.append(value);
    }
  }

  private Object[] buildObjectValue(String[] notifyObjectValues, Study study) {
    Object[] vals = new Object[notifyObjectValues.length];
    for (int i = 0; i < notifyObjectValues.length; i++) {
      if ("PatientID".equals(notifyObjectValues[i])) {
        vals[i] = study.getPatientID();
      } else if ("StudyDescription".equals(notifyObjectValues[i])) {
        vals[i] = study.getStudyDescription();
      } else if ("StudyInstanceUID".equals(notifyObjectValues[i])) {
        vals[i] = study.getStudyInstanceUID();
      } else if ("StudyDate".equals(notifyObjectValues[i])) {
        vals[i] = study.getStudyDate();
      }
    }
    return vals;
  }

  protected void postMail(String subject, String message) throws Exception {
    String smtpHost = config.getSmtpHost();
    if (!StringUtil.hasText(smtpHost)) {
      LOGGER.warn(
          "Cannot send mail to {}, no smtp host in configuration!", Arrays.toString(emailList));
      return;
    }

    Authenticator authenticator = null;
    Properties props = new Properties();
    props.setProperty("mail.smtp.host", smtpHost);
    props.setProperty("mail.smtp.port", config.getSmtpPort());

    // Value with authentication should be "SSL" or "STARTTLS"
    if (StringUtil.hasText(config.getMailAuthType())) {
      if ("SSL".equals(config.getMailAuthType())) {
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", config.getSmtpPort()); // SSL Port
        props.put(
            "mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
        props.put("mail.smtp.ssl.checkserveridentity", true);
      } else {
        props.put("mail.smtp.auth", "true"); // enable authentication
        props.put("mail.smtp.starttls.enable", "true");
      }

      String username = config.getMailAuthUser();
      String password = config.getMailAuthPwd();
      authenticator =
          new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(username, password);
            }
          };
      props.setProperty("mail.smtp.submitter", username);
    }

    // create some properties and get the default Session
    Session session = Session.getInstance(props, authenticator);
    session.setDebug(false);

    // create a message
    Message msg = new MimeMessage(session);

    // set the from and to address
    InternetAddress addressFrom = new InternetAddress("karnak@kehops.online");
    msg.setFrom(addressFrom);

    InternetAddress[] addressTo = new InternetAddress[emailList.length];
    for (int i = 0; i < emailList.length; i++) {
      addressTo[i] = new InternetAddress(emailList[i].trim());
    }
    msg.setRecipients(Message.RecipientType.TO, addressTo);

    // Optional : You can also set your custom headers in the Email if you Want
    // msg.addHeader("MyHeaderName", "myHeaderValue");

    // Setting the Subject and Content Type
    msg.setSubject(subject);
    msg.setContent(message, "text/plain");
    Transport.send(msg);
    LOGGER.info("Email notification sent to {}. Subject: {}", addressTo, subject);
  }
}
