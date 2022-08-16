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
import org.woheller69.lavatories.api.IDataExtractor;
import org.woheller69.lavatories.api.IProcessHttpRequest;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.database.Station;
import org.woheller69.lavatories.ui.updater.ViewUpdater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class processes the HTTP requests that are made to the Tankerkönig API requesting the
 * current prices for all stored cities.
 */
public class OSMProcessHttpRequestAddress implements IProcessHttpRequest {

    /**
     * Member variables
     */
    private Context context;
    private SQLiteHelper dbHelper;
    private List<Station> stations;

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     * @param stations
     */
    public OSMProcessHttpRequestAddress(Context context, List<Station> stations) {
        this.context = context;
        this.dbHelper = SQLiteHelper.getInstance(context);
        this.stations = stations;

    }

    /**
     * Converts the response to JSON and updates the database. Note that for this method no
     * UI-related operations are performed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response, int cityId) {
        Log.d("Request",response);
        this.dbHelper = SQLiteHelper.getInstance(context);
            try {
                JSONArray list = new JSONArray(response);
                for (int i = 0; i < list.length(); i++) {
                    String address1 = "";
                    String address2 = "";
                    String currentItem = list.get(i).toString();
                    Log.d("ExtractAddress", currentItem);
                    JSONObject json = new JSONObject(currentItem);
                    String uuid = json.getString("osm_id");
                    JSONObject address = json.getJSONObject("address");
                    if (address.has("road")) address1 = address.getString("road");
                    if (address.has("house_number")) address1 = address1 + " "+ address.getString("house_number");
                    if (address.has("postcode")) address2 = address.getString("postcode");
                    if (address.has("village")) address2 = address2 + " " + address.getString("village");

                    for (Station station:stations){
                        if (station.getUuid().equals(uuid)){
                            Log.d("Extract",uuid+" "+address1+" "+address2);
                            station.setAddress1(address1);
                            station.setAddress2(address2);
                            dbHelper.addStation(station);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        Collections.sort(stations,(o1,o2) -> (int) (o1.getDistance()*1000 - o2.getDistance()*1000));
        ViewUpdater.updateStations(stations,cityId);
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
