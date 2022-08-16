package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.woheller69.lavatories.database.CityToWatch;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.database.Station;
import org.woheller69.lavatories.api.IDataExtractor;


public class OSMDataExtractor implements IDataExtractor {

    @Override
    public boolean wasCityFound(String data) {
        try {
            JSONObject json = new JSONObject(data);
            return json.has("elements");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Station extractStation(String data, int cityId, Context context) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Station station = new Station();
            station.setTimestamp((long) ((System.currentTimeMillis())/ 1000));
            SQLiteHelper db = SQLiteHelper.getInstance(context);
            CityToWatch city = db.getCityToWatch(cityId);
            Location cityLocation = new Location(LocationManager.PASSIVE_PROVIDER);
            cityLocation.setLatitude(city.getLatitude());
            cityLocation.setLongitude(city.getLongitude());

            JSONObject json = new JSONObject(data);

            station.setOpen(true);
            station.setBrand(" ");
            //if (json.getString("brand").equals("")) station.setBrand(json.getString("name"));
            station.setName(" ");
            station.setAddress1("Stasse");
            station.setAddress2("PLZ");
            station.setLatitude(json.getDouble("lat"));
            station.setLongitude(json.getDouble("lon"));
            Location toiletLocation = new Location(LocationManager.PASSIVE_PROVIDER);
            toiletLocation.setLatitude(station.getLatitude());
            toiletLocation.setLongitude(station.getLongitude());
            station.setDistance(Math.round(cityLocation.distanceTo(toiletLocation)/10)/100.0);
            station.setUuid(json.getString("id"));
            JSONObject tags = json.getJSONObject("tags");
            if (tags.has("operator")) station.setBrand(tags.getString("operator"));
            if (tags.has("opening_hours")) station.setName(tags.getString("opening_hours"));
            return station;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
