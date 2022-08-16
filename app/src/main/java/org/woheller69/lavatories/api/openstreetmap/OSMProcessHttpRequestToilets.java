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
import org.woheller69.lavatories.database.Station;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.ui.updater.ViewUpdater;
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
        dbHelper.deleteStationsByCityId(cityId); //start with empty stations list
        List<Station> stations = new ArrayList<>();
        if (extractor.wasCityFound(response)) {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray list = json.getJSONArray("elements");
                for (int i = 0; i < list.length(); i++) {
                    String currentItem = list.get(i).toString();
                    Station station = extractor.extractStation(currentItem,cityId,context);
                    if (station != null) { // Could retrieve all data, so add it to the list
                        station.setCity_id(cityId);
                        // add it to the database
                        //dbHelper.addStation(station);
                        stations.add(station);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            final String ERROR_MSG = context.getResources().getString(R.string.error_fetch_stations);
            if (NavigationActivity.isVisible)
                Toast.makeText(context, ERROR_MSG, Toast.LENGTH_LONG).show();
        }
        Collections.sort(stations,(o1,o2) -> (int) (o1.getDistance()*1000 - o2.getDistance()*1000));
        stations=stations.stream().limit(49).collect(Collectors.toList());  //limit to 49 stations. Max for Nominatim API call
        ViewUpdater.updateStations(stations,cityId);
        OSMHttpRequestForAddress addressRequest = new OSMHttpRequestForAddress(context);
        addressRequest.perform(cityId,stations);
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
                if (NavigationActivity.isVisible) Toast.makeText(context, context.getResources().getString(R.string.error_fetch_stations), Toast.LENGTH_LONG).show();
            }
        });
    }

}
