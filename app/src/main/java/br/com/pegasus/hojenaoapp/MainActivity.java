package br.com.pegasus.hojenaoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.Parse;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.ClockDatabaseHelper;
import br.com.pegasus.hojenaoapp.persistence.contracts.AlarmClockContract;
import br.com.pegasus.hojenaoapp.util.AlarmUtil;
import br.com.pegasus.hojenaoapp.util.Constants;


public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private ClockDatabaseHelper mDbHelper;
    private  AlarmClockCursorAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView listview;
    private TextView textview;

    private static int SAVE_ALARM_REQUEST = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new ClockDatabaseHelper(getApplicationContext());
        checkCity();
        listview = (RecyclerView) findViewById(R.id.list);
        textview = (TextView) findViewById(R.id.list_empty);
        listview.setItemAnimator(new DefaultItemAnimator());
        listview.setHasFixedSize(true);
        Cursor list = mDbHelper.recuperarListaAlarmes();
        adapter = new AlarmClockCursorAdapter(this,list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(mLayoutManager);
        listview.setAdapter(adapter);
        listview.setVisibility(list.getCount() == 0 ? View.GONE : View.VISIBLE);
        textview.setVisibility(list.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void checkCity() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        if(!settings.contains("city")) {
            Intent i = new Intent(this, ClockService.class);
            startService(i);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Cursor list = mDbHelper.recuperarListaAlarmes();
        ((AlarmClockCursorAdapter)listview.getAdapter()).setCursor(list);
        listview.getAdapter().notifyDataSetChanged();
        listview.setVisibility(list.getCount() == 0 ? View.GONE : View.VISIBLE);
        textview.setVisibility(list.getCount() == 0 ? View.VISIBLE : View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            startActivityForResult(new Intent(getApplication(), EditClockActivity.class), SAVE_ALARM_REQUEST);
            return true;
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplication(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AlarmClockCursorAdapter extends RecyclerView.Adapter<AlarmClockCursorAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_clock, viewGroup, false);
            ViewHolder v = new ViewHolder(view);
            return v;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            cursor.moveToPosition(i);
            String hour = cursor.getString(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_HOUR));
            String minute = cursor.getString(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_MINUTE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_NAME));
            viewHolder.tvBody.setText(transformToTwoDigits(hour)+":"+transformToTwoDigits(minute));
            viewHolder.tvPriority.setText(String.valueOf(name));
            Integer ativo = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry.COLUMN_NAME_ACTIVE));
            if(ativo == 0){
                viewHolder.imageButton.setImageResource(R.drawable.ic_alarm_off_black_48dp);
            }else{
                viewHolder.imageButton.setImageResource(R.drawable.ic_alarm_on_black_48dp);
            }
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
            // each data item is just a string in this case
            public TextView tvBody;
            public TextView tvPriority;
            public ImageButton imageButton;
            public ImageButton deleteButton;
            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
                tvBody = (TextView) v.findViewById(R.id.tvBody);
                tvPriority = (TextView) v.findViewById(R.id.tvPriority);
                imageButton = (ImageButton) v.findViewById(R.id.imageButton);
                deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long id = cursor.getLong(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry._ID));
                        AlarmClock alarmClock = new AlarmClock();
                        alarmClock.setId(id.intValue());
                        alarmClock.setActive(false);
                        mDbHelper.deleteAlarmClock(cursor.getLong(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry._ID)));
                        itemView.setBackgroundColor(getResources().getColor(R.color.icons));
                        AlarmUtil.configureAlarm(alarmClock, getApplicationContext(), true);
                        cursor = mDbHelper.recuperarListaAlarmes();
                        listview.setVisibility(cursor.getCount() == 0 ? View.GONE : View.VISIBLE);
                        textview.setVisibility(cursor.getCount() == 0 ? View.VISIBLE : View.GONE);
                        notifyDataSetChanged();
                    }
                });
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cursor.moveToPosition(getPosition());
                        AlarmClock alarmClock = mDbHelper.recuperarAlarmePorId(cursor.getLong(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry._ID)));
                        if(!alarmClock.getActive()){
                            alarmClock.setActive(true);
                            imageButton.setImageResource(R.drawable.ic_alarm_on_black_48dp);
                        }else{
                            alarmClock.setActive(false);
                            imageButton.setImageResource(R.drawable.ic_alarm_off_black_48dp);
                        }
                        mDbHelper.saveAlarmClock(alarmClock);
                        AlarmUtil.configureAlarm(alarmClock, getApplicationContext(), true);
                        cursor = mDbHelper.recuperarListaAlarmes();
                    }

                });
            }

            @Override
            public void onClick(View v) {
                cursor.moveToPosition(getPosition());
                Intent x = new Intent(getApplication(), EditClockActivity.class);
                x.putExtra("alarmClock", cursor.getLong(cursor.getColumnIndexOrThrow(AlarmClockContract.AlarmClockEntry._ID)));
                startActivityForResult(x, SAVE_ALARM_REQUEST);
            }

            @Override
            public boolean onLongClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.primary_light));
                deleteButton.setVisibility(View.VISIBLE);
                return true;
            }
        }

        public Cursor getCursor() {
            return cursor;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        private Cursor cursor;

        public AlarmClockCursorAdapter(Context context, Cursor cursor) {
            this.cursor = cursor;
        }

        private String transformToTwoDigits(String value){
            if(value !=null){
                if(value.length()==1) {
                    return "0" + value;
                }
            }
            return value;
        }
    }

}
