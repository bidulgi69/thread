package com.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    protected static List<Double> doubles = new ArrayList<>();
    protected static Lock lock = new Lock();
    protected static boolean finished = false;

    static class Lock {
        private int lock;
        public Lock() {
            lock = 0;
        }

        public synchronized void getLock() {
            lock = 1;
        }

        public synchronized void unlock() {
            lock = 0;
        }

        public synchronized boolean can() {
            return lock == 0;
        }
    }

    public static void main(String[] args) {
    // write your code here
        try {
            Producer producer1 = new Producer();
            Producer producer2 = new Producer();
            Consumer consumer1 = new Consumer();
            Consumer consumer2 = new Consumer();

            producer1.start();
            producer2.start();
            consumer1.start();
            consumer2.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            finished = true;
        }
    }

    static synchronized void produce() {
        double d = Math.random() * 1000;
        doubles.add(d);
        System.out.println("produce: " + doubles.toString() + " + produces: " + d);
    }

    static synchronized void consume() {
        int random = (int) (Math.random() * doubles.size());
        Iterator<Double> iter = doubles.iterator();
        double d = 0.0d;
        while (iter.hasNext()) {
            d = iter.next();
            if (d == doubles.get(random)) {
                iter.remove();
                break;
            }
        }
        System.out.println("consume: " + doubles.toString() + " - popped: " + d);
    }

    static class Producer extends Thread {
        @Override
        public void run() {
            while (!finished) {
                if (doubles.size() < 2) {
                    if (lock.can()) {
                        lock.getLock();
                        produce();
                        lock.unlock();
                    } else Thread.yield();
                }
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            while (!finished) {
                if (doubles.size() > 0) {
                    if (lock.can()) {
                        lock.getLock();
                        consume();
                        lock.unlock();
                    } else Thread.yield();
                }
            }
        }
    }
}
