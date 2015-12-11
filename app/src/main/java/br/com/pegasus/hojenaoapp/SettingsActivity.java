package br.com.pegasus.hojenaoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import br.com.pegasus.hojenaoapp.entity.Holiday;
import br.com.pegasus.hojenaoapp.persistence.CityDatabaseHelper;
import br.com.pegasus.hojenaoapp.persistence.HolidayDatabaseHelper;
import br.com.pegasus.hojenaoapp.persistence.StateDatabaseHelper;
import br.com.pegasus.hojenaoapp.task.CityAsyncTask;
import br.com.pegasus.hojenaoapp.task.HolidayAsyncTask;
import br.com.pegasus.hojenaoapp.task.StateAsyncTask;
import br.com.pegasus.hojenaoapp.util.Constants;
import br.com.pegasus.hojenaoapp.util.GPSUtil;


public class SettingsActivity extends AppCompatActivity implements Serializable{

    private CityDatabaseHelper mDbHelperCity;
    private StateDatabaseHelper mDbHelper;
    private Spinner spinner;
    private Spinner spinnerCity;
    private  SharedPreferences settings;
    private SwitchCompat mSwitch;
    private ProgressDialog Dialog;
    private AlertDialog AlertDialog;
    private HolidayDatabaseHelper holidayDatabaseHelper;
    private TextView valueState;
    private TextView valueCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        holidayDatabaseHelper = new HolidayDatabaseHelper(SettingsActivity.this);
        mDbHelperCity = new CityDatabaseHelper(SettingsActivity.this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Dialog = new ProgressDialog(this);
        AlertDialog = new AlertDialog.Builder(this)
                .setMessage("Ocorreu um erro ao recuperar os dados.")
                .setNeutralButton("Ok",null)
                .create();
        mSwitch = (SwitchCompat) findViewById(R.id.checkBox);
        mSwitch.setChecked(settings.getBoolean("detect_city", true));
        spinner = (Spinner) findViewById(R.id.spinnerState);
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        valueState =  (TextView)findViewById(R.id.valueState);
        valueCity =  (TextView)findViewById(R.id.valueCity);
        if(!settings.contains(Constants.PREFS_DETECT_CITY)){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.PREFS_DETECT_CITY, true);
            editor.commit();
        }
        mSwitch.setOnCheckedChangeListener(updateAutomaticallyListener);
        atualizaDetectarAutomaticamente();

    }
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(returnAsyncCall);
    }
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(returnAsyncCall);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter= new IntentFilter();
        filter.addAction(Constants.CALLBACK_CITY);
        filter.addAction(Constants.CALLBACK_HOLIDAY);
        filter.addAction(Constants.CALLBACK_STATE);
        filter.addAction(Constants.CALLBACK_LOCALIZATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(returnAsyncCall, filter);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GPSUtil.ENABLING_GPS){
            new GPSUtil().detectaAutomaticamente(this,Dialog);
        }
    }

     private CompoundButton.OnCheckedChangeListener updateAutomaticallyListener =  new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            boolean checked = settings.getBoolean(Constants.PREFS_DETECT_CITY, false);
            if(checked != isChecked) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.PREFS_DETECT_CITY, isChecked);
                editor.commit();
                if(isChecked) {
                   new GPSUtil().detectaAutomaticamente(SettingsActivity.this,false);
                }
                settings.edit().remove(Constants.PREFS_STATE).commit();
                settings.edit().remove(Constants.PREFS_CITY).commit();
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layoutFeriados);
                linearLayout.setVisibility(View.GONE);
                atualizaDetectarAutomaticamente();
            }
        }
    };
    private void atualizaDetectarAutomaticamente() {
        if(mSwitch.isChecked()){
            detectarAutomaticamenteSelecionado();
        }else {
            naoDetectarAutomaticamenteSelecionado();
        }
    }
    private void naoDetectarAutomaticamenteSelecionado() {
        check = 0;
        valueState.setVisibility(View.GONE);
        valueCity.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
        if(!settings.contains(Constants.PREFS_CITY)) {
            spinnerCity.setVisibility(View.GONE);
            findViewById(R.id.labelCity).setVisibility(View.GONE);
        }
        mDbHelper = new StateDatabaseHelper(getApplicationContext());
        List<String> lista = mDbHelper.recuperarListaEstado();
        if (lista.isEmpty()) {
            Dialog.setMessage("Recuperando Estados...");
            Dialog.show();
            new StateAsyncTask(getApplicationContext()).execute((String) null);
        } else {
            carregaDadosSpinnerEstado(lista);
            if(settings.contains(Constants.PREFS_STATE)) {
                carregarCidades(settings.getString(Constants.PREFS_STATE,""));
            }
            if(settings.contains(Constants.PREFS_CITY)) {
                carregarFeriados(settings.getString(Constants.PREFS_CITY,""));
            }
        }

    }

    private void detectarAutomaticamenteSelecionado() {
        valueState.setText(settings.getString("state", ""));
        valueState.setVisibility(View.VISIBLE);

        valueCity.setText(settings.getString("city", ""));
        valueCity.setVisibility(View.VISIBLE);

        spinner.setVisibility(View.GONE);
        findViewById(R.id.labelCity).setVisibility(View.VISIBLE);
        spinnerCity.setVisibility(View.GONE);
        if(settings.contains(Constants.PREFS_CITY)&&settings.contains(Constants.PREFS_STATE)) {
            carregarFeriados(settings.getString(Constants.PREFS_CITY, ""));
        }
    }

    int check=0;
    private AdapterView.OnItemSelectedListener onSelectSpinnerState = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            check++;
            if(check>1) {
                CharSequence selectedState = ((TextView) view).getText();
                if(!selectedState.equals("Selecione")) {
                    String state = settings.getString(Constants.PREFS_STATE, "");
                    if (state == null || !state.equals(selectedState)) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.PREFS_STATE, (String) ((TextView) view).getText());
                        editor.commit();
                        String newState = (String) ((TextView) view).getText();
                        settings.edit().remove(Constants.PREFS_CITY);
                        carregarCidades(newState);
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutFeriados);
                        linearLayout.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    private void carregarCidades(String newState) {
        List<String> listaCidade = mDbHelperCity.recuperarListaCidade(newState);
        checkCity=0;
        if (listaCidade.isEmpty()) {
            chamaAssicronamenteCargaCidade(newState);
        } else {
            carregaListaCidade(listaCidade);
        }
    }

    int checkCity=0;
    private AdapterView.OnItemSelectedListener onSelectSpinnerCity = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            checkCity=checkCity+1;
            if(checkCity>1) {
                CharSequence selectedCity = ((TextView) view).getText();
                if(!selectedCity.equals("Selecione")) {
                    String city = settings.getString(Constants.PREFS_CITY, "");
                    if (city == null || !city.equals(selectedCity)) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("city", (String) ((TextView) view).getText());
                        editor.commit();
                        carregarFeriados(settings.getString(Constants.PREFS_CITY, ""));
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void carregarFeriados(String city) {
        List<Holiday> listaFeriados = holidayDatabaseHelper.recuperarFeriados(city);
        if (listaFeriados.isEmpty()) {
            Dialog.setMessage("Recuperando Feriados...");
            Dialog.show();
            new HolidayAsyncTask(SettingsActivity.this).execute(settings.getString(Constants.PREFS_STATE, ""),
                    settings.getString(Constants.PREFS_CITY, ""));
        }else{
            loadHolidays(listaFeriados);
        }
    }

    private void carregaDadosSpinnerEstado(List<String> lista) {
        lista.add(0,"Selecione");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, lista);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if(settings.contains(Constants.PREFS_STATE)) {
            spinner.setSelection(lista.indexOf(settings.getString(Constants.PREFS_STATE, "")));
        }
        spinner.setOnItemSelectedListener(onSelectSpinnerState);


    }
    private void chamaAssicronamenteCargaCidade(String state) {
        spinnerCity.setEnabled(false);
        Dialog.setMessage("Recuperando Cidades...");
        Dialog.show();
        new CityAsyncTask(getApplicationContext()).execute(state);
    }

    private void carregaListaCidade(List<String> listaCidade) {
        listaCidade.add(0,"Selecione");
        ArrayAdapter<String> dataAdapterCity = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, listaCidade);

        dataAdapterCity.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(dataAdapterCity);
        spinnerCity.setEnabled(true);
        spinnerCity.setOnItemSelectedListener(onSelectSpinnerCity);
        spinnerCity.setVisibility(View.VISIBLE);
        findViewById(R.id.labelCity).setVisibility(View.VISIBLE);
        if(settings.contains(Constants.PREFS_CITY)) {
            spinnerCity.setSelection(listaCidade.indexOf(settings.getString(Constants.PREFS_CITY, "")));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private BroadcastReceiver returnAsyncCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean  sucess = intent.getBooleanExtra("sucess",false);
            switch (intent.getAction()){
                case Constants.CALLBACK_HOLIDAY:{onFinishUpdateHoliday(sucess);break;}
                case Constants.CALLBACK_CITY:{onFinishUpdateCity(sucess);break;}
                case Constants.CALLBACK_STATE:{onFinishUpdateState(sucess);break;}
                case Constants.CALLBACK_LOCALIZATION:{onFinishUpdateLocalization(sucess);break;}
            }
            Dialog.dismiss();
        }
    };
    protected void onFinishUpdateState(boolean sucess){
        if(sucess){
            List<String> lista = mDbHelper.recuperarListaEstado();
            carregaDadosSpinnerEstado(lista);
        }
    }
    protected void onFinishUpdateLocalization(boolean sucess){
        if(sucess){
            detectarAutomaticamenteSelecionado();
        }
    }

    protected void onFinishUpdateCity(boolean sucess){
        if(sucess){
            spinnerCity.setEnabled(true);
            List<String> listaCidade =  mDbHelperCity.recuperarListaCidade(settings.getString(Constants.PREFS_STATE, ""));
            carregaListaCidade(listaCidade);
        }
    }

    protected void onFinishUpdateHoliday(boolean sucess) {
        if(sucess){
            List<Holiday> listaFeriados =  holidayDatabaseHelper.recuperarFeriados(settings.getString(Constants.PREFS_CITY, ""));
            loadHolidays(listaFeriados);
        }else{
            AlertDialog.show();
        }
    }

    private void loadHolidays(List<Holiday> listaFeriados) {
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, listaFeriados);
        ListView listview = (ListView)findViewById(R.id.listaFeriados);
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layoutFeriados);
        linearLayout.setVisibility(View.VISIBLE);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
