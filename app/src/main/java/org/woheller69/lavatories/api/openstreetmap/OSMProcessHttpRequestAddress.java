package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.woheller69.AndroidAddressFormatter.AndroidAddressFormatter;
import org.woheller69.lavatories.R;
import org.woheller69.lavatories.activities.NavigationActivity;

import org.woheller69.lavatories.api.IProcessHttpRequest;
import org.woheller69.lavatories.database.CityToWatch;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.ui.Help.StringFormatUtils;
import org.woheller69.lavatories.ui.updater.ViewUpdater;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private List<Lavatory> lavatories;

    /**
     * Constructor.
     *
     * @param context The context of the HTTP request.
     * @param lavatories
     */
    public OSMProcessHttpRequestAddress(Context context, List<Lavatory> lavatories) {
        this.context = context;
        this.dbHelper = SQLiteHelper.getInstance(context);
        this.lavatories = lavatories;

    }

    /**
     * Converts the response to JSON and updates the database. Note that for this method no
     * UI-related operations are performed.
     *
     * @param response The response of the HTTP request.
     */
    @Override
    public void processSuccessScenario(String response, int cityId) {
        this.dbHelper = SQLiteHelper.getInstance(context);
        SQLiteHelper db = SQLiteHelper.getInstance(context);
        CityToWatch city = db.getCityToWatch(cityId);
        Location cityLocation = new Location(LocationManager.PASSIVE_PROVIDER);
        cityLocation.setLatitude(city.getLatitude());
        cityLocation.setLongitude(city.getLongitude());

            try {
                JSONArray list = new JSONArray(response);
                for (int i = 0; i < list.length(); i++) {
                    String address1 = "";
                    String address2 = "";
                    JSONObject json=list.getJSONObject(i);
                    String uuid = json.getString("osm_type").equals("node") ? "N" + json.getString("osm_id") : "W" + json.getString("osm_id");
                    String address = json.getString("address");
                    SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
                    AndroidAddressFormatter formatter = new AndroidAddressFormatter(true, (prefManager.getBoolean("pref_Debug",false)), (prefManager.getBoolean("pref_Debug",false)));

                    //fix issues with Ã¼ instead of ü, etc. OSM data is UTF-8 encoded
                    //String(byte[] bytes, Charset charset) constructs a new String by decoding the specified array of bytes using the specified charset.
                    //address = new String(address.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                    //remove \/ in address fields and spaces at end
                    //e.g. for Dublin JSONArray(response) adds \/  in Eire / Ireland
                    address1 = "";
                    try {
                        address1 = StringFormatUtils.removeNewline(formatter.format(address.replace("\\/","/").trim()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (Lavatory lavatory : lavatories){
                        if (lavatory.getUuid().equals(uuid)){
                            lavatory.setLatitude(json.getDouble("lat"));
                            lavatory.setLongitude(json.getDouble("lon"));
                            Location toiletLocation = new Location(LocationManager.PASSIVE_PROVIDER);
                            toiletLocation.setLatitude(lavatory.getLatitude());
                            toiletLocation.setLongitude(lavatory.getLongitude());
                            lavatory.setDistance(Math.round(cityLocation.distanceTo(toiletLocation)/10)/100.0);
                            lavatory.setAddress1(address1);
                            lavatory.setAddress2(address2);
                            dbHelper.updateLavatoryAddress(lavatory);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        Collections.sort(lavatories,(o1, o2) -> (int) (o1.getDistance()*1000 - o2.getDistance()*1000));
        ViewUpdater.updateLavatories(lavatories,cityId);
    }

    /**
     * Shows an error that the data could not be retrieved.
     *
     * @param error The error that occurred while executing the HTTP request.
     */
    @Override
    public void processFailScenario(final VolleyError error) {
        Handler h = new Handler(this.context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                if (NavigationActivity.isVisible) Toast.makeText(context, context.getResources().getString(R.string.error_fetch_lavatories), Toast.LENGTH_LONG).show();
            }
        });
    }
}
