package br.com.pegasus.hojenaoapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.pegasus.hojenaoapp.entity.AlarmClock;
import br.com.pegasus.hojenaoapp.persistence.HolidayDatabaseHelper;
import br.com.pegasus.hojenaoapp.task.HolidayAsyncTask;
import br.com.pegasus.hojenaoapp.util.Constants;

public class ClockService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    public ClockService() {
    }

    private GoogleApiClient mGoogleApiClient;

    private String city;

    private String state;

    private LocalBroadcastManager mLocalBroadcastManager;

    private Bundle bundle;


    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        buildGoogleApiClient();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.CALLBACK_HOLIDAY);
        mLocalBroadcastManager.registerReceiver(returnAsyncCall, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            updateStateAndCity(mLastLocation);
        }else{
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locationRequest,this);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        updateStateAndCity(location);
    }

    private void updateStateAndCity(Location mLastLocation) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    // In this sample, get just a single address.
                    1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getCountryCode().equals("BR")) {
                    city = address.getLocality();
                    state = address.getAdminArea();
                    SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("city", city);
                    editor.putString("state", state);
                    editor.commit();
                    /*if (!settings.contains("city") || !settings.getString("city", "").equals(city)) {
                        new HolidayAsyncTask(this).execute(state, city);
                        return;
                    }*/
                }

            }
            sendSucessBroadcast();
        } catch (IOException ioe) {

        }
        stopSelf();
    }


    private void sendSucessBroadcast() {
        Intent intent = new Intent(Constants.CALLBACK_LOCALIZATION);
        if(bundle!=null) {
            intent.putExtras(bundle);
        }
        intent.putExtra("sucess", true);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    public void onFinishUpdate(boolean sucess, String action) {
        if (sucess) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("city", city);
            editor.putString("state", state);
            editor.commit();
        }
        sendSucessBroadcast();
        stopSelf();
    }

    private BroadcastReceiver returnAsyncCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean sucess = intent.getBooleanExtra("sucess", false);
            onFinishUpdate(sucess, intent.getAction());
        }
    };


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bundle = intent.getExtras();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient.connect();
        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mLocalBroadcastManager.unregisterReceiver(returnAsyncCall);
    }


}
