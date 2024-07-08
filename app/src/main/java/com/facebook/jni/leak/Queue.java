package com.facebook.jni.leak;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

// https://www.cnblogs.com/xfeiyun/p/17328267.html
class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("🚸 Person object " + name + " is finalizing...");
        super.finalize();
        System.out.println("🚸 Person object " + name + " is finalized!");
    }
}

public class Queue {
    static boolean isSuccessReclaimed = false;

    public static void main(String[] args) {
        isSuccessReclaimed = false;

        ReferenceQueue<Person> referenceQueue = new ReferenceQueue<>();
        Person person = new Person("[Tomas]");
        WeakReference<Person> weakReference = new WeakReference<>(person, referenceQueue);
//        PhantomReference<Person> phantomReference = new PhantomReference<>(person, referenceQueue);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Wait for the garbage collector to finalize the Person object
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("checking for garbage collector (daemon)...");

//                    Person reclaimedPerson = weakReference.get();
//                    if (reclaimedPerson == null) {
//                        System.out.println("✅Person object has been reclaimed by the garbage collector (daemon)");
//                        isSuccessReclaimed = true;
//                        break;
//                    }

                    Reference<? extends Person> reclaimedPerson = referenceQueue.poll();
                    if (reclaimedPerson != null) {
                        System.out.println("✅Person object has been reclaimed by the garbage collector");
                        isSuccessReclaimed = true;
                        break;
                    }
                }
            }
        }).start();


        int count = 0;
        while (true) {
            count++;
            System.out.println(count + ". checking for garbage collector (main)...");

//            Person reclaimedPerson = weakReference.get();
//            if (reclaimedPerson == null) {
//                System.out.println("✅Person object has been reclaimed by the garbage collector");
//                isSuccessReclaimed = true;
//                break;
//            }

//            Reference<? extends Person> reclaimedPerson = referenceQueue.poll();
//            if (reclaimedPerson != null) {
//                System.out.println("✅Person object has been reclaimed by the garbage collector (main)");
//                isSuccessReclaimed = true;
//                break;
//            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Make the Person object eligible for garbage collection
            // Request the JVM to perform garbage collection
            if (count == 1) {
                System.out.println("Person object is being dereferenced");
                person = null; // Make the Person object eligible for garbage collection
                System.gc(); // Request the JVM to perform garbage collection
            }

            if (isSuccessReclaimed) {
                System.out.println("💯 Done! Exiting the program!!!");
                break;
            }
        }
    }
}