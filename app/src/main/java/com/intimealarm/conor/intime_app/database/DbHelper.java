package com.intimealarm.conor.intime_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.utilities.Helper;
import com.intimealarm.conor.intime_app.models.Alarm;

import java.util.ArrayList;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 09/01/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    // Variables
    Helper help;
    Context context;

    public static final String DB_NAME = "InTimeAlarm";
    public static final int DB_VERSION = 15;

    public static final String TABLE_ALARMS = "Alarms";
    public static final String A_COLUMN_ID = "_id";
    public static final String A_COLUMN_TIME = "time";
    public static final String A_COLUMN_TO = "loc_to";
    public static final String A_COLUMN_FROM = "loc_from";
    public static final String A_COLUMN_DAYS = "days";
    public static final String A_COLUMN_DUETIME = "due_time";
    public static final String A_COLUMN_ISACTIVE = "isActive";
    public static final String A_COLUMN_ISPUBLIC = "isPublic";
    public static final String A_COLUMN_TMODES = "tmodes";
    public static final String A_COLUMN_PREPTIME = "prep_time";

    public static final String TABLE_LOCATION = "Locations";
    public static final String LOC_COLUMN_ID = "_id";
    public static final String LOC_COLUMN_LABLE = "lable";
    public static final String LOC_COLUMN_ADDRESS = "address";
    public static final String LOC_COLUMN_LAT = "lat";
    public static final String LOC_COLUMN_LNG = "lng";

    private static final String DATABASE_CREATE_ALARMS =
            "CREATE TABLE " + TABLE_ALARMS + "( " +
                    A_COLUMN_ID + " INTEGER primary key autoincrement," +
                    A_COLUMN_TIME + " TEXT not null," +
                    A_COLUMN_TO + " INTEGER not null," +
                    A_COLUMN_FROM + " INTEGER not null," +
                    A_COLUMN_DAYS + " TEXT not null," +
                    A_COLUMN_DUETIME + " TEXT not null," +
                    A_COLUMN_PREPTIME + " INTEGER not null," +
                    A_COLUMN_ISACTIVE + " TEXT not null," +
                    A_COLUMN_TMODES + " TEXT not null," +
                    A_COLUMN_ISPUBLIC + " TEXT not null);";

    private static final String DATABASE_CREATE_LOCATIONS =
            "CREATE TABLE " + TABLE_LOCATION + "( " +
                    LOC_COLUMN_ID + " INTEGER primary key autoincrement,"+
                    LOC_COLUMN_ADDRESS + " TEXT not null,"+
                    LOC_COLUMN_LABLE + " TEXT not null," +
                    LOC_COLUMN_LAT + " REAL not null,"+
                    LOC_COLUMN_LNG + " REAL not null);";


    // Constructor
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        help = new Helper();
    }

    // Create Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_ALARMS);
        sqLiteDatabase.execSQL(DATABASE_CREATE_LOCATIONS);

    }

    // On Database Upgrade
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(sqLiteDatabase);

    }

    /*
     * Alarm CRUD methods
     */

    // Method to add a Alarm
    public Alarm addAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(A_COLUMN_ID, alarm.getId());
        values.put(A_COLUMN_TIME, alarm.getTime());
        values.put(A_COLUMN_FROM, alarm.getFrom());
        values.put(A_COLUMN_TO, alarm.getTo());
        values.put(A_COLUMN_DUETIME, alarm.getDueTime());
        values.put(A_COLUMN_PREPTIME, alarm.getPreptime());
        values.put(A_COLUMN_DAYS, help.arrToString(alarm.getDays()));
        values.put(A_COLUMN_ISACTIVE, alarm.getActive());
        values.put(A_COLUMN_ISPUBLIC, alarm.getPublic());
        values.put(A_COLUMN_TMODES, help.arrToString(alarm.getTmodes()));


        db.insert(TABLE_ALARMS, null, values);
        db.close();
        return alarm;
    }

    // Method to Retrieve All Alarms
    public ArrayList<Alarm> allAlarms() {
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS +" ORDER BY " + A_COLUMN_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                String time, dueTime;
                int[] days;
                String[] tmodes;
                boolean isActive, isPublic;
                int id, to, from, prepTime;

                id = cursor.getInt(cursor.getColumnIndex(A_COLUMN_ID));
                time = cursor.getString(cursor.getColumnIndex(A_COLUMN_TIME));
                dueTime = cursor.getString(cursor.getColumnIndex(A_COLUMN_DUETIME));
                to = cursor.getInt(cursor.getColumnIndex(A_COLUMN_TO));
                from = cursor.getInt(cursor.getColumnIndex(A_COLUMN_FROM));
                prepTime = cursor.getInt(cursor.getColumnIndex(A_COLUMN_PREPTIME));
                days = help.stringToArr(cursor.getString(cursor.getColumnIndex(A_COLUMN_DAYS)));
                isActive = (cursor.getString(cursor.getColumnIndex(A_COLUMN_ISACTIVE)).equals("1"));
                isPublic = (cursor.getString(cursor.getColumnIndex(A_COLUMN_ISPUBLIC)).equals("1"));
                tmodes = help.stringToStringArr(cursor.getString(cursor.getColumnIndex(A_COLUMN_TMODES)));

                alarms.add(new Alarm(
                        id,
                        time,
                        days,
                        from,
                        to,
                        dueTime,
                        isActive,
                        isPublic,
                        tmodes,
                        prepTime));

            } while (cursor.moveToNext());
        }

        db.close();

        return alarms;
    }

    // Method to Delete a Alarms
    public void deleteAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
