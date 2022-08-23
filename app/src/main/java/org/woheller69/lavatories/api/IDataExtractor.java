package org.woheller69.lavatories.api;

import android.content.Context;

import org.woheller69.lavatories.database.Lavatory;

/**
 * This interface defines the frame of the functionality to extract information which
 * is returned by some API.
 */
public interface IDataExtractor {


    boolean wasCityFound(String data);


    Lavatory extractLavatory(String data, int cityId, Context context);


}
