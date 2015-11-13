package br.com.pegasus.hojenaoapp.persistence.contracts;

import android.provider.BaseColumns;

/**
 * Created by emival on 6/5/15.
 */
public class StateContract {

    public StateContract(){}

    /* Inner class that defines the table contents */
    public static abstract class StateEntry implements BaseColumns {
        public static final String TABLE_NAME = "state";
        public static final String COLUMN_NAME_DESCRIPTION = "state_description";
    }
}
