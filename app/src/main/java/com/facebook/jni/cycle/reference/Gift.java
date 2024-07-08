package com.facebook.jni.cycle.reference;

public class Gift {

    private static int kIncrement = 0;
    public int id = kIncrement++;

    public Owner owner;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Gift " + id + " finalized!");
    }
}
