package br.com.pegasus.hojenaoapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.pegasus.hojenaoapp.entity.City;
import br.com.pegasus.hojenaoapp.entity.State;
import br.com.pegasus.hojenaoapp.persistence.contracts.AlarmClockContract;
import br.com.pegasus.hojenaoapp.persistence.contracts.CityContract;
import br.com.pegasus.hojenaoapp.persistence.contracts.StateContract;

/**
 * Created by emival on 5/28/15.
 */
public class CityDatabaseHelper extends SQLiteOpenHelper{
    public static final String DB_NAME = "cityDb";

    public static final int DB_VERSION = 2;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CityContract.CityEntry.TABLE_NAME + " (" +
                    CityContract.CityEntry._ID + INTEGER_TYPE+" PRIMARY KEY" + COMMA_SEP+
                    CityContract.CityEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP+
                    CityContract.CityEntry.COLUMN_NAME_STATE + TEXT_TYPE +
            " )";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CityContract.CityEntry.TABLE_NAME;



    public CityDatabaseHelper(Context ctx) {
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


    public void saveCity(City city) {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CityContract.CityEntry.COLUMN_NAME_DESCRIPTION, city.getName());
        values.put(CityContract.CityEntry.COLUMN_NAME_STATE, city.getState());

// Insert the new row, returning the primary key value of the new row

            Long newRowId;
            newRowId = db.insert(
                    CityContract.CityEntry.TABLE_NAME,
                    "null",
                    values);

    }

    public List<String> recuperarListaCidade(String state) {
        SQLiteDatabase db = getReadableDatabase();
        // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                CityContract.CityEntry.COLUMN_NAME_DESCRIPTION,
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                CityContract.CityEntry.COLUMN_NAME_DESCRIPTION + " ASC";
        String selection = CityContract.CityEntry.COLUMN_NAME_STATE + " = ?";
        String[] selectionArgs = { state };

        Cursor cursor = db.query(
                CityContract.CityEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        List<String> lista = new ArrayList<String>();
        cursor.moveToFirst();
        if(cursor.isAfterLast() == false) {
            int i = 0;
            do {
                lista.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


}
