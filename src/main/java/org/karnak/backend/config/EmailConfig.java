/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import java.util.Objects;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.weasis.core.util.StringUtil;

@Configuration
public class EmailConfig {

  @Bean
  public JavaMailSender getJavaMailSender() {

    // retrieve system properties
    String mailSmtpPort = retrieveSystemProperty("MAIL_SMTP_PORT", "0");
    String mailSmtpUser = retrieveSystemProperty("MAIL_SMTP_USER", null);
    String mailAuthType = retrieveSystemProperty("MAIL_SMTP_TYPE", null);

    // Configure JavaMailSender
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(retrieveSystemProperty("MAIL_SMTP_HOST", null));
    mailSender.setPort(Integer.parseInt(mailSmtpPort));
    mailSender.setUsername(mailSmtpUser);
    mailSender.setPassword(retrieveSystemProperty("MAIL_SMTP_SECRET", null));

    // Additional properties
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");

    // Value with authentication should be "SSL" or "STARTTLS"
    if (StringUtil.hasText(mailAuthType)) {
      props.put("mail.smtp.auth", "true");
      if (Objects.equals("SSL", mailAuthType)) {
        props.put("mail.smtp.socketFactory.port", mailSmtpPort); // SSL Port
        props.put(
            "mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
        props.put("mail.smtp.ssl.checkserveridentity", true);
      } else {
        props.put("mail.smtp.starttls.enable", "true");
      }
      props.setProperty("mail.smtp.submitter", mailSmtpUser);
    }
    return mailSender;
  }

  /**
   * Retrieve system property
   *
   * @param key Key
   * @param defaultValue default value
   * @return property found
   */
  private String retrieveSystemProperty(String key, String defaultValue) {
    String val = System.getProperty(key);
    if (!StringUtil.hasText(val)) {
      val = System.getenv(key);
      if (!StringUtil.hasText(val)) {
        return defaultValue;
      }
    }
    return val;
  }
}
