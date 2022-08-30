package org.woheller69.lavatories.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import androidx.core.app.JobIntentService;
import android.widget.Toast;

import org.woheller69.lavatories.BuildConfig;
import org.woheller69.lavatories.R;
import org.woheller69.lavatories.activities.NavigationActivity;
import org.woheller69.lavatories.database.CityToWatch;
import org.woheller69.lavatories.database.Lavatory;
import org.woheller69.lavatories.database.SQLiteHelper;
import org.woheller69.lavatories.api.IHttpRequest;
import org.woheller69.lavatories.api.openstreetmap.OSMHttpRequestForToilets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;

/**
 * This class provides the functionality to fetch price data for a given city as a background
 * task.
 */

public class UpdateDataService extends JobIntentService {

    public static final String UPDATE_SINGLE_ACTION = "UPDATE_SINGLE_ACTION";
    public static final String SKIP_UPDATE_INTERVAL = "skipUpdateInterval";
    private static final long MIN_UPDATE_INTERVAL=20;

    private SQLiteHelper dbHelper;
    private SharedPreferences prefManager;

    /**
     * Constructor.
     */
    public UpdateDataService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = SQLiteHelper.getInstance(getApplicationContext());
        prefManager = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onHandleWork(Intent intent) {
        if (!isOnline()) {
            Handler h = new Handler(getApplicationContext().getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    if (NavigationActivity.isVisible) Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        if (intent != null) {
            if (UPDATE_SINGLE_ACTION.equals(intent.getAction())) handleUpdateSingle(intent);
        }
    }



    private void handleUpdateSingle(Intent intent) {
        int cityId = intent.getIntExtra("cityId",-1);
        CityToWatch city = dbHelper.getCityToWatch(cityId);
        handleUpdateLavatoriesAction(cityId, city.getLatitude(), city.getLongitude());
    }

    private void handleUpdateLavatoriesAction(int cityId, float lat, float lon) {
        boolean updateNow = true;

        long timestamp = 0;
        long systemTime = System.currentTimeMillis() / 1000;

        List<Lavatory> lavatories = dbHelper.getLavatoriesByCityId(cityId);
        if (lavatories.size() > 0) {             // check timestamp of lavatories
            timestamp = lavatories.get(0).getTimestamp();
        }

        // check timestamp of the current lavatories
        if ((timestamp+MIN_UPDATE_INTERVAL-systemTime)>0) updateNow=false;  //even if updateNow is true, never update if less than MIN_UPDATE_INTERVAL s

        // Update if update forced or if a certain time has passed
        if (updateNow) {
                IHttpRequest lavatoriesRequest = new OSMHttpRequestForToilets(getApplicationContext());
                lavatoriesRequest.perform(lat, lon, cityId);
        }
    }

    private boolean isOnline() {
        try {
            URL url = new URL(BuildConfig.BASE_URL);
            InetAddress inetAddress = InetAddress.getByName(url.getHost());
            return inetAddress.isReachable(2000);
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }
}
