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

            if (json.has("diesel") && !json.isNull("diesel")) station.setDiesel(json.getDouble("diesel"));
            if (json.has("e5") && !json.isNull("e5")) station.setE5( json.getDouble("e5"));
            if (json.has("e10") && !json.isNull("e10")) station.setE10( json.getDouble("e10"));

            if (json.has("price")) {
                if (!json.isNull("price")) {
                    switch (sharedPreferences.getString("pref_type", "all")) {
                        case "diesel":
                            station.setDiesel(json.getDouble("price"));
                            break;
                        case "e5":
                            station.setE5(json.getDouble("price"));
                            break;
                        case "e10":
                            station.setE10(json.getDouble("price"));
                            break;
                    }
                } else return null;
            }
            station.setOpen(true);
            station.setBrand("Brand");
            //if (json.getString("brand").equals("")) station.setBrand(json.getString("name"));
            station.setName("Test");
            station.setAddress1("Stasse");
            station.setAddress2("PLZ");
            //station.setDistance(json.getDouble("dist"));
            station.setLatitude(json.getDouble("lat"));
            station.setLongitude(json.getDouble("lon"));
            Location toiletLocation = new Location(LocationManager.PASSIVE_PROVIDER);
            toiletLocation.setLatitude(station.getLatitude());
            toiletLocation.setLongitude(station.getLongitude());
            station.setDistance(Math.round(cityLocation.distanceTo(toiletLocation)/10)/100.0);
            station.setUuid(json.getString("id"));

            return station;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
