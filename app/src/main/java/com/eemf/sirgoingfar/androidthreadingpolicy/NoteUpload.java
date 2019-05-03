package com.eemf.sirgoingfar.androidthreadingpolicy;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

public class NoteUpload {

    private final Context mContext;
    private boolean mCancel;

    public NoteUpload(@NonNull Context context){
        mContext = context;
    }

    public boolean isJobCancel() {
        return mCancel;
    }

    public void cancelJob() {
        this.mCancel = true;
    }

    public void uploadNote(@NonNull Uri dataUri){

        if(!mCancel) {

            //do the actual job here, e.g. the note upload

        }

    }
}
