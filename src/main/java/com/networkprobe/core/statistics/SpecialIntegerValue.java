package com.networkprobe.core.statistics;

public class SpecialIntegerValue extends Number {

    private volatile int value;

    public SpecialIntegerValue(int initialValue) {
        value = initialValue;
    }

    public SpecialIntegerValue() {
        this(0);
    }

    private synchronized void increment(int toIncrement) {
        value += toIncrement;
    }

    public synchronized void increment() {
        increment(1);
    }

    public synchronized void decrease() {
        increment(-1);
    }

    public synchronized int incrementAndGet() {
        increment();
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
