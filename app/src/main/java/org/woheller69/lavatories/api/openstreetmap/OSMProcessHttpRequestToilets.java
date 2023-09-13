package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.os.Handler;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.woheller69.lavatories.R;
import org.woheller69.lavatories.activities.NavigationActivity;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.api.IDataExtractor;
import org.woheller69.lavatories.api.IProcessHttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class processes the HTTP requests that are made to the Tankerkönig API requesting the
 * current prices for all stored cities.
 */
public class OSMProcessHttpRequestToilets implements IProcessHttpRequest {

    /**
     * Member variables
     */
    private Context context;
    private SQLiteHelper dbHelper;

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     */
    public OSMProcessHttpRequestToilets(Context context) {
        this.context = context;
        this.dbHelper = SQLiteHelper.getInstance(context);
    }

    /**
     * Converts the response to JSON and updates the database. Note that for this method no
     * UI-related operations are performed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response, int cityId) {
        //Log.d("Request",response);
        IDataExtractor extractor = new OSMDataExtractor();
        dbHelper.deleteLavatoriesByCityId(cityId); //start with empty list
        List<Lavatory> lavatories = new ArrayList<>();
        if (extractor.wasCityFound(response)) {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray list = json.getJSONArray("elements");
                for (int i = 0; i < list.length(); i++) {
                    String currentItem = list.get(i).toString();
                    Lavatory lavatory = extractor.extractLavatory(currentItem,cityId,context);
                    if (lavatory != null) { // Could retrieve all data, so add it to the list
                        lavatory.setCity_id(cityId);
                        lavatories.add(lavatory);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            final String ERROR_MSG = context.getResources().getString(R.string.error_fetch_lavatories);
            if (NavigationActivity.isVisible)
                Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
        }

        List<Lavatory> lavatoriesBatch = new ArrayList<>();
        for (Lavatory lavatory : lavatories){  //split into batches of 49 lavatories, which is the max for Nominatim API calls
            if (lavatoriesBatch.size()<49){
                lavatoriesBatch.add(lavatory);
            }
            if (lavatoriesBatch.size()==49){
                OSMHttpRequestForAddress addressRequest = new OSMHttpRequestForAddress(context);
                addressRequest.perform(cityId, lavatoriesBatch);
                lavatoriesBatch = new ArrayList<>();
            }
        }
        OSMHttpRequestForAddress addressRequest = new OSMHttpRequestForAddress(context);
        addressRequest.perform(cityId, lavatoriesBatch);
    }

    /**
     * Shows an error that the data could not be retrieved.
     *
     * @param error The error that occurred while executing the HTTP request.
     */
    @Override
    public void processFailScenario(final VolleyError error) {
        Log.d("Error", String.valueOf(error));
        Handler h = new Handler(this.context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (NavigationActivity.isVisible) Toast.makeText(context, context.getResources().getString(R.string.error_fetch_lavatories), Toast.LENGTH_LONG).show();
            }
        });
    }

}
