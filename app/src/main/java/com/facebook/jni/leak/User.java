package com.facebook.jni.leak;

import java.lang.ref.PhantomReference;

public class User {

    private String id;

    public PhantomReference<Person> me;

    public User(String id) {
        this.id = id;
        System.out.println("Person " + id + " created!");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Person " + id + " finalized!");
        if (me != null) {
            me.enqueue();
        }
    }
}
