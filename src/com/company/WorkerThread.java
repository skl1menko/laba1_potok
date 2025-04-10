package com.company;

class WorkerThread extends Thread {
    private final int id;
    private final int step;
    private volatile boolean running = true;
    private long sum = 0;
    private int count = 0;

    public WorkerThread(int id, int step) {
        this.id = id;
        this.step = step;
    }

    public void stopRunning() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("Потік #" + id + " запущено.");
        int value = 0;
        while (running) {
            sum += value;
            count++;
            value += step;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Потік #" + id + ": Сума = " + sum + ", Кількість доданків = " + count);
    }
}
