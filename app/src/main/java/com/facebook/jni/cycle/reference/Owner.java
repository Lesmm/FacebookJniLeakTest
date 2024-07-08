package com.facebook.jni.cycle.reference;

public class Owner {

    private static int kIncrement = 0;
    public int id = kIncrement++;

    public Gift gift;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Owner " + id + " finalized!");
    }
}
