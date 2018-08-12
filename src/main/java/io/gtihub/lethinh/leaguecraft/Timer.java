/*
 * Copyright (c) 2018 to Le Thinh
 */

package io.gtihub.lethinh.leaguecraft;

import java.util.concurrent.TimeUnit;

public class Timer {

    private long lastMS;

    public Timer() {
        this.lastMS = this.getCurrentMS();
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public void setLastMS(long lastMSIn) {
        this.lastMS = lastMSIn;
    }

    public void reset() {
        this.setLastMS(this.getCurrentMS());
    }

    public long getCurrentMS() {
        return System.nanoTime() / (long) 1E6;
    }

    public boolean isDelayComplete(long ms) {
        return this.getCurrentMS() - this.getLastMS() >= ms;
    }

    public boolean isDelayComplete(long duration, TimeUnit source) {
        return this.getCurrentMS() - this.getLastMS() >= source.toMillis(duration);
    }

}
