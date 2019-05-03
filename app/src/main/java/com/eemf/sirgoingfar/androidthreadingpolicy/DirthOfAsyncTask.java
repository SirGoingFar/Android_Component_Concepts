package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class DirthOfAsyncTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dirth_of_async_task);
        //.
        //.
        //.
        //.
        //assuming the next action is to save to database
        saveDataToDatabase();
    }

    @SuppressLint("StaticFieldLeak")
    private void saveDataToDatabase() {

       AsyncTask<ContentValues, Integer, Uri> saveDataSyncTask = new AsyncTask<ContentValues, Integer, Uri>() {

           private ProgressBar mProgressBar;

           @Override
           protected void onPreExecute() {
               mProgressBar = findViewById(R.id.pb_loader);
               mProgressBar.setVisibility(View.VISIBLE);
           }

           @Override
            protected Uri doInBackground(ContentValues... contentValues) {

               if(contentValues == null || contentValues[0] == null)
                   return null;

               publishProgress(1);

               simulateLongRunningTask();

               publishProgress(2);

               simulateLongRunningTask();

               publishProgress(3);

               simulateLongRunningTask();

//                return getContentResolver().insert(Uri.parse("The Uri of table"), contentValues[0]);
               return null;
            }

           @Override
           protected void onProgressUpdate(Integer... values) {

               if(mProgressBar != null && values != null)
                   mProgressBar.setProgress(values[0]);
           }

           @Override
           protected void onPostExecute(Uri uri) {

               if(uri != null)
                   Log.d(getClass().getSimpleName(), uri.getEncodedPath());

               mProgressBar.setVisibility(View.GONE);
           }

           private void simulateLongRunningTask() {

               try {
                   Thread.sleep(3000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }

       };

       saveDataSyncTask.execute(new ContentValues());
    }
}
