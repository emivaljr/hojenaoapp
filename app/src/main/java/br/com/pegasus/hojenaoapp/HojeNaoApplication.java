package br.com.pegasus.hojenaoapp;

import android.app.Application;
import android.database.Cursor;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import java.util.List;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.ClockDatabaseHelper;
import br.com.pegasus.hojenaoapp.util.AlarmUtil;

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

        ClockDatabaseHelper clockDatabaseHelper = new ClockDatabaseHelper(getApplicationContext());
        List<AlarmClock> alarmClocks = clockDatabaseHelper.recuperarListAlarmes();
        for(AlarmClock alarmClock : alarmClocks){
            AlarmUtil.configureAlarm(alarmClock,getApplicationContext(),true);
        }


    }
}
