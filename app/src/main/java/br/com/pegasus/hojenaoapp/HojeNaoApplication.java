package br.com.pegasus.hojenaoapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by emival on 11/4/15.
 */
public class HojeNaoApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "N9AKD8DZNDCbWl400vyBSRgVyIbwFSFtvkf6pMNs", "zlm4CWwK59AVxL2TsL67ytNPIplTXvxUnObEJ84h");

    }
}
