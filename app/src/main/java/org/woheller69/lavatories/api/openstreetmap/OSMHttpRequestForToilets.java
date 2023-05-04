package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import org.woheller69.lavatories.BuildConfig;
import org.woheller69.lavatories.http.HttpRequestType;
import org.woheller69.lavatories.http.VolleyHttpRequest;
import org.woheller69.lavatories.preferences.AppPreferencesManager;
import org.woheller69.lavatories.api.IHttpRequest;

/**
 * This class provides the functionality for making and processing HTTP requests to
 * Tankerkönig to retrieve the latest gas prices for all stored cities.
 */
public class OSMHttpRequestForToilets implements IHttpRequest {

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
     * @see IHttpRequest#perform(float, float,int)
     */
    @Override
    public void perform(float lat, float lon, int cityId) {
        org.woheller69.lavatories.http.IHttpRequest httpRequest = new VolleyHttpRequest(context, cityId);
        final String URL = getUrlForQueryingLavatories(context, lat, lon);
        httpRequest.make(URL, HttpRequestType.GET, new OSMProcessHttpRequestToilets(context));
    }

    protected String getUrlForQueryingLavatories(Context context, float lat, float lon) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return String.format(
                "%s?data=[out:json][timeout:5];(node[\"amenity\"=\"toilets\"](around:%s,%s,%s););out;>;out skel qt;",
                BuildConfig.BASE_URL,
                sharedPreferences.getString("pref_searchRadius","3000"),
                lat,
                lon
        );
    }
}
