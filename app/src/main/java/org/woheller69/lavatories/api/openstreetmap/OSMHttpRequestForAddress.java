package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.util.Log;

import org.woheller69.lavatories.BuildConfig;
import org.woheller69.lavatories.api.IHttpRequest;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.http.HttpRequestType;
import org.woheller69.lavatories.http.VolleyHttpRequest;

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

    public void perform(int cityId, List<Lavatory> lavatories) {
        org.woheller69.lavatories.http.IHttpRequest httpRequest = new VolleyHttpRequest(context, cityId);
        final String URL = getUrlForQueryingAddress(lavatories);
        httpRequest.make(URL, HttpRequestType.GET, new OSMProcessHttpRequestAddress(context, lavatories));
    }

    protected String getUrlForQueryingAddress(List<Lavatory> lavatories) {
        String idString="";
        for (Lavatory lavatory : lavatories){
            idString=idString+"N"+ lavatory.getUuid()+",";
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
