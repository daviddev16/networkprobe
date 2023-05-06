package com.networkprobe.core.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public final class SimpleTimeWatch {

    private volatile long startNanoTime;

    public void start() {
        startNanoTime = System.nanoTime();
    }

    public long elapsedTime() {
        return (System.nanoTime() - startNanoTime);
    }

    public String elapsedTimeInMiliseconds() {
        return String.format("%d ms",TimeUnit.NANOSECONDS.toMillis(elapsedTime()));
    }
}
