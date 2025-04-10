package com.company;

import java.util.List;

class ControllerThread extends Thread {
    private final List<WorkerThread> threads;
    private final List<Integer> stopTimes;

    public ControllerThread(List<WorkerThread> threads, List<Integer> stopTimes) {
        this.threads = threads;
        this.stopTimes = stopTimes;
    }

    @Override
    public void run() {
        for (int i = 0; i < threads.size(); i++) {
            int delay = stopTimes.get(i);
            WorkerThread thread = threads.get(i);
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    thread.stopRunning();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
