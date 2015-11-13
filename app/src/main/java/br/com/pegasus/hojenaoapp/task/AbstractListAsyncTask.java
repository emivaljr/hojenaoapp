package br.com.pegasus.hojenaoapp.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import br.com.pegasus.hojenaoapp.backend.myApi.MyApi;
import br.com.pegasus.hojenaoapp.util.Constants;

/**
 * Created by emival on 8/29/15.
 */
public abstract class AbstractListAsyncTask<T> extends AsyncTask<String, Void, T> {

    protected static MyApi myApiService = null;
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
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    // http://hojenaoapp.appspot.com/
                    .setRootUrl(Constants.APP_ENGINE_API)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }
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
