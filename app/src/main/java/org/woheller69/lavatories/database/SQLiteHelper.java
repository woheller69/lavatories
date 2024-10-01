package org.woheller69.lavatories.database;

import android.content.ContentValues;
import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import androidx.preference.PreferenceManager;
import org.woheller69.lavatories.preferences.AppPreferencesManager;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static SQLiteHelper instance = null;

    private static final String DATABASE_NAME = "Lavatories.db";

    //Names of tables in the database
    private static final String TABLE_CITIES_TO_WATCH = "CITIES_TO_WATCH";
    private static final String TABLE_LAVATORIES = "LAVATORIES";

    //Names of columns in TABLE_CITIES_TO_WATCH
    private static final String CITIES_TO_WATCH_ID = "cities_to_watch_id";
    private static final String CITIES_TO_WATCH_CITY_ID = "city_id";
    private static final String CITIES_TO_WATCH_COLUMN_RANK = "rank";
    private static final String CITIES_TO_WATCH_NAME = "city_name";
    private static final String CITIES_TO_WATCH_LONGITUDE = "longitude";
    private static final String CITIES_TO_WATCH_LATITUDE = "latitude";

    //Names of columns in TABLE_LAVATORIES
    private static final String LAVATORY_ID = "lavatory_id";
    private static final String LAVATORY_CITY_ID = "city_id";
    private static final String LAVATORY_TIMESTAMP = "timestamp";
    private static final String LAVATORY_WHEELCHAIR = "wheelchair";
    private static final String LAVATORY_BABY_CHANGING = "baby_changing";
    private static final String LAVATORY_PAID = "paid";
    private static final String LAVATORY_OPERATOR = "brand";
    private static final String LAVATORY_OPENING_HOURS = "name";
    private static final String LAVATORY_ADDRESS1 = "address1";
    private static final String LAVATORY_ADDRESS2 = "address2";
    private static final String LAVATORY_DISTANCE = "distance";
    private static final String LAVATORY_LATITUDE = "latitude";
    private static final String LAVATORY_LONGITUDE = "longitude";
    private static final String LAVATORY_UUID = "uuid";

    private SharedPreferences prefManager;
    private AppPreferencesManager appPref;

    /**
     * Create Table statements for all tables
     */
    private static final String CREATE_TABLE_LAVATORIES = "CREATE TABLE " + TABLE_LAVATORIES +
            "(" +
            LAVATORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            LAVATORY_CITY_ID + " INTEGER," +
            LAVATORY_TIMESTAMP + " LONG NOT NULL," +
            LAVATORY_WHEELCHAIR + " BIT," +
            LAVATORY_BABY_CHANGING + " BIT," +
            LAVATORY_PAID + " BIT," +
            LAVATORY_OPERATOR + " VARCHAR(200) NOT NULL," +
            LAVATORY_OPENING_HOURS + " VARCHAR(200) NOT NULL," +
            LAVATORY_ADDRESS1 + " VARCHAR(200) NOT NULL," +
            LAVATORY_ADDRESS2 + " VARCHAR(200) NOT NULL," +
            LAVATORY_DISTANCE + " REAL," +
            LAVATORY_LATITUDE + " REAL," +
            LAVATORY_LONGITUDE + " REAL," +
            LAVATORY_UUID + " VARCHAR(200) NOT NULL ); ";

    private static final String CREATE_TABLE_CITIES_TO_WATCH = "CREATE TABLE " + TABLE_CITIES_TO_WATCH +
            "(" +
            CITIES_TO_WATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CITIES_TO_WATCH_CITY_ID + " INTEGER," +
            CITIES_TO_WATCH_COLUMN_RANK + " INTEGER," +
            CITIES_TO_WATCH_NAME + " VARCHAR(100) NOT NULL," +
            CITIES_TO_WATCH_LONGITUDE + " REAL NOT NULL," +
            CITIES_TO_WATCH_LATITUDE + " REAL NOT NULL ); ";

    public static SQLiteHelper getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new SQLiteHelper(context.getApplicationContext());
        }
        return instance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        prefManager = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        appPref = new AppPreferencesManager(prefManager);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CITIES_TO_WATCH);
        db.execSQL(CREATE_TABLE_LAVATORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Methods for TABLE_CITIES_TO_WATCH
     */
    public synchronized long addCityToWatch(CityToWatch city) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITIES_TO_WATCH_CITY_ID, city.getCityId());
        values.put(CITIES_TO_WATCH_COLUMN_RANK, city.getRank());
        values.put(CITIES_TO_WATCH_NAME,city.getCityName());
        values.put(CITIES_TO_WATCH_LATITUDE,city.getLatitude());
        values.put(CITIES_TO_WATCH_LONGITUDE,city.getLongitude());

        long id=database.insert(TABLE_CITIES_TO_WATCH, null, values);

        //use id also instead of city id as unique identifier
        values.put(CITIES_TO_WATCH_CITY_ID,id);
        database.update(TABLE_CITIES_TO_WATCH, values, CITIES_TO_WATCH_ID + " = ?",
                new String[]{String.valueOf(id)});

        database.close();
        return id;
    }

    public synchronized CityToWatch getCityToWatch(int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        String[] arguments = {String.valueOf(id)};

        Cursor cursor = database.rawQuery(
                "SELECT " + CITIES_TO_WATCH_ID +
                        ", " + CITIES_TO_WATCH_CITY_ID +
                        ", " + CITIES_TO_WATCH_NAME +
                        ", " + CITIES_TO_WATCH_LONGITUDE +
                        ", " + CITIES_TO_WATCH_LATITUDE +
                        ", " + CITIES_TO_WATCH_COLUMN_RANK +
                        " FROM " + TABLE_CITIES_TO_WATCH +
                        " WHERE " + CITIES_TO_WATCH_CITY_ID + " = ?", arguments);

        CityToWatch cityToWatch = new CityToWatch();

        if (cursor != null && cursor.moveToFirst()) {
            cityToWatch.setId(Integer.parseInt(cursor.getString(0)));
            cityToWatch.setCityId(Integer.parseInt(cursor.getString(1)));
            cityToWatch.setCityName(cursor.getString(2));
            cityToWatch.setLongitude(Float.parseFloat(cursor.getString(3)));
            cityToWatch.setLatitude(Float.parseFloat(cursor.getString(4)));
            cityToWatch.setRank(Integer.parseInt(cursor.getString(5)));

            cursor.close();
        }
        database.close();
        return cityToWatch;
    }


    public synchronized List<CityToWatch> getAllCitiesToWatch() {
        List<CityToWatch> cityToWatchList = new ArrayList<>();

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + CITIES_TO_WATCH_ID +
                        ", " + CITIES_TO_WATCH_CITY_ID +
                        ", " + CITIES_TO_WATCH_NAME +
                        ", " + CITIES_TO_WATCH_LONGITUDE +
                        ", " + CITIES_TO_WATCH_LATITUDE +
                        ", " + CITIES_TO_WATCH_COLUMN_RANK +
                        " FROM " + TABLE_CITIES_TO_WATCH
                , new String[]{});

        CityToWatch cityToWatch;

        if (cursor.moveToFirst()) {
            do {
                cityToWatch = new CityToWatch();
                cityToWatch.setId(Integer.parseInt(cursor.getString(0)));
                cityToWatch.setCityId(Integer.parseInt(cursor.getString(1)));
                cityToWatch.setCityName(cursor.getString(2));
                cityToWatch.setLongitude(Float.parseFloat(cursor.getString(3)));
                cityToWatch.setLatitude(Float.parseFloat(cursor.getString(4)));
                cityToWatch.setRank(Integer.parseInt(cursor.getString(5)));

                cityToWatchList.add(cityToWatch);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return cityToWatchList;
    }

    public synchronized void updateCityToWatch(CityToWatch cityToWatch) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CITIES_TO_WATCH_CITY_ID, cityToWatch.getCityId());
        values.put(CITIES_TO_WATCH_COLUMN_RANK, cityToWatch.getRank());
        values.put(CITIES_TO_WATCH_NAME,cityToWatch.getCityName());
        values.put(CITIES_TO_WATCH_LATITUDE,cityToWatch.getLatitude());
        values.put(CITIES_TO_WATCH_LONGITUDE,cityToWatch.getLongitude());

        database.update(TABLE_CITIES_TO_WATCH, values, CITIES_TO_WATCH_ID + " = ?",
                new String[]{String.valueOf(cityToWatch.getId())});
        database.close();
    }

    public synchronized void deleteCityToWatch(CityToWatch cityToWatch) {

        //First delete all price data for city which is deleted
        deleteLavatoriesByCityId(cityToWatch.getCityId());

        //Now remove city from CITIES_TO_WATCH
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_CITIES_TO_WATCH, CITIES_TO_WATCH_ID + " = ?",
                new String[]{Integer.toString(cityToWatch.getId())});
        database.close();
    }

    public synchronized int getWatchedCitiesCount() {
        SQLiteDatabase database = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(database, TABLE_CITIES_TO_WATCH);
        database.close();
        return (int) count;
    }

    public int getMaxRank() {
        List<CityToWatch> cities = getAllCitiesToWatch();
        int maxRank = 0;
        for (CityToWatch ctw : cities) {
            if (ctw.getRank() > maxRank) maxRank = ctw.getRank();
        }
        return maxRank;
    }


    /**
     * Methods for TABLE_LAVATORIES
     */
    public synchronized void addLavatory(Lavatory lavatory) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LAVATORY_CITY_ID, lavatory.getCity_id());
        values.put(LAVATORY_TIMESTAMP, lavatory.getTimestamp());
        values.put(LAVATORY_WHEELCHAIR, lavatory.isWheelchair());
        values.put(LAVATORY_BABY_CHANGING, lavatory.isBabyChanging());
        values.put(LAVATORY_PAID, lavatory.isPaid());
        values.put(LAVATORY_OPERATOR, lavatory.getOperator());
        values.put(LAVATORY_OPENING_HOURS, lavatory.getOpeningHours());
        values.put(LAVATORY_ADDRESS1, lavatory.getAddress1());
        values.put(LAVATORY_ADDRESS2, lavatory.getAddress2());
        values.put(LAVATORY_DISTANCE, lavatory.getDistance());
        values.put(LAVATORY_LATITUDE, lavatory.getLatitude());
        values.put(LAVATORY_LONGITUDE, lavatory.getLongitude());
        values.put(LAVATORY_UUID, lavatory.getUuid());
        database.insert(TABLE_LAVATORIES, null, values);
        database.close();
    }

    public synchronized void deleteAllLavatories() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from " + TABLE_LAVATORIES);
        database.close();
    }

    public synchronized void deleteLavatoriesByCityId(int cityId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_LAVATORIES, LAVATORY_CITY_ID + " = ?",
                new String[]{Integer.toString(cityId)});
        database.close();
    }

    public synchronized List<Lavatory> getLavatoriesByCityId(int cityId) {
        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.query(TABLE_LAVATORIES,
                new String[]{LAVATORY_ID,
                        LAVATORY_CITY_ID,
                        LAVATORY_TIMESTAMP,
                        LAVATORY_WHEELCHAIR,
                        LAVATORY_BABY_CHANGING,
                        LAVATORY_PAID,
                        LAVATORY_OPERATOR,
                        LAVATORY_OPENING_HOURS,
                        LAVATORY_ADDRESS1,
                        LAVATORY_ADDRESS2,
                        LAVATORY_DISTANCE,
                        LAVATORY_LATITUDE,
                        LAVATORY_LONGITUDE,
                        LAVATORY_UUID}
                , LAVATORY_CITY_ID + "=?",
                new String[]{String.valueOf(cityId)}, null, null, null, null);

        List<Lavatory> list = new ArrayList<>();
        Lavatory lavatory;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                lavatory = new Lavatory();
                lavatory.setId(Integer.parseInt(cursor.getString(0)));
                lavatory.setCity_id(Integer.parseInt(cursor.getString(1)));
                lavatory.setTimestamp(Long.parseLong(cursor.getString(2)));
                lavatory.setWheelchair(cursor.getString(3).equals("1"));
                lavatory.setBabyChanging(cursor.getString(4).equals("1"));
                lavatory.setPaid(cursor.getString(5).equals("1"));
                lavatory.setOperator(cursor.getString(6));
                lavatory.setOpeningHours(cursor.getString(7));
                lavatory.setAddress1(cursor.getString(8));
                lavatory.setAddress2(cursor.getString(9));
                lavatory.setDistance(Double.parseDouble(cursor.getString(10)));
                lavatory.setLatitude(Double.parseDouble(cursor.getString(11)));
                lavatory.setLongitude(Double.parseDouble(cursor.getString(12)));
                lavatory.setUuid(cursor.getString(13));
                list.add(lavatory);
            } while (cursor.moveToNext());

            cursor.close();
        }
        Comparator<Lavatory> comparator = (o1, o2) -> {
            int specialCompare = prefManager.getBoolean("pref_BabyPrio", true) ? Boolean.compare(o2.isBabyChanging(), o1.isBabyChanging()) : Boolean.compare(o2.isWheelchair(), o1.isWheelchair()) ;
            if (specialCompare == 0 || !appPref.isSpecialLavatorySort())
            {
                int distCompare = (int) (o1.getDistance()*1000 - o2.getDistance()*1000);
                return distCompare; // sort by dist
            }
            return specialCompare; // sort by Baby Changing / Wheel chair
        };
        Collections.sort(list, comparator);
        database.close();
        return list;
    }

    public static int getWidgetCityID(Context context) {
        SQLiteHelper db = SQLiteHelper.getInstance(context);
        int cityID=0;
        List<CityToWatch> cities = db.getAllCitiesToWatch();
        int rank=cities.get(0).getRank();
        for (int i = 0; i < cities.size(); i++) {   //find cityID for first city to watch = lowest Rank
            CityToWatch city = cities.get(i);
            if (city.getRank() <= rank ){
                rank=city.getRank();
                cityID = city.getCityId();
            }
        }
        return cityID;
    }

}
