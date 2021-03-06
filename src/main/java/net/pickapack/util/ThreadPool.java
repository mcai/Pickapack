/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.util;

import net.pickapack.io.cmd.CommandLineHelper;

import java.util.LinkedList;
import java.util.Random;

/**
 * Thread pool.
 *
 * @author Min Cai
 */
public class ThreadPool {
    private BlockingQueue queue = new BlockingQueue();
    private boolean closed = true;
    private int size;

    /**
     * Create a thread pool of the specified size.
     *
     * @param size the size of the thread pool
     */
    public ThreadPool(int size) {
        this.size = size;
    }

    /**
     * Open the thread pool.
     */
    public synchronized void open() {
        if (!this.closed) {
            throw new IllegalStateException("Pool already started.");
        }
        this.closed = false;
        for (int i = 0; i < this.size; ++i) {
            new PooledThread().start();
        }
    }

    /**
     * Enqueue the specified runnable in the thread pool.
     *
     * @param job the job as a runnable
     */
    public synchronized void execute(Runnable job) {
        if (this.closed) {
            throw new PoolClosedException();
        }
        this.queue.enqueue(job);
    }

    private class PooledThread extends Thread {
        public void run() {
            while (true) {
                Runnable job = (Runnable) queue.dequeue();
                if (job == null) {
                    break;
                }
                try {
                    job.run();
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
    }

    /**
     * Close the thread pool.
     */
    public void close() {
        this.closed = true;
        this.queue.close();
    }

    /**
     * Get the size of the thread pool.
     *
     * @return the size of the thread pool
     */
    public int getSize() {
        return size;
    }

    private static class PoolClosedException extends RuntimeException {
        PoolClosedException() {
            super("Pool closed.");
        }
    }

    class BlockingQueue {
        private LinkedList<Object> list = new LinkedList<Object>();
        private boolean closed = false;
        private boolean wait = false;

        public synchronized void enqueue(Object o) {
            if (closed) {
                throw new ClosedException();
            }
            list.add(o);
            notify();
        }

        public synchronized Object dequeue() {
            while (!closed && list.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
            if (list.size() == 0) {
                return null;
            }
            return list.removeFirst();
        }

        public synchronized int size() {
            return list.size();
        }

        public synchronized void close() {
            closed = true;
            notifyAll();
        }

        public synchronized void open() {
            closed = false;
        }
    }

    /**
     *
     */
    public class ClosedException extends RuntimeException {
        ClosedException() {
            super("Queue closed.");
        }
    }

    /**
     * Entry point.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        System.out.println("Total memory: " + CommandLineHelper.invokeShellCommandAndGetResult("cat /proc/meminfo | grep MemTotal").get(0).split("\\s+")[1] + "KB");
        System.out.println("Free memory: " + CommandLineHelper.invokeShellCommandAndGetResult("cat /proc/meminfo | grep MemFree").get(0).split("\\s+")[1] + "KB");

        int numProcessors = Integer.parseInt(CommandLineHelper.invokeShellCommandAndGetResult("cat /proc/cpuinfo | grep -c processor").get(0));

        System.out.println("Using " + numProcessors + " threads to execute jobs...");

        ThreadPool threadPool = new ThreadPool(numProcessors);
        threadPool.open();

        final Random random = new Random();

        for (int i = 0; i < 100; i++) {
            final int finalI = i;
            threadPool.execute(new Runnable() {
                public void run() {
                    System.out.println("Executing job #" + finalI + "...");

                    try {
                        Thread.sleep(random.nextInt(5) * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Job #" + finalI + " done.");
                }
            });
        }

        threadPool.close();
    }
}