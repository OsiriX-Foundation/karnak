package org.karnak.backend.service;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service

public class MemoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MemoryService.class);


  @Scheduled(fixedRate = 10 * 1000)
  public void callGarbageCollector() {
    LOGGER.info("Memory:%d \t \t %d \t \t %d".formatted(Runtime.getRuntime().freeMemory(),
        Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory()));

    System.gc();
    System.runFinalization();
    System.gc();
    System.runFinalization();

    LOGGER.info("Memory:%d \t \t %d \t \t %d".formatted(Runtime.getRuntime().freeMemory(),
        Runtime.getRuntime().totalMemory(), Runtime.getRuntime().maxMemory()));
    try {
      TimeUnit.MILLISECONDS.sleep(200);
    } catch (InterruptedException et) {
      Thread.currentThread().interrupt();
    }
  }

}
