package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.eemf.sirgoingfar.androidthreadingpolicy.custom.ModuleStatusView;

public class MainActivity extends AppCompatActivity {

    private ModuleStatusView mModuleStatusView;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Enable StrictMode on the UI Thread
        enableStrictMode();

        mModuleStatusView = findViewById(R.id.mdv_course_module_status);
        setModuleStatus();
    }

    private void setModuleStatus() {

        int totalModule = 11;
        int completedModule = 7;

        boolean[] moduleStatus = new boolean[totalModule];

        for(int i = 0; i < completedModule; i++)
            moduleStatus[i] = true;


        mModuleStatusView.setmModuleStatus(moduleStatus);

    }

    /*
    * This should be done only in the Debug Version of the App
    * Instead of specifying all the violating operations individually (as in MTH 1), detectAll() can be used
    * Multiple violation penalties can be specified too
    * */
    private void enableStrictMode() {

        if (BuildConfig.DEBUG) {
            /*
                //MTD 1
                StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                        //Operations to Detect
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        //Violation Discovery Penalties
                        .penaltyLog()
                        .penaltyDeath()
                        .build();
            */

            //MTD 2
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDialog()
                    .build();

            //Set the Thread Policy
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    private void executeTaskInBackground(@NonNull String threadName, @NonNull Runnable task) {

        //create new Thread
        HandlerThread handlerThread = new HandlerThread(threadName, HandlerThread.NORM_PRIORITY);

        //start the Thread
        handlerThread.start();

        //associate the task handler with handlerThread looper
        Handler taskHandler = new Handler(handlerThread.getLooper());

        //post the task in taskHandler Message Queue
        taskHandler.postDelayed(task, 1000);
    }
}
