using System;
using System.Collections.Generic;
using System.Threading;

namespace Company
{
    class ControllerThread
    {
        private readonly List<WorkerThread> threads;
        private readonly List<int> stopTimes;

        public ControllerThread(List<WorkerThread> threads, List<int> stopTimes)
        {
            this.threads = threads;
            this.stopTimes = stopTimes;
        }

        public void Start()
        {
            for (int i = 0; i < threads.Count; i++)
            {
                int delay = stopTimes[i];
                var thread = threads[i];
                new Thread(() =>
                {
                    Thread.Sleep(delay);
                    thread.StopRunning();
                }).Start();
            }
        }
    }

    class WorkerThread
    {
        private readonly int id;
        private readonly int step;
        private bool running = true;
        private long sum = 0;
        private int count = 0;

        public WorkerThread(int id, int step)
        {
            this.id = id;
            this.step = step;
        }

        public void StopRunning()
        {
            running = false;
        }

        public void Start()
        {
            Console.WriteLine($"Потік #{id} запущено.");
            int value = 0;
            while (running)
            {
                sum += value;
                count++;
                value += step;
                Thread.Sleep(10);
            }
            Console.WriteLine($"Потік #{id}: Сума = {sum}, Кількість доданків = {count}");
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            int numThreads = 100;
            int step = 2;

            Random rand = new Random();
            List<int> delays = new List<int>();
            List<WorkerThread> threads = new List<WorkerThread>();

            for (int i = 0; i < numThreads; i++)
            {
                int delay = rand.Next(5000, 20001); // Від 5 до 20 секунд у мілісекундах
                delays.Add(delay);
                threads.Add(new WorkerThread(i + 1, step));
                Console.WriteLine($"Потік #{i + 1} буде зупинено через {delay} мс");
            }

            // Запуск усіх потоків
            foreach (var thread in threads)
            {
                new Thread(thread.Start).Start();
            }

            // Керуючий потік для зупинки
            ControllerThread controller = new ControllerThread(threads, delays);
            controller.Start();
        }
    }
}
