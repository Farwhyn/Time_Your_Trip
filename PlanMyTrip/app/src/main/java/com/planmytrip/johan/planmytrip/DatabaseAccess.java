package com.planmytrip.johan.planmytrip;

/**
 * Created by james on 01/11/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.planmytrip.johan.planmytrip.Stop;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    //private String route_num = "099";
    private String route_id;
    private String trip_id;
    private String stop_id;
    private String prev_id = "";

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    public DatabaseAccess(Context context) {

        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {

        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }


    public Stop getOriginalStop(String stop_code) {
        Cursor cursor = database.rawQuery("SELECT * FROM stops WHERE stop_code=" + stop_code, null);
        Stop prevStop = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    prevStop = new Stop(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(4), cursor.getString(5));
                    return prevStop;
                }
            } finally {
                Log.d("route id", "error");
                cursor.close();
            }
        }
        return prevStop;

    }


    //function to see if a bus stop already exists in the database;
    public boolean checkFavorite(String stopcode) {
        Cursor cursor = database.rawQuery("SELECT * FROM favourite WHERE stop_code=" + stopcode, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                return true;
            }
            else
                return false;

        }

        else
            return false;
    }

    public void writeToFavourite(Stop stop) {
        database.execSQL("INSERT INTO favourite VALUES('" + stop.getStopID() + "', '" + stop.getStopCode() + "', '" + stop.getName() + "', '" + stop.getLatitude() + "', '" + stop.getLongitude() + "');");
    }

    public ArrayList<Stop> getFromFavourite() {
        ArrayList<Stop> list = new ArrayList<Stop>();
        Cursor cursor = database.rawQuery("SELECT * FROM favourite", null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Stop stop = new Stop(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                list.add(stop);
                cursor.moveToNext();
            }
        }

        return list;

    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of bus stops along a route
     */
    public ArrayList<Stop> getStops(String route_num, String destination) {
        ArrayList<Stop> list = new ArrayList<Stop>();
        Cursor cursor = database.rawQuery("SELECT * FROM routes WHERE route_short_name LIKE '%" + route_num + "%'", null);
        Stop newStop = null;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    route_id = cursor.getString(0);
                    Log.d("route id", route_id);
                }
            } finally {
                Log.d("route id", "error");
                cursor.close();
            }
        }

        else {
            return null;
        }

        if(destination.charAt(0) == 'U' && destination.charAt(2) == 'B' && destination.charAt(4) == 'C'){
            destination = "UBC";
        }

        cursor = database.rawQuery("SELECT * FROM trips WHERE route_id=" + route_id + " AND trip_headsign LIKE '%" + destination + "%'", null);
        if (cursor != null) {
            try {
                if (cursor.moveToPosition(2)) {
                    trip_id = cursor.getString(2);
                    Log.d("trip id", trip_id);
                }
            } finally {
                cursor.close();
            }
        }

        else {
            return null;
        }
        cursor = database.rawQuery("SELECT * FROM stop_times WHERE trip_id=" + trip_id, null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                stop_id = cursor.getString(3);
                Log.d("stop id", stop_id);
                Cursor c2 = database.rawQuery("SELECT * FROM stops WHERE stop_id=" + stop_id, null);
                if (c2 != null) {
                    try {
                        if (c2.moveToFirst()) {
                            newStop = new Stop(c2.getString(0), c2.getString(1), c2.getString(2), c2.getString(4), c2.getString(5));
                            list.add(newStop);
                        }
                    } finally {
                        c2.close();
                    }
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
        else
            Log.d("SQLite error", "bad query");
        return list;
    }

    public ArrayList<String> getAllRoutes(){
        ArrayList<String> list = new ArrayList<String>();
        String busRoute = "";
        String routeName = "";
        String both = "";
        Cursor cursor = database.rawQuery("SELECT * FROM routes", null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                busRoute = cursor.getString(2);
                routeName = cursor.getString(3);
                both = busRoute + " " + routeName;
                list.add(both);
                cursor.moveToNext();
            }
        }
        return list;
    }

    public ArrayList<String> getSkytrainRoutes(){
        ArrayList<String> list = new ArrayList<String>();
        String busRoute = "";
        String routeName = "";
        String both = "";
        Cursor cursor = database.rawQuery("SELECT * FROM routes WHERE agency_id=CMBC", null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                busRoute = cursor.getString(2);
                routeName = cursor.getString(3);
                both = busRoute + " " + routeName;
                list.add(both);
                cursor.moveToNext();
            }
        }
        return list;
    }
}


