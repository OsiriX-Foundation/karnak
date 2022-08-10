package org.karnak.backend.service;

import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MemoryService {

  @Scheduled(fixedRate = 30 * 1000)
  public void callGarbageCollector() {
    System.gc();
    System.runFinalization();
    System.gc();
    System.runFinalization();
    try {
      TimeUnit.MILLISECONDS.sleep(200);
    } catch (InterruptedException et) {
      Thread.currentThread().interrupt();
    }
  }

}
