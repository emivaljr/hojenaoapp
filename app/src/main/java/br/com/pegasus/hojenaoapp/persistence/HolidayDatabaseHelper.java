package br.com.pegasus.hojenaoapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.entity.Holiday;
import br.com.pegasus.hojenaoapp.persistence.contracts.AlarmClockContract;
import br.com.pegasus.hojenaoapp.persistence.contracts.HolidayContract;

/**
 * Created by emival on 5/28/15.
 */
public class HolidayDatabaseHelper extends SQLiteOpenHelper{
    public static final String DB_NAME = "holidayDb";

    public static final int DB_VERSION = 2;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HolidayContract.HolidayEntry.TABLE_NAME + " (" +
                    HolidayContract.HolidayEntry._ID + INTEGER_TYPE+" PRIMARY KEY" + COMMA_SEP+
                    HolidayContract.HolidayEntry.COLUMN_NAME_DATE + DATE_TYPE + COMMA_SEP +
                    HolidayContract.HolidayEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION_TYPE + TEXT_TYPE +
            " )";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HolidayContract.HolidayEntry.TABLE_NAME;



    public HolidayDatabaseHelper(Context ctx) {
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


    public void saveHoliday(Holiday holiday) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HolidayContract.HolidayEntry.COLUMN_NAME_DATE, dateFormat.format(holiday.getDate()));
        values.put(HolidayContract.HolidayEntry.COLUMN_NAME_DESCRIPTION, holiday.getDescription());
        values.put(HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION, holiday.getLocation());
        values.put(HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION_TYPE, holiday.getLocationType());

// Insert the new row, returning the primary key value of the new row

            Long newRowId;
            newRowId = db.insert(
                    HolidayContract.HolidayEntry.TABLE_NAME,
                    "null",
                    values);

    }
    public boolean isFeriado(Date data,String city) {
        SQLiteDatabase db = getReadableDatabase();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                HolidayContract.HolidayEntry._ID,
                HolidayContract.HolidayEntry.COLUMN_NAME_DATE,
                HolidayContract.HolidayEntry.COLUMN_NAME_DESCRIPTION,
                HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION
        };

// How you want the results sorted in the resulting Cursor
        String selection = HolidayContract.HolidayEntry.COLUMN_NAME_DATE + " = ? "+
                " and "+ HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION+ " = ?";
        String[] selectionArgs = { dateFormat.format(data),city };

        Cursor cursor = db.query(
                HolidayContract.HolidayEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        return cursor.moveToFirst();
    }

    public List<Holiday> recuperarFeriados(String city) {
        SQLiteDatabase db = getReadableDatabase();
        List<Holiday> lista = new ArrayList<Holiday>();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                HolidayContract.HolidayEntry._ID,
                HolidayContract.HolidayEntry.COLUMN_NAME_DATE,
                HolidayContract.HolidayEntry.COLUMN_NAME_DESCRIPTION,
                HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION
        };

// How you want the results sorted in the resulting Cursor
        String selection = HolidayContract.HolidayEntry.COLUMN_NAME_LOCATION+ " = ?";
        String[] selectionArgs = { city };
        String sortOrder =
                HolidayContract.HolidayEntry.COLUMN_NAME_DATE + " ASC";

        Cursor cursor = db.query(
                HolidayContract.HolidayEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        try {
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int i = 0;
            Holiday holiday = new Holiday();
            holiday.setId(cursor.getInt(i++));
            holiday.setDate(dateFormat.parse(cursor.getString(i++)));
            holiday.setDescription(cursor.getString(i++));
            holiday.setLocation(cursor.getString(i++));
            lista.add(holiday);
            cursor.moveToNext();
        }
            cursor.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
