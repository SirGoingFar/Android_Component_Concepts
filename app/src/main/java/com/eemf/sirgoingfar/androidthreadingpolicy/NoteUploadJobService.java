package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFormatException;
import android.util.Log;

import java.io.FileNotFoundException;

public class NoteUploadJobService extends JobService {

    public static final String EXTRA_NOTE_URI = "com.eemf.sirgoingfar.androidthreadingpolicy.NOTE_URI";
    private NoteUpload mNoteUpload;

    public NoteUploadJobService(){}
    /**
     * NOTE: Add this to Manifest File
     * <p>
     * <service
     * android:name=".NoteUploadJobService"
     * android:exported="false"
     * android:permission="android.permission.BIND_JOB_SERVICE"/>
     * <p>
     * - without the permission attribute, the Job Scheduler will not run the job, even if scheduled
     */
    @Override
    public boolean onStartJob(JobParameters params) {

        @SuppressLint("StaticFieldLeak")
        AsyncTask<JobParameters, Void, JobParameters> mTask =
                new AsyncTask<JobParameters, Void, JobParameters>() {
                    @Override
                    protected JobParameters doInBackground(JobParameters... jobParameters) {

                        JobParameters jobParams = jobParameters[0];

                        //get the data URI from the Job Extra
                        Uri dataUri = Uri.parse(jobParams.getExtras().getString(EXTRA_NOTE_URI));

                        //start upload task
                        Log.d("JOB SCHEDULING", "Upload started");
                        mNoteUpload.uploadNote(dataUri);

                        return jobParams;
                    }

                    @Override
                    protected void onPostExecute(JobParameters jobParams) {

                        if (!mNoteUpload.isJobCancel()) {
                            jobFinished(jobParams, false); // does job needs to be re-scheduled?
                            Log.d("JOB SCHEDULING", "Job ended");
                        }
                    }
                };

        mNoteUpload = new NoteUpload(this);
        Log.d("JOB SCHEDULING", "Job started");
        mTask.execute(params);

        return true; //Is a background job running?
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        //cancelJob current job
        Log.d("JOB SCHEDULING", "Job canceled");
        mNoteUpload.cancelJob();

        /*if(mNoteUpload.isJobCancel())
            throw new JobCancelledException();*/

        return true; //restart job later?
    }

    class JobCancelledException extends RuntimeException{

        private static final String GENERAL_ERROR_MSG = "Job cancelled";

        public JobCancelledException(){
            this(GENERAL_ERROR_MSG, new Throwable(GENERAL_ERROR_MSG));
        }

        public JobCancelledException(String message){
            this(message, new Throwable(message));
        }

        public JobCancelledException(Throwable cause){
            this(GENERAL_ERROR_MSG, cause);
        }

        public JobCancelledException(String message, Throwable cause){
            super(message, cause);
        }

    }
}
