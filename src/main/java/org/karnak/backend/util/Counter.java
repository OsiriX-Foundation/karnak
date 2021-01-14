package org.karnak.backend.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

  private final AtomicInteger count = new AtomicInteger(0);
  private final int interval;

  public Counter(int interval) {
    this.interval = interval;
  }

  public int getCount() {
    return count.get();
  }

  public void resetCount() {
    this.count.set(0);
  }

  public int increment() {
    return this.count.addAndGet(interval);
  }
}
