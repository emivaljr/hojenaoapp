package br.com.pegasus.hojenaoapp.persistence.contracts;

import android.provider.BaseColumns;

/**
 * Created by emival on 6/5/15.
 */
public class HolidayContract {

    public HolidayContract(){}

    /* Inner class that defines the table contents */
    public static abstract class HolidayEntry implements BaseColumns {
        public static final String TABLE_NAME = "holiday";
        public static final String COLUMN_NAME_DATE = "holiday_date";
        public static final String COLUMN_NAME_DESCRIPTION = "holiday_description";
        public static final String COLUMN_NAME_LOCATION = "holiday_location";
        public static final String COLUMN_NAME_LOCATION_TYPE = "holiday_location_type";
    }
}
