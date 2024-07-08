package com.facebook.jni.leak;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.jni.cycle.reference.Gift;
import com.facebook.jni.cycle.reference.Owner;
import com.facebook.jni.leak.databinding.ActivityMainBinding;
import com.facebook.soloader.nativeloader.NativeLoader;
import com.facebook.soloader.nativeloader.SystemDelegate;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'leak' library on application startup.
    static {
        NativeLoader.init(new SystemDelegate());
        System.loadLibrary("leak");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        binding.buttonGc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Calling gc...");
                System.gc();
            }
        });

        binding.buttonLeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 10; i++) {
                    VideoPipeline pipeline = new VideoPipeline(i, 300);
                    pipeline.isManualDeleteHybridData = false;
                    Log.i("MainActivity", i + ", VideoPipeline: " + pipeline);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pipeline.close();
                        }
                    }, 5000);
                }
            }
        });

        binding.buttonNoLeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 10; i++) {
                    VideoPipeline pipeline = new VideoPipeline(i, 300);
                    pipeline.isManualDeleteHybridData = true;
                    Log.i("MainActivity", i + ", VideoPipeline: " + pipeline);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pipeline.close();
                        }
                    }, 5000);
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Calling gc...");
                        System.gc();
                    }
                }, 7000);
            }
        });

        binding.buttonLeakCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferenceQueue<VideoPipeline> referenceQueue = new ReferenceQueue<>();
                for (int i = 100; i < 200; i++) {
                    VideoPipeline pipeline = new VideoPipeline(i, 300);
                    pipeline.isManualDeleteHybridData = true;
//                    WeakReference<VideoPipeline> weakReference = new WeakReference<>(pipeline, referenceQueue);
                    PhantomReference<VideoPipeline> phantomReference = new PhantomReference<>(pipeline, referenceQueue);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pipeline.close();
                        }
                    }, 5000);
                }

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
                            Reference<? extends VideoPipeline> reclaimedObj = referenceQueue.poll();
                            if (referenceQueue.poll() != null) {  /// Failed to checked ....
                                System.out.println(
                                        "✅ " + "VideoPipeline" + " object has been reclaimed by the garbage collector"
                                );
//                                break;
                            }
                        }
                    }
                }).start();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(
                                "########### Please click GC button for calling System.gc() manually ###########"
                        );
                    }
                }, 7000);
            }
        });

        binding.buttonQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        com.facebook.jni.leak.Queue.main(null);
                    }
                }).start();
            }
        });

        binding.buttonReferenceLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Test Reference Loop Start...");
                Owner owner = new Owner();
                Gift gift = new Gift();
                owner.gift = gift;
                gift.owner = owner;
                owner = null;
                System.gc();
            }
        });
    }

    /**
     * A native method that is implemented by the 'leak' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}