package br.com.pegasus.hojenaoapp.task;

import android.content.Context;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.pegasus.hojenaoapp.entity.Feriado;
import br.com.pegasus.hojenaoapp.entity.Holiday;
import br.com.pegasus.hojenaoapp.persistence.HolidayDatabaseHelper;
import br.com.pegasus.hojenaoapp.util.Constants;

/**
 * Created by emival on 6/7/15.
 */
public class HolidayAsyncTask extends AbstractListAsyncTask<List<Feriado>> {
    private String city;
    private String state;

    public HolidayAsyncTask(Context context){
        super(context, Constants.CALLBACK_HOLIDAY);
    }

    @Override
    protected List<Feriado> doAsyncTask(String... params) {
        state = params[0];
        city = params[1];
        try {
            List<Feriado> list = new ArrayList<>();
            if(state !=null && !state.equals("")&& city!=null && !city.equals("")) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("State");
                query.whereEqualTo("name", state);
                ParseObject parseObject = query.getFirst();
                ParseQuery<ParseObject> queryCity = ParseQuery.getQuery("City");
                queryCity.whereEqualTo("id_state", parseObject.getInt("id_state"));
                queryCity.whereContains("name_city", city);
                ParseObject parseCity = queryCity.getFirst();
                ParseQuery<ParseObject> queryHoliday = ParseQuery.getQuery("Holiday");
                queryHoliday.whereEqualTo("id_state", parseObject.getInt("id_state"));
                queryHoliday.whereEqualTo("id_city", parseCity.getInt("id_city"));

                List<ParseObject> parseObjects = queryHoliday.find();
                ParseQuery<ParseObject> queryStateHoliday = ParseQuery.getQuery("StateHoliday");
                queryStateHoliday.whereEqualTo("id_state", parseObject.getInt("id_state"));
                parseObjects.addAll(queryStateHoliday.find());
                for (ParseObject p : parseObjects) {
                    Feriado feriado = new Feriado();
                    feriado.setData(p.getString("date"));
                    feriado.setNome(p.getString("name"));
                    list.add(feriado);
                }
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    protected void doResultUpdate(List<Feriado> result) {
        try {
            HolidayDatabaseHelper helper = new HolidayDatabaseHelper(context);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Feriado feriado : result) {
                Holiday holiday = new Holiday();
                holiday.setDate(simpleDateFormat.parse(feriado.getData()));
                holiday.setDescription(feriado.getNome());
                holiday.setLocation(city);
                helper.saveHoliday(holiday);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
