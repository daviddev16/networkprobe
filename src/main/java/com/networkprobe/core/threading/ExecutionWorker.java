package com.networkprobe.core.threading;

public abstract class ExecutionWorker implements Runnable {

    private volatile boolean state = false;
    private volatile Thread worker;

    private final boolean updatable;
    private final boolean daemon;

    private final String name;

    public ExecutionWorker(String name, boolean updatable, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
        this.updatable = updatable;
    }

    public synchronized void changeState(boolean newState) {
        if (newState != state)
            state = newState;
    }

    public synchronized void start() {
        if (!state) {
            state = true;
            worker = new Thread(this, name);
            worker.setDaemon(daemon);
            worker.start();
        }
    }

    public synchronized void stop() {
        if (state) {
            state = false;
            interrupt(worker);
            worker = null;
        }
    }

    private void interrupt(Thread thread) {
        try {
            thread.interrupt();
        } catch (Exception e) {/* ignore */}
    }

    @Override
    public void run() {
        onBegin();
        while (state && updatable) {
            onUpdate();
        }
        onStop();
    }

    protected abstract void onBegin();
    protected abstract void onUpdate();
    protected abstract void onStop();

}
