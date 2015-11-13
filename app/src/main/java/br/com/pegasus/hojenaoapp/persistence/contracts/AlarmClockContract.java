package br.com.pegasus.hojenaoapp.persistence.contracts;

import android.provider.BaseColumns;

/**
 * Created by emival on 5/29/15.
 */
public final class AlarmClockContract {
    public AlarmClockContract() {}

    /* Inner class that defines the table contents */
    public static abstract class AlarmClockEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarmclock";
        public static final String COLUMN_NAME_ENTRY_ID = "alarmclockid";
        public static final String COLUMN_NAME_HOUR = "alarmclock_hour";
        public static final String COLUMN_NAME_MINUTE = "alarmclock_minute";
        public static final String COLUMN_NAME_NAME = "alarmclock_name";
        public static final String COLUMN_NAME_ACTIVE = "alarmclock_active";
        public static final String COLUMN_NAME_SNOOZE = "alarmclock_snooze";
        public static final String COLUMN_NAME_DAYS_OF_WEEK = "alarmclock_daysofweek";
        public static final String COLUMN_NAME_HOLIDAY = "alarmclock_holiday";
    }
}
