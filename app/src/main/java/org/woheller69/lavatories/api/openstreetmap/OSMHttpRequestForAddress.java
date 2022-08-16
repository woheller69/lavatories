package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.woheller69.lavatories.BuildConfig;
import org.woheller69.lavatories.api.IHttpRequest;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.database.Station;
import org.woheller69.lavatories.http.HttpRequestType;
import org.woheller69.lavatories.http.VolleyHttpRequest;
import org.woheller69.lavatories.preferences.AppPreferencesManager;

import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to
 * Tankerkönig to retrieve the latest gas prices for all stored cities.
 */
public class OSMHttpRequestForAddress  {

    /**
     * Member variables.
     */
    private Context context;

    /**
     * @param context The context to use.
     */
    public OSMHttpRequestForAddress(Context context) {
        this.context = context;
    }

    /**
     * @see IHttpRequest#perform(float, float,int)
     */

    public void perform(int cityId, List<Station> stations) {
        org.woheller69.lavatories.http.IHttpRequest httpRequest = new VolleyHttpRequest(context, cityId);
        final String URL = getUrlForQueryingAddress(stations);
        httpRequest.make(URL, HttpRequestType.GET, new OSMProcessHttpRequestAddress(context,stations));
    }

    protected String getUrlForQueryingAddress(List<Station> stations) {
        String idString="";
        for (Station station:stations){
            idString=idString+"N"+station.getUuid()+",";
        }
        Log.d("Request",String.format(
                "%slookup?format=json&osm_ids=%s",
                BuildConfig.BASE_URL2,
                idString
        ));
        return String.format(
                "%slookup?format=json&osm_ids=%s",
                BuildConfig.BASE_URL2,
                idString
        );
    }
}
