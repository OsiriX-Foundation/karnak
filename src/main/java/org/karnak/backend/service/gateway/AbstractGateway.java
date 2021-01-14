package org.karnak.backend.service.gateway;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.karnak.backend.util.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.weasis.core.util.FileUtil;

public abstract class AbstractGateway {

  public static final Marker notifyAdmin = MarkerFactory.getMarker("NOTIFY_ADMIN");
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGateway.class);
  protected final Counter iterationCount;
  protected final GatewaySetUp config;

  private final ScheduledThreadPoolExecutor gatewayProcess;
  protected volatile long lastErrorNotification;

  protected AbstractGateway(GatewaySetUp config) {
    this.config = config;
    this.iterationCount = new Counter(config.getIntervalCheck());
    this.lastErrorNotification = 0;
    this.gatewayProcess = new ScheduledThreadPoolExecutor(1);
  }

  public static void deleteOldFiles(File dir) {
    if ((dir == null) || !dir.isDirectory()) {
      return;
    }
    File[] filesAndDirs = dir.listFiles();
    if (filesAndDirs != null) {
      for (File file : filesAndDirs) {
        if (file.isDirectory()) {
          deleteOldFiles(file);
          // Do not deleting AETsource directories, because of possible concomitant arrivals
        }
        // When the file is older than 3 days, it can be deleted
        else if ((System.currentTimeMillis() - file.lastModified()) > 259200000) {
          LOGGER.info("Deleting old file: {}", file);
          FileUtil.delete(file);
        }
      }
    }
  }

  public static boolean isFolderContainsFile(File dir) {
    if ((dir == null) || !dir.isDirectory()) {
      return false;
    }
    File[] filesAndDirs = dir.listFiles();
    for (File file : filesAndDirs) {
      if (file.isDirectory()) {
        if (isFolderContainsFile(file)) {
          return true;
        }
      } else {
        // .part extension means that file is not complete.
        if (!file.getPath().endsWith(".part")) {
          return true;
        }
      }
    }
    return false;
  }

  public synchronized void start() {
    if (!gatewayProcess.isTerminated()) {
      stop();
    }
    gatewayProcess.scheduleAtFixedRate(this::check, 0, config.getIntervalCheck(), TimeUnit.SECONDS);
  }

  public synchronized void stop() {
    gatewayProcess.shutdown();
  }

  protected abstract void check();
}