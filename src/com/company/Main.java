public class Main {
    public static void main(String[] args) {
        int numThreads = 100;
        int step = 2;

        java.util.Random rand = new java.util.Random();
        java.util.List<WorkerThread> threads = new java.util.ArrayList<>();
        java.util.List<Integer> delays = new java.util.ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int delay = rand.nextInt(15001) + 5000;
            delays.add(delay);
            threads.add(new WorkerThread(i + 1, step));
            System.out.println("Потік #" + (i + 1) + " буде зупинено через " + delay + " мс");
        }

        for (WorkerThread thread : threads) {
            new Thread(thread).start();
        }

        ControllerThread controller = new ControllerThread(threads, delays);
        controller.start();
    }
}

class WorkerThread implements Runnable {
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

    public void run() {
        System.out.println("Потік #" + id + " запущено.");
        int value = 0;
        while (running) {
            sum += value;
            count++;
            value += step;
        }
        System.out.println("Потік #" + id + ": Сума = " + sum + ", Кількість доданків = " + count);
    }
}

class ThreadInfo {
    public int index;
    public int stopTime;

    public ThreadInfo(int index, int stopTime) {
        this.index = index;
        this.stopTime = stopTime;
    }
}

class ControllerThread {
    private final java.util.List<WorkerThread> threads;
    private final java.util.List<Integer> stopTimes;

    public ControllerThread(java.util.List<WorkerThread> threads, java.util.List<Integer> stopTimes) {
        this.threads = threads;
        this.stopTimes = stopTimes;
    }

    public void start() {
        Thread controllerThread = new Thread(new Runnable() {
            public void run() {
                java.util.List<ThreadInfo> infoList = new java.util.ArrayList<>();

                for (int i = 0; i < threads.size(); i++) {
                    infoList.add(new ThreadInfo(i, stopTimes.get(i)));
                }

                java.util.Collections.sort(infoList, new java.util.Comparator<ThreadInfo>() {
                    public int compare(ThreadInfo a, ThreadInfo b) {
                        return Integer.compare(a.stopTime, b.stopTime);
                    }
                });

                int previousTime = 0;

                for (ThreadInfo info : infoList) {
                    int delay = info.stopTime - previousTime;
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    threads.get(info.index).stopRunning();
                    System.out.println("Потік #" + (info.index + 1) + " зупинено через " + info.stopTime + " мс");
                    previousTime = info.stopTime;
                }
            }
        });

        controllerThread.start();
    }
}
