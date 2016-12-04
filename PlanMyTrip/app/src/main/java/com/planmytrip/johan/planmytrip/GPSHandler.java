package com.planmytrip.johan.planmytrip;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.Timer;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by johan on 06.11.2016.
 */

public class GPSHandler {

    private LocationManager locationManager;
    private Context context;
    LocationListener listener;

    public GPSHandler(Context context){
        this.context = context;
        setup();
    }

    public void setup(){
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (context instanceof TimerService) {
                    ((TimerService) context).gotGPSUpdate(location);
                }
                else{
                    ((OfflineService) context).gotGPSUpdate(location);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                if (context instanceof TimerService) {
                    ((TimerService) context).gpsProviderDisabled();
                }
                else{
                    ((OfflineService) context).gpsProviderDisabled();
                }
            }
        };

    }

    public double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public boolean requestGPSUpdates(long time){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            }
            return false;
        }

        System.out.println("requestUpdates " + time);
        locationManager.requestLocationUpdates("gps", time, 0, listener);
        return true;
    }

    public boolean removeUpdates(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            }
            return false;
        }
        locationManager.removeUpdates(listener);
        System.out.println("removeUpdates");
        return true;
    }



}
