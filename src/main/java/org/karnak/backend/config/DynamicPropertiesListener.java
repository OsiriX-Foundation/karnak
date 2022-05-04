package org.karnak.backend.config;

import java.util.UUID;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

public class DynamicPropertiesListener implements ApplicationListener<ApplicationStartingEvent> {

  private final static UUID applicationInstanceId = UUID.randomUUID();

  public void onApplicationEvent(ApplicationStartingEvent event) {
    System.setProperty("spring.application.instance_id", applicationInstanceId.toString());
    System.out.println("initialized spring.application.instance_id " + applicationInstanceId);
  }

}
