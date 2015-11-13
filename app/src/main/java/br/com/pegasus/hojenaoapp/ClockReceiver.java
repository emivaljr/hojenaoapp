package br.com.pegasus.hojenaoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.HolidayDatabaseHelper;
import br.com.pegasus.hojenaoapp.util.Constants;

public class ClockReceiver extends BroadcastReceiver {
    public ClockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        if(settings.getBoolean(Constants.PREFS_DETECT_CITY, true)) {
            Intent i = new Intent(context, ClockService.class);
            i.putExtras(intent.getExtras());
            context.startService(i);
        }else{
            callUI(context,intent,settings);
        }
    }
    private void callUI(Context context, Intent intent, SharedPreferences settings) {
        AlarmClock alarmClock = (AlarmClock) intent.getSerializableExtra("alarm_clock");
        HolidayDatabaseHelper holidayDatabaseHelper = new HolidayDatabaseHelper(context);
        if(alarmClock != null && alarmClock.getActive()) {
            Calendar cal = Calendar.getInstance();
            int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
            if (!Arrays.asList(alarmClock.getDaysofweek().split("#")).contains(String.valueOf(day_of_week - 1))) {
                return;
            }
        /*       cal.set(Calendar.DAY_OF_MONTH,1);
        cal.set(Calendar.MONTH,Calendar.JANUARY);*/
            if (alarmClock.getHoliday() ||
                    !holidayDatabaseHelper.isFeriado(new Date(),settings.getString(Constants.PREFS_CITY,""))) {
                executeAction(context,alarmClock);
            }
        }

    }
    public void executeAction(Context context,AlarmClock alarmClock) {
        Intent i = new Intent(context,ClockActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable("alarm_clock", alarmClock);
        i.putExtras(bundle);
        context.startActivity(i);
    }
}