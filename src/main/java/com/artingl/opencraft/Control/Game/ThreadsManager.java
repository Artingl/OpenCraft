package com.artingl.opencraft.Control.Game;

import com.artingl.opencraft.Logger.Logger;
import com.artingl.opencraft.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadsManager {

    private class Task {
        public boolean done;
        public Runnable runnable;

        public Task(Runnable runnable) {
            this.runnable = runnable;
            this.done = false;
        }
    }

    private int MAX_AVAILABLE_THREADS;

    private final Thread[] threads;
    private final ConcurrentHashMap<Integer, ConcurrentLinkedDeque<Task>> tasks;

    public ThreadsManager() {
        MAX_AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors() / 3;
        if (MAX_AVAILABLE_THREADS < 1) {
            MAX_AVAILABLE_THREADS = 1;
        }

        threads = new Thread[MAX_AVAILABLE_THREADS];
        tasks = new ConcurrentHashMap<>();

        for (int i = 0; i < MAX_AVAILABLE_THREADS; i++) {
            tasks.put(i, new ConcurrentLinkedDeque<>());
            int threadId = i;
            threads[i] = new Thread(() -> {
                while (true) {
                    try {
                        for (int j = 0; j < tasks.get(threadId).size(); j++) {
                            Task task = tasks.get(threadId).pollFirst();

                            if (task != null) {
                                if (!task.done) {
                                    task.runnable.run();
                                    task.done = true;
                                }
                            }

                            Utils.sleep(2);
                        }
                    } catch (Exception e) {
                        Logger.exception("Error in thread " + Thread.currentThread().getName() + "!", e);
                    }
                }
            });

            threads[i].setDaemon(true);
            threads[i].start();
        }

        Logger.debug("Loaded " + getAvailableThreadsCount() + " threads!");
    }

    public void execute(Runnable runnable) {
        int smallest = 0;
        int id = -1;

        for (int i = 0; i < MAX_AVAILABLE_THREADS; i++) {
            if (smallest > tasks.get(i).size() || id == -1) {
                smallest = tasks.get(i).size();
                id = i;
            }
        }

        tasks.get(id).add(new Task(runnable));
    }

    public Thread[] getThreads() {
        return threads;
    }

    public int getAvailableThreadsCount() {
        return MAX_AVAILABLE_THREADS;
    }
}
