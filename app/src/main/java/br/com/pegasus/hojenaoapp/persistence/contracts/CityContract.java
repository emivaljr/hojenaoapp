package br.com.pegasus.hojenaoapp.persistence.contracts;

import android.provider.BaseColumns;

/**
 * Created by emival on 6/5/15.
 */
public class CityContract {

    public CityContract(){}

    /* Inner class that defines the table contents */
    public static abstract class CityEntry implements BaseColumns {
        public static final String TABLE_NAME = "city";
        public static final String COLUMN_NAME_DESCRIPTION = "city_description";
        public static final String COLUMN_NAME_STATE = "city_state";

    }
}
