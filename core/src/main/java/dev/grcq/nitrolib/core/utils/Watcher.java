package dev.grcq.nitrolib.core.utils;

public class Watcher {

    private final Runnable runnable;
    private final long delay;
    private final boolean async;
    private boolean running = false;
    private Thread thread;

    public Watcher(Runnable runnable, long delay, boolean async) {
        this.runnable = runnable;
        this.delay = delay;
        this.async = async;
    }

    public Watcher(Runnable runnable, long delay) {
        this(runnable, delay, false);
    }

    public Watcher(Runnable runnable, boolean async) {
        this(runnable, 0, async);
    }

    public Watcher(Runnable runnable) {
        this(runnable, 0, false);
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(() -> {
            while (running) {
                try {
                    if (async) {
                        new Thread(runnable).start();
                    } else {
                        runnable.run();
                    }
                    if (delay > 0) Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void stop() {
        if (!running) return;
        running = false;
        thread.interrupt();
    }

    public boolean isRunning() {
        return running;
    }

}
