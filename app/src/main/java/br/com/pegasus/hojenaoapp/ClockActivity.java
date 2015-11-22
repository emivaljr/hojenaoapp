package br.com.pegasus.hojenaoapp;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.util.AlarmUtil;
import br.com.pegasus.hojenaoapp.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ClockActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private Ringtone ringtoneSound;

    private PowerManager.WakeLock wakeLock;

    private AlarmClock alarmClock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        final Button fechar = (Button)findViewById(R.id.close);
        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ringtoneSound.stop();
                wakeLock.release();
            }
        });
        final Button snooze = (Button)findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ringtoneSound.stop();
                wakeLock.release();
                AlarmUtil.configureAlarm(alarmClock, getApplicationContext(), false);

            }
        });
        alarmClock = (AlarmClock)getIntent().getSerializableExtra("alarm_clock");
        final TextView name = (TextView)findViewById(R.id.textView);
        name.setText(alarmClock.getName());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ligarTela();
        tocarSom();
    }

    private void tocarSom() {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if(volume==0)
            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtoneSound = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);

        if (ringtoneSound != null) {
            ringtoneSound.setStreamType(AudioManager.STREAM_ALARM);
            ringtoneSound.play();
        }
    }


    private void ligarTela() {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
    }

}
