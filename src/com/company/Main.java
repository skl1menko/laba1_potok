package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int step = 2;
        int numThreads = 4;
        Random random = new Random();

        List<WorkerThread> threads = new ArrayList<>();
        List<Integer> delays = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            threads.add(new WorkerThread(i + 1, step));
            int delay = (5 + random.nextInt(16)) * 1000; // 5–20 сек
            delays.add(delay);
            System.out.println("Потік #" + (i + 1) + " буде зупинено через " + (delay / 1000) + " секунд.");
        }

        for (WorkerThread thread : threads) {
            thread.start();
        }

        ControllerThread controller = new ControllerThread(threads, delays);
        controller.start();
    }
}
