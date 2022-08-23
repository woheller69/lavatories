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
import org.woheller69.lavatories.database.Lavatory;
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
    public Lavatory extractLavatory(String data, int cityId, Context context) {
        try {
            Lavatory lavatory = new Lavatory();
            lavatory.setTimestamp((long) ((System.currentTimeMillis())/ 1000));
            SQLiteHelper db = SQLiteHelper.getInstance(context);
            CityToWatch city = db.getCityToWatch(cityId);
            Location cityLocation = new Location(LocationManager.PASSIVE_PROVIDER);
            cityLocation.setLatitude(city.getLatitude());
            cityLocation.setLongitude(city.getLongitude());

            JSONObject json = new JSONObject(data);

            lavatory.setOperator(" ");
            lavatory.setOpeningHours(" ");
            lavatory.setAddress1(" ");
            lavatory.setAddress2(" ");
            lavatory.setLatitude(json.getDouble("lat"));
            lavatory.setLongitude(json.getDouble("lon"));
            Location toiletLocation = new Location(LocationManager.PASSIVE_PROVIDER);
            toiletLocation.setLatitude(lavatory.getLatitude());
            toiletLocation.setLongitude(lavatory.getLongitude());
            lavatory.setDistance(Math.round(cityLocation.distanceTo(toiletLocation)/10)/100.0);
            lavatory.setUuid(json.getString("id"));
            JSONObject tags = json.getJSONObject("tags");
            if (tags.has("operator")) lavatory.setOperator(tags.getString("operator"));
            if (tags.has("opening_hours")) lavatory.setOpeningHours(tags.getString("opening_hours"));
            if (tags.has("access") && tags.getString(("access")).contains("private"))  return null;
            lavatory.setWheelchair(tags.has("wheelchair") && !tags.getString(("wheelchair")).equals("no"));
            lavatory.setBabyChanging(tags.has("changing_table") && !tags.getString(("changing_table")).equals("no"));
            lavatory.setPaid(tags.has("fee") && !tags.getString(("fee")).equals("no"));
            return lavatory;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