//       Delete with time and days
//      db.execSQL("DELETE FROM " + TABLE_ALARMS +
//                " WHERE " + COLUMN_TIME + "='" + alarm.getDays() + "' " +
//                "AND " + COLUMN_DAYS + "='" + arrToString(alarm.getDays()) + "'");

      db.execSQL("DELETE FROM " + TABLE_ALARMS +
                " WHERE " + A_COLUMN_ID + "=" + alarm.getId());


        db.close();
    }

    // Method to Update Alarm
    public void updateAlarm(Alarm a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(A_COLUMN_TIME, a.getTime());
        values.put(A_COLUMN_FROM, a.getFrom());
        values.put(A_COLUMN_TO, a.getTo());
        values.put(A_COLUMN_DUETIME, a.getDueTime());
        values.put(A_COLUMN_PREPTIME, a.getPreptime());
        values.put(A_COLUMN_DAYS, help.arrToString(a.getDays()));
        values.put(A_COLUMN_ISACTIVE, a.getActive());
        values.put(A_COLUMN_ISPUBLIC, a.getPublic());
        values.put(A_COLUMN_TMODES, help.arrToString(a.getTmodes()));

        db.update(TABLE_ALARMS, values, A_COLUMN_ID+"="+a.getId(),null );
        db.close();
    }

    // Set Alarm to Inactive
    public void disableAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(A_COLUMN_ISACTIVE, false);

        db.update(TABLE_ALARMS, values, A_COLUMN_ID+"="+id,null );
        db.close();
    }

    /*
     * Location CRUD methods
     */

    // Add Location
    public Location addLocation(Location loc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LOC_COLUMN_ID, loc.getId());
        values.put(LOC_COLUMN_LABLE, loc.getLable());
        values.put(LOC_COLUMN_ADDRESS, loc.getAddress());
        values.put(LOC_COLUMN_LAT, loc.getLat());
        values.put(LOC_COLUMN_LNG, loc.getLng());

        db.insert(TABLE_LOCATION, null, values);
        db.close();

        return loc;
    }

    // Get All Locations
    public ArrayList<Location> allLocations(){
        ArrayList<Location> locs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION +" ORDER BY " + LOC_COLUMN_LABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                String address, lable;
                double lat, lng;
                int id;

                id = cursor.getInt(cursor.getColumnIndex(LOC_COLUMN_ID));
                address = cursor.getString(cursor.getColumnIndex(LOC_COLUMN_ADDRESS));
                lable = cursor.getString(cursor.getColumnIndex(LOC_COLUMN_LABLE));
                lat = cursor.getDouble(cursor.getColumnIndex(LOC_COLUMN_LAT));
                lng = cursor.getDouble(cursor.getColumnIndex(LOC_COLUMN_LNG));

                locs.add(new Location(id,lable, address,lat,lng));

            } while (cursor.moveToNext());
        }

        db.close();

        return locs;
    }

    // Get Single Location
    public Location getLoc(int id){
        Location loc = new Location();
        if (id == -1){
            return loc;
        }

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION +" WHERE " + LOC_COLUMN_ID +" = "+ id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                String address, lable;
                double lat, lng;
                int _id;

                _id = cursor.getInt(cursor.getColumnIndex(LOC_COLUMN_ID));
                address = cursor.getString(cursor.getColumnIndex(LOC_COLUMN_ADDRESS));
                lable = cursor.getString(cursor.getColumnIndex(LOC_COLUMN_LABLE));
                lat = cursor.getDouble(cursor.getColumnIndex(LOC_COLUMN_LAT));
                lng = cursor.getDouble(cursor.getColumnIndex(LOC_COLUMN_LNG));

                loc = new Location(_id,lable, address,lat,lng);

            } while (cursor.moveToNext());
        }

        db.close();

        return loc;
    }

    // Delete Location
    public void deleteLocation(Location loc){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_LOCATION +
                " WHERE " + LOC_COLUMN_ID + "=" + loc.getId());


        db.close();
    }

    // Update Location
    public void updateLocation(Location loc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LOC_COLUMN_LABLE, loc.getLable());
        values.put(LOC_COLUMN_ADDRESS, loc.getAddress());
        values.put(LOC_COLUMN_LAT, loc.getLat());
        values.put(LOC_COLUMN_LNG, loc.getLng());

        db.update(TABLE_LOCATION, values, LOC_COLUMN_ID+"="+loc.getId(),null );
        db.close();
    }

}
