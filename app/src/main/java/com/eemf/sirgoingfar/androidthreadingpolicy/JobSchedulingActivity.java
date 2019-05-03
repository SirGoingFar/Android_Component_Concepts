package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.ParseException;

public class JobSchedulingActivity extends AppCompatActivity {

    private final int NOTE_UPLOAD_JOB_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_scheduling);

        //this can be called from anywhere
        scheduleNoteUpload();
    }

    private void scheduleNoteUpload() {

        ComponentName componentName = new ComponentName(this, NoteUploadJobService.class);

        //Job Data
        PersistableBundle jobPersistableBundle = new PersistableBundle();
        jobPersistableBundle.putString(NoteUploadJobService.EXTRA_NOTE_URI, Uri.parse("put a valid URI here").toString());

        //set job info
        JobInfo noteUploadJobInfo = new JobInfo.Builder(NOTE_UPLOAD_JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(jobPersistableBundle)
                .build();

        //schedule job
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        Log.d("JOB SCHEDULING", "Job Scheduled");
        scheduler.schedule(noteUploadJobInfo);
    }
}
