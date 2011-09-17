package com.iCo6.util;

public class Thrun extends Thread {
    private final Runnable r;

    public Thrun(Runnable r) {
        this.r = r;
    }

    @Override
    public void run() {
        this.r.run();
        interrupt();
    }

    public static void init(Runnable r) {
        Thrun tr = new Thrun(r);
        tr.start();
    }
}
