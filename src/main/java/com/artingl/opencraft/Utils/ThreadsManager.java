package com.artingl.opencraft.Utils;

import com.artingl.opencraft.Logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

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
    private final HashMap<Integer, ArrayList<Task>> tasks;

    public ThreadsManager() {
        MAX_AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors() / 2;
        if (MAX_AVAILABLE_THREADS < 1) {
            MAX_AVAILABLE_THREADS = 1;
        }

        threads = new Thread[MAX_AVAILABLE_THREADS];
        tasks = new HashMap<>();

        for (int i = 0; i < MAX_AVAILABLE_THREADS; i++) {
            tasks.put(i, new ArrayList<>());
            int threadId = i;
            threads[i] = new Thread(() -> {
                while (true) {
                    try {
                        ArrayList<Task> list = new ArrayList<>(tasks.get(threadId));
                        for (Task task: list) {
                            task.runnable.run();
                            task.done = true;
                        }

                        Thread.sleep(10);
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
        int smallest = Integer.MAX_VALUE;
        int id = -1;

        for (int i = 0; i < MAX_AVAILABLE_THREADS; i++) {
            ArrayList<Task> values = tasks.get(i);
            values.removeIf(task -> task.done);
            tasks.put(i, values);

            if (smallest > values.size()) {
                id = i;
                smallest = values.size();
            }
        }

        if (id == -1) {
            id = 0;
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
