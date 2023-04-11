package org.woheller69.lavatories.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.woheller69.lavatories.firststart.TutorialActivity;
import org.woheller69.lavatories.preferences.AppPreferencesManager;

/**
 * Created by yonjuni on 24.10.16.
 */

public class SplashActivity extends AppCompatActivity {
    private AppPreferencesManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(this));
        if (prefManager.isFirstTimeLaunch(this)){  //First time got to TutorialActivity
            Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        } else { //otherwise directly start LavSeekerActivity
            Intent mainIntent = new Intent(SplashActivity.this, LavSeekerActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        }
        SplashActivity.this.finish();
    }

}
