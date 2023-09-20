package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;
import org.woheller69.lavatories.BuildConfig;
import org.woheller69.lavatories.api.IHttpRequest;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.http.HttpRequestType;
import org.woheller69.lavatories.http.VolleyHttpRequest;

import java.util.List;

/**
 * This class provides the functionality for making and processing HTTP requests to
 * Nominatim API to retrieve the address for lavatories.
 */
public class OSMHttpRequestForAddress  {

    /**
     * Member variables.
     */
    private final Context context;

    /**
     * @param context The context to use.
     */
    public OSMHttpRequestForAddress(Context context) {
        this.context = context;
    }

    /**
     * @see IHttpRequest#perform(float, float,int)
     */

    public void perform(int cityId, List<Lavatory> lavatories) {
        org.woheller69.lavatories.http.IHttpRequest httpRequest = new VolleyHttpRequest(context, cityId);
        final String URL = getUrlForQueryingAddress(lavatories);
        httpRequest.make(URL, HttpRequestType.GET, new OSMProcessHttpRequestAddress(context, lavatories));
    }

    protected String getUrlForQueryingAddress(List<Lavatory> lavatories) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String idString="";
        for (Lavatory lavatory : lavatories){
            idString=idString + lavatory.getUuid()+",";
        }
        Log.d("Request",String.format(
                "%slookup?format=json&osm_ids=%s",
                sharedPreferences.getString("pref_Nominatim_URL", BuildConfig.BASE_URL2),
                idString
        ));
        return String.format(
                "%slookup?format=json&osm_ids=%s",
                sharedPreferences.getString("pref_Nominatim_URL", BuildConfig.BASE_URL2),
                idString
        );
    }
}
