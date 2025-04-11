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
            new Thread(() =>
            {
                List<ThreadInfo> threadInfoList = new List<ThreadInfo>();
                for (int i = 0; i < threads.Count; i++)
                {
                    threadInfoList.Add(new ThreadInfo { Index = i, StopTime = stopTimes[i] });
                }

                threadInfoList.Sort(new ThreadInfoComparer());

                int previousTime = 0;

                foreach (var info in threadInfoList)
                {
                    int delay = info.StopTime - previousTime;
                    Thread.Sleep(delay);
                    threads[info.Index].StopRunning();
                    Console.WriteLine($"Потік #{info.Index + 1} зупинено через {info.StopTime} мс");
                    previousTime = info.StopTime;
                }

            }).Start();
        }

        // Допоміжний клас для збереження інформації про потік
        class ThreadInfo
        {
            public int Index;
            public int StopTime;
        }

        // Класичний компаратор без лямбда-виразів
        class ThreadInfoComparer : IComparer<ThreadInfo>
        {
            public int Compare(ThreadInfo a, ThreadInfo b)
            {
                return a.StopTime.CompareTo(b.StopTime);
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
            List<WorkerThread> threads = new List<WorkerThread>();
            List<int> delays = new List<int>();

            for (int i = 0; i < numThreads; i++)
            {
                int delay = rand.Next(5000, 20001); // 5–20 сек
                delays.Add(delay);
                threads.Add(new WorkerThread(i + 1, step));
                Console.WriteLine($"Потік #{i + 1} буде зупинено через {delay} мс");
            }

            foreach (var thread in threads)
            {
                new Thread(thread.Start).Start();
            }

            ControllerThread controller = new ControllerThread(threads, delays);
            controller.Start();
        }
    }
}
