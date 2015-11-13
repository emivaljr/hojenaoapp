package br.com.pegasus.hojenaoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.ClockDatabaseHelper;
import br.com.pegasus.hojenaoapp.persistence.contracts.AlarmClockContract;
import br.com.pegasus.hojenaoapp.util.AlarmUtil;


public class EditClockActivity extends AppCompatActivity {

    private AlarmClock alarmClock;
    private ClockDatabaseHelper mDbHelper;
    private static int SAVE_ALARM_REQUEST = 10;
    private Integer[] checkboxes = new Integer[]{R.id.checkBoxDomingo,
            R.id.checkBoxSegunda,
            R.id.checkBoxTerca,
            R.id.checkBoxQuarta,
            R.id.checkBoxQuinta,
            R.id.checkBoxSexta,
            R.id.checkBoxSabado};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
         mDbHelper = new ClockDatabaseHelper(getApplicationContext());
        setContentView(R.layout.activity_edit_clock);
        Switch switcher =  (Switch)findViewById(R.id.inputSnooze);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.layoutSoneca).setVisibility(isChecked?View.VISIBLE:View.GONE);
            }
        });
        Long idAlarmClock = getIntent().getLongExtra("alarmClock",0L);
        TimePicker timePicker =  (TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        if(idAlarmClock > 0L){
            alarmClock = mDbHelper.recuperarAlarmePorId(idAlarmClock);
            timePicker.setCurrentHour(alarmClock.getHour());
            timePicker.setCurrentMinute(alarmClock.getMinute());
            TextView name =  (TextView)findViewById(R.id.inputName);
            name.setText(alarmClock.getName());
            List<String> lista =  Arrays.asList(alarmClock.getDaysofweek().split("#"));
            int i = 0;
            for (Integer id:checkboxes){
                ((CheckBox)findViewById(id)).setChecked(lista.contains(String.valueOf((Integer)i)));
                i++;
            }
            int snooze = alarmClock.getSnooze();
            if(snooze>0) {
                ((Switch) findViewById(R.id.inputSnooze)).setChecked(true);
                ((TextView) findViewById(R.id.inputSnoozeTime)).setText(String.valueOf(snooze));
            }
            ((Switch) findViewById(R.id.inputFeriado)).setChecked(alarmClock.getHoliday());
        }else{
            alarmClock = new AlarmClock();
            alarmClock.setActive(true);
            for (Integer id:checkboxes){
                ((CheckBox)findViewById(id)).setChecked(true);
            }
            ((Switch)findViewById(R.id.inputSnooze)).setChecked(true);
            ((TextView)findViewById(R.id.inputSnoozeTime)).setText("10");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_clock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            TextView name =  (TextView)findViewById(R.id.inputName);
            alarmClock.setName(name.getText().toString());
            TimePicker timePicker =  (TimePicker)findViewById(R.id.timePicker);
            alarmClock.setHour(timePicker.getCurrentHour());
            alarmClock.setMinute(timePicker.getCurrentMinute());
            alarmClock.setActive(true);
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Integer idCheck:checkboxes){
                if(((CheckBox)findViewById(idCheck)).isChecked()){
                    sb.append(i+"#");
                }
                i++;
            }
            if(sb.length()>0){
                sb.deleteCharAt(sb.lastIndexOf("#"));
            }
            alarmClock.setDaysofweek(sb.toString());
            if(((Switch)findViewById(R.id.inputSnooze)).isChecked()){
                alarmClock.setSnooze(Integer.parseInt(((TextView) findViewById(R.id.inputSnoozeTime)).getText().toString()));
            }else{
                alarmClock.setSnooze(0);
            }
            alarmClock.setHoliday(((Switch) findViewById(R.id.inputFeriado)).isChecked());
            mDbHelper.saveAlarmClock(alarmClock);
            Context context = getApplicationContext();
            AlarmUtil.configureAlarm(alarmClock, context, true);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
