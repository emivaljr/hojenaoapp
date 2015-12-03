package br.com.pegasus.hojenaoapp.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by emival on 8/29/15.
 */
public abstract class AbstractListAsyncTask<T> extends AsyncTask<String, Void, T> {

    protected Context context;

    protected LocalBroadcastManager mLocalBroadcastManager;

    protected String callBack;

    public AbstractListAsyncTask(Context context,String callback){
        this.context = context;
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.callBack = callback;
    }

    @Override
    protected T doInBackground(String... params) {
        return doAsyncTask(params);
    }

    protected abstract T doAsyncTask(String... params);

    @Override
    protected void onPostExecute(T result) {
        boolean sucess = false;
        if(result != null) {
            doResultUpdate(result);
            sucess = true;
        }
        Intent intent = new Intent(callBack);
        intent.putExtra("sucess", sucess);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    protected abstract void doResultUpdate(T result);
}
