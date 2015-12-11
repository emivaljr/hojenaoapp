package br.com.pegasus.hojenaoapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;

import br.com.pegasus.hojenaoapp.ClockService;
import br.com.pegasus.hojenaoapp.SettingsActivity;

/**
 * Created by emival on 12/6/15.
 */
public class GPSUtil {

    public static int ENABLING_GPS = 20;


    public void detectaAutomaticamente(Activity context,ProgressDialog dialog) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(dialog!=null) {
                dialog.setMessage("Atualizando dados de Estado e cidade...");
                dialog.show();
            }
            Intent i = new Intent(context, ClockService.class);
            context.startService(i);
        }else{
            showGPSDisabledAlertToUser(context);
        }
    }
    public void detectaAutomaticamente(Activity context,boolean wait) {
        detectaAutomaticamente(context,null);
    }

    public void showGPSDisabledAlertToUser(final Activity context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Precisamos do serviço de localização(GPS) para detectar automaticamente a sua cidade.")
                .setCancelable(false)
                .setPositiveButton("Habilitar",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivityForResult(callGPSSettingIntent, ENABLING_GPS);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Configurar manualmente",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(Constants.PREFS_DETECT_CITY, false);
                        editor.commit();
                        context.startActivity(new Intent(context, SettingsActivity.class));
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
