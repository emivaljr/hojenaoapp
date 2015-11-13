package br.com.pegasus.hojenaoapp.task;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.pegasus.hojenaoapp.entity.State;
import br.com.pegasus.hojenaoapp.persistence.StateDatabaseHelper;
import br.com.pegasus.hojenaoapp.util.Constants;

/**
 * Created by emival on 6/7/15.
 */
public class StateAsyncTask extends AbstractListAsyncTask<List<String>> {

    public StateAsyncTask(Context context){
        super(context, Constants.CALLBACK_STATE);

    }

    @Override
    protected List<String> doAsyncTask(String... params) {
        final List<String> lista = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("State");
        try {
            List<ParseObject> parseObjects = query.find();
            for (ParseObject p : parseObjects){
                lista.add(p.getString("name"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    protected void doResultUpdate(List<String> result) {
        StateDatabaseHelper helper = new StateDatabaseHelper(context);
        for (String stateName : result) {
            State state = new State();
            state.setName(stateName);
            helper.saveState(state);
        }
    }

}
