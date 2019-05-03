package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class AlarmManagerDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private final int MODE_WAKE_DEVICE = 0;
    private final int MODE_DONT_WAKE_DEVICE = 1;
    private String TAG = getClass().getName().concat(" ALARM DEMO");
    private final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager_demo);

        Button btnWakeDeviceSingleAlarm = findViewById(R.id.btn_wake_device);
        Button btnDontWakeDeviceSingleAlarm = findViewById(R.id.btn_dont_wake_device);
        Button btnWakeDeviceRepeatingAlarm = findViewById(R.id.btn_wake_device2);
        Button btnDontWakeDeviceRepeatingAlarm = findViewById(R.id.btn_dont_wake_device2);


        btnDontWakeDeviceSingleAlarm.setOnClickListener(this);
        btnWakeDeviceSingleAlarm.setOnClickListener(this);
        btnWakeDeviceRepeatingAlarm.setOnClickListener(this);
        btnDontWakeDeviceRepeatingAlarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();

        switch (viewId) {

            case R.id.btn_wake_device:
                setupSingleAlarm(MODE_WAKE_DEVICE);
                break;

            case R.id.btn_dont_wake_device:
                setupSingleAlarm(MODE_DONT_WAKE_DEVICE);
                break;

            case R.id.btn_wake_device2:
                setupRepeatingAlarm(MODE_WAKE_DEVICE);
                break;

            case R.id.btn_dont_wake_device2:
                setupRepeatingAlarm(MODE_DONT_WAKE_DEVICE);
                break;
        }
    }

    private void setupSingleAlarm(int alarmMode) {

        //create an Explicit intent to the Receiver class; add appropriate extra data
        Intent intent = new Intent(this, SampleTaskReceiver.class);

        //Alarm accepts Pending Intent, create one
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Get a reference to the Alarm Manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int alarmType;
        if (alarmMode == MODE_DONT_WAKE_DEVICE)
            alarmType = AlarmManager.ELAPSED_REALTIME;
        else
            alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

        //calculate the Alarm time
        long currentTime = SystemClock.elapsedRealtime();
        long waitingTime = 10 /* 10s */ * 1000 /*millisecond converter*/;

        alarmManager.set(alarmType, currentTime + waitingTime, pendingIntent);
        Log.d(TAG, "Alarm is set!");
    }

    private void setupRepeatingAlarm(int alarmMode) {

        //create an Explicit intent to the Receiver class; add appropriate extra data
        Intent intent = new Intent(this, SampleTaskReceiver.class);

        //Alarm accepts Pending Intent, create one
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Get a reference to the Alarm Manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int alarmType;

        if (alarmMode == MODE_DONT_WAKE_DEVICE)
            alarmType = AlarmManager.ELAPSED_REALTIME;
        else
            alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;

        //calculate the Alarm time
        Calendar now = Calendar.getInstance();
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 3);
        alarmStartTime.set(Calendar.MINUTE, 28);
        alarmStartTime.set(Calendar.SECOND, 0);
        Log.d("ALARM", alarmStartTime.toString());
        //be sure the starting time is not a past to the current time
        if(now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1); //start at this time tomorrow
            Log.d("ALARM re-scheduled to:", alarmStartTime.toString());
        }

        alarmManager.setRepeating(alarmType, alarmStartTime.getTimeInMillis(), 10000, pendingIntent);
        Log.d(TAG, "Alarm is set!");
    }
}
