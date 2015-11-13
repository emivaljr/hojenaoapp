package br.com.pegasus.hojenaoapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.contracts.AlarmClockContract;

/**
 * Created by emival on 5/28/15.
 */
public class ClockDatabaseHelper extends SQLiteOpenHelper{
    public static final String DB_NAME = "clockDb";
    public static final int DB_VERSION = 2;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AlarmClockContract.AlarmClockEntry.TABLE_NAME + " (" +
                    AlarmClockContract.AlarmClockEntry._ID + " INTEGER PRIMARY KEY," +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR + INTEGER_TYPE + COMMA_SEP +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_MINUTE + INTEGER_TYPE + COMMA_SEP +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_ACTIVE + BOOLEAN_TYPE + COMMA_SEP +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_DAYS_OF_WEEK + TEXT_TYPE + COMMA_SEP +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_SNOOZE + INTEGER_TYPE + COMMA_SEP +
                    AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOLIDAY +BOOLEAN_TYPE+
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AlarmClockContract.AlarmClockEntry.TABLE_NAME;



    public ClockDatabaseHelper(Context ctx) {
        super(ctx,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void saveAlarmClock(AlarmClock alarmClock) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR, alarmClock.getHour());
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_MINUTE, alarmClock.getMinute());
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_NAME, alarmClock.getName());
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_ACTIVE, alarmClock.getActive() ? 1 : 0);
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_DAYS_OF_WEEK, alarmClock.getDaysofweek());
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_SNOOZE, alarmClock.getSnooze());
        values.put(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOLIDAY, alarmClock.getHoliday() ? 1 : 0);

// Insert the new row, returning the primary key value of the new row
        if(alarmClock.getId() == null) {
            Long newRowId;
            newRowId = db.insert(
                    AlarmClockContract.AlarmClockEntry.TABLE_NAME,
                    "null",
                    values);
            alarmClock.setId(newRowId.intValue());
        }else{
            String selection = AlarmClockContract.AlarmClockEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(alarmClock.getId()) };
            int count = db.update(
                    AlarmClockContract.AlarmClockEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }
    public void deleteAlarmClock(Long id){
        SQLiteDatabase db = getWritableDatabase();
        String selection = AlarmClockContract.AlarmClockEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        int count = db.delete(
                AlarmClockContract.AlarmClockEntry.TABLE_NAME,
                selection,
                selectionArgs);
    }

    public AlarmClock recuperarAlarmePorId(Long id) {
        SQLiteDatabase db = getReadableDatabase();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                AlarmClockContract.AlarmClockEntry._ID,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_MINUTE,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_NAME,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_ACTIVE,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_DAYS_OF_WEEK,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_SNOOZE,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOLIDAY
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR + " DESC";

        Cursor cur = db.query(
                AlarmClockContract.AlarmClockEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                "_id = ?",                                // The columns for the WHERE clause
                new String[]{String.valueOf(id)},       // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        AlarmClock alarmClock = null;
        cur.moveToFirst();
        if(cur.isAfterLast() == false) {
            int i = 0;
            alarmClock = new AlarmClock();
            alarmClock.setId(cur.getInt(i++));
            alarmClock.setHour(cur.getInt(i++));
            alarmClock.setMinute(cur.getInt(i++));
            alarmClock.setName(cur.getString(i++));
            alarmClock.setActive(cur.getInt(i++) != 0);
            alarmClock.setDaysofweek(cur.getString(i++));
            alarmClock.setSnooze(cur.getInt(i++));
            alarmClock.setHoliday(cur.getInt(i++) != 0);
        }
        cur.close();



        return alarmClock;

    }

    public Cursor recuperarListaAlarmes() {
        SQLiteDatabase db = getReadableDatabase();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                AlarmClockContract.AlarmClockEntry._ID,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_MINUTE,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_NAME,
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_ACTIVE
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR + " ASC";

        Cursor cursor = db.query(
                AlarmClockContract.AlarmClockEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );






        return cursor;
    }
}
