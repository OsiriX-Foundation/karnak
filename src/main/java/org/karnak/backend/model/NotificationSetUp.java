package org.karnak.backend.model;

public class NotificationSetUp {

  private final String notifyObjectErrorPrefix;
  private final String notifyObjectPattern;
  private final String[] notifyObjectValues;
  private final int notifyInterval;

  public NotificationSetUp(String notifyObjectErrorPrefix, String notifyObjectPattern,
      String[] notifyObjectValues, int notifyInterval) {
    super();
    this.notifyObjectErrorPrefix = notifyObjectErrorPrefix;
    this.notifyObjectPattern = notifyObjectPattern;
    this.notifyObjectValues = notifyObjectValues;
    this.notifyInterval = notifyInterval;
  }

    public String getNotifyObjectErrorPrefix() {
        return notifyObjectErrorPrefix;
    }

    public String getNotifyObjectPattern() {
        return notifyObjectPattern;
    }

    public String[] getNotifyObjectValues() {
        return notifyObjectValues;
    }

    public int getNotifyInterval() {
        return notifyInterval;
    }

}
