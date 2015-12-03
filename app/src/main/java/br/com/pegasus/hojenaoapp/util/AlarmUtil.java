package br.com.pegasus.hojenaoapp.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import br.com.pegasus.hojenaoapp.ClockReceiver;
import br.com.pegasus.hojenaoapp.entity.AlarmClock;

/**
 * Created by emival on 6/5/15.
 */
public class AlarmUtil {

    public static void configureAlarm(AlarmClock alarmClock,Context context,boolean cancelLast,boolean snoozeActivated) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ClockReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt("alarm_clock_id", alarmClock.getId());
        bundle.putInt("snooze",snoozeActivated?1:0);
        intent.putExtras(bundle);
        Integer id = alarmClock.getId();
        if(snoozeActivated){
            id *= -1;
        }
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        /*if(cancelLast) {
            alarmMgr.cancel(alarmIntent);
        }*/
        if(alarmClock.getActive()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, alarmClock.getHour());
            if(snoozeActivated){
                calendar.add(Calendar.MINUTE,alarmClock.getSnooze());
            }else {
                calendar.set(Calendar.MINUTE, alarmClock.getMinute());
            }
            calendar.set(Calendar.SECOND, 00);
            if (calendar.getTime().before(new Date())&&!snoozeActivated) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            }
        }
    }
    public static void configureAlarm(AlarmClock alarmClock,Context context,boolean cancelLast) {
        configureAlarm(alarmClock,context,cancelLast,false);
    }
}
