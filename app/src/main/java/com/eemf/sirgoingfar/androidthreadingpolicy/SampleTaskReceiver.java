package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class SampleTaskReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getName().concat(" ALARM DEMO");

    @Override
    public void onReceive(Context context, Intent intent) {

        //Your task/operation details come in here
        Log.d(TAG, "Job done!");
    }
}
