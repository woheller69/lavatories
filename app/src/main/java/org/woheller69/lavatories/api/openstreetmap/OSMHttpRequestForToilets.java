package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.woheller69.lavatories.BuildConfig;
import org.woheller69.lavatories.http.HttpRequestType;
import org.woheller69.lavatories.http.IHttpRequest;
import org.woheller69.lavatories.http.VolleyHttpRequest;
import org.woheller69.lavatories.preferences.AppPreferencesManager;
import org.woheller69.lavatories.api.IHttpRequestForToilets;

/**
 * This class provides the functionality for making and processing HTTP requests to
 * Tankerkönig to retrieve the latest gas prices for all stored cities.
 */
public class OSMHttpRequestForToilets implements IHttpRequestForToilets {

    /**
     * Member variables.
     */
    private Context context;

    /**
     * @param context The context to use.
     */
    public OSMHttpRequestForToilets(Context context) {
        this.context = context;
    }

    /**
     * @see IHttpRequestForToilets#perform(float, float,int)
     */
    @Override
    public void perform(float lat, float lon, int cityId) {
        IHttpRequest httpRequest = new VolleyHttpRequest(context, cityId);
        final String URL = getUrlForQueryingStations(context, lat, lon);
        httpRequest.make(URL, HttpRequestType.GET, new OSMProcessHttpRequest(context));
    }

    protected String getUrlForQueryingStations(Context context, float lat, float lon) {
        AppPreferencesManager prefManager =
                new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("Request",String.format(
                "%s?data=[out:json][timeout:25];(node[\"amenity\"=\"toilets\"](around:%s,%s,%s););out;>;out skel qt;",
                BuildConfig.BASE_URL,
                sharedPreferences.getString("pref_searchRadius","3000"),
                lat,
                lon
        ));
        return String.format(
                "%s?data=[out:json][timeout:25];(node[\"amenity\"=\"toilets\"](around:%s,%s,%s););out;>;out skel qt;",
                BuildConfig.BASE_URL,
                sharedPreferences.getString("pref_searchRadius","3000"),
                lat,
                lon
        );
    }
}
