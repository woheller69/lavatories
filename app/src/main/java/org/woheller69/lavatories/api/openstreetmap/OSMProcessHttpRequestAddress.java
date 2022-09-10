package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.woheller69.AndroidAddressFormatter.AndroidAddressFormatter;
import org.woheller69.lavatories.R;
import org.woheller69.lavatories.activities.NavigationActivity;

import org.woheller69.lavatories.api.IProcessHttpRequest;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.ui.Help.StringFormatUtils;
import org.woheller69.lavatories.ui.updater.ViewUpdater;

import java.io.IOException;
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
        //Log.d("Request",response);
        this.dbHelper = SQLiteHelper.getInstance(context);
            try {
                JSONArray list = new JSONArray(response);
                for (int i = 0; i < list.length(); i++) {
                    String address1 = "";
                    String address2 = "";
                    String currentItem = list.get(i).toString();
                    JSONObject json = new JSONObject(currentItem);
                    String uuid = json.getString("osm_id");
                    JSONObject address = json.getJSONObject("address");

                    AndroidAddressFormatter formatter = new AndroidAddressFormatter(true, false, false);
                    try {
                        address1 = StringFormatUtils.removeNewline(formatter.format(address.toString().trim()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (Lavatory lavatory : lavatories){
                        if (lavatory.getUuid().equals(uuid)){
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
