package br.com.pegasus.hojenaoapp.task;

import android.content.Context;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.pegasus.hojenaoapp.entity.City;
import br.com.pegasus.hojenaoapp.persistence.CityDatabaseHelper;
import br.com.pegasus.hojenaoapp.util.Constants;

/**
 * Created by emival on 6/7/15.
 */
public class CityAsyncTask extends AbstractListAsyncTask<List<String>> {

    private String state;

    public CityAsyncTask(Context context){
        super(context, Constants.CALLBACK_CITY);
    }

    @Override
    protected List<String> doAsyncTask(String... params) {
        final List<String> lista = new ArrayList<String>();
        state = params[0];
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("State");
            query.whereEqualTo("name", state);
            ParseObject parseObject = query.getFirst();
            ParseQuery<ParseObject> queryCity = ParseQuery.getQuery("City");
            queryCity.whereEqualTo("id_state", parseObject.getInt("id_state"));
            List<ParseObject> parseObjects = queryCity.find();
            for (ParseObject p : parseObjects){
                lista.add(p.getString("name_city"));
            }
            return lista;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    protected void doResultUpdate(List<String> result) {
        CityDatabaseHelper helper = new CityDatabaseHelper(context);
        for (String stateName : result) {
            City city = new City();
            city.setName(stateName);
            city.setState(state);
            helper.saveCity(city);
        }
    }

}
