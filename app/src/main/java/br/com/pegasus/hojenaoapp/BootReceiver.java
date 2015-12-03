package br.com.pegasus.hojenaoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.ClockDatabaseHelper;
import br.com.pegasus.hojenaoapp.util.AlarmUtil;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ClockDatabaseHelper clockDatabaseHelper = new ClockDatabaseHelper(context);
        List<AlarmClock> alarmClocks = clockDatabaseHelper.recuperarListAlarmes();
        for(AlarmClock alarmClock : alarmClocks){
            AlarmUtil.configureAlarm(alarmClock, context, true);
        }
    }
}
