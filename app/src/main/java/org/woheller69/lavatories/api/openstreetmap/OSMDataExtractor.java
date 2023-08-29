package org.woheller69.lavatories.api.openstreetmap;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.api.IDataExtractor;
import java.nio.charset.StandardCharsets;


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
            //fix issues with Ã¼ instead of ü, etc. OSM data is UTF-8 encoded
            //Overpass-API does not provide info about utf-8 charset in header
            //String(byte[] bytes, Charset charset) constructs a new String by decoding the specified array of bytes using the specified charset.
            data = (new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            Lavatory lavatory = new Lavatory();
            lavatory.setTimestamp((long) ((System.currentTimeMillis())/ 1000));

            JSONObject json = new JSONObject(data);
            if (!json.has("tags")) return null;

            lavatory.setOperator(" ");
            lavatory.setOpeningHours(" ");
            lavatory.setAddress1(" ");
            lavatory.setAddress2(" ");
            lavatory.setUuid(json.getString("type").equals("node") ? "N" + json.getString("id") : "W" + json.getString("id"));
            JSONObject tags = json.getJSONObject("tags");
            if (tags.has("amenity") && tags.getString("amenity").contains("toilets")) {
                if (tags.has("operator")) lavatory.setOperator(tags.getString("operator"));
                if (tags.has("opening_hours")) lavatory.setOpeningHours(tags.getString("opening_hours"));
                if (tags.has("access") && tags.getString(("access")).contains("private"))
                    return null;
                lavatory.setWheelchair(tags.has("wheelchair") && !tags.getString(("wheelchair")).equals("no"));
                lavatory.setBabyChanging(tags.has("changing_table") && !tags.getString(("changing_table")).equals("no"));
                lavatory.setPaid(tags.has("fee") && !tags.getString(("fee")).equals("no"));
                return lavatory;
            } else if (tags.has("toilets") && tags.getString("toilets").contains("yes")){
                if (tags.has("name")) lavatory.setOperator(tags.getString("name"));
                if (tags.has("opening_hours")) lavatory.setOpeningHours(tags.getString("opening_hours"));
                if (tags.has("toilets:access") && (tags.getString(("toilets:access")).contains("customers") || tags.getString(("toilets:access")).contains("no")))
                    return null;
                lavatory.setBabyChanging(tags.has("changing_table") && !tags.getString(("changing_table")).equals("no"));
                lavatory.setWheelchair(tags.has("toilets:wheelchair") && !tags.getString(("toilets:wheelchair")).equals("no"));
                lavatory.setPaid(tags.has("toilets:fee") && !tags.getString(("toilets:fee")).equals("no"));
                return lavatory;
            } else {
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
