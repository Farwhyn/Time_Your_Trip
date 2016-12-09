package com.planmytrip.johan.planmytrip;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

/**
 * Created by johan on 15.11.2016.
 */


public class TimerService extends Service {


    final int UPDATE_TIME_TEXTVIEW = 1;
    final int UPDATE_DISTANCE_TEXTVIEW = 2;
    final int NO_GPS_CONNECTION = 3;
    final int SERVICE_GOT_DESTROYED = 4;
    final int UNBIND_SERVICE = 5;

    private MediaPlayer mp = new MediaPlayer();
    Vibrator vib;
    private CountDownTimer timer;
    private double destLat;
    private double destLong;
    private String routeNo;
    private Handler myHandler;
    private Runnable runnable;
    private GPSHandler gpsHandler;
    private boolean hasPlayedAlarm = false;
    private boolean hasSetGPSTo10000 = false;
    private boolean hasSetGPSTo5000 = false;
    private boolean hasSetGPSTo3000 = false;
    boolean alarmEnabled = true;
    PowerManager.WakeLock wakeLock;

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    private final IBinder mBinder = new MyBinder();
    private Messenger outMessenger;

    @Override
    public IBinder onBind(Intent arg0) {
        Bundle extras = arg0.getExtras();
        Log.d("service", "onBind");
        // Get messager from the Activity
        if (extras != null) {
            Log.d("service", "onBind with extra");
            outMessenger = (Messenger) extras.get("MESSENGER");
            System.out.println("TimerService Bind service");

        }
        return mBinder;
    }

    public class MyBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == "clickOnIt"){
                mp.stop();
                vib.cancel();
                sendMessage(UNBIND_SERVICE,"");
                System.out.println("clickedOn it" + intent.toString());
                Intent intent1 = getPackageManager().getLaunchIntentForPackage(getPackageName());
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.setComponent(new ComponentName("com.planmytrip.johan.planmytrip", "com.planmytrip.johan.planmytrip.alarmTimer"));
                intent1.putExtra("UserClickedOnPermanentNotification", "UserClickedOnPermanentNotification");
                startActivity(intent1);

            }
            else {
                unregisterReceiver(this);
                doStopSelf();
                System.out.println("Deleted Notification" + intent.toString());
            }
        }
    };

    private void doStopSelf(){
        sendMessage(UNBIND_SERVICE,"");
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        Log.d("service", "onBind");
        // Get messager from the Activity
        if (extras != null) {
            if(intent.getAction()== "continue"){

            }
            else {
                Log.d("service", "onBind with extra");
                myHandler = new Handler();
                gpsHandler = new GPSHandler(this);
                vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                //Media Player to be used
                mp = MediaPlayer.create(this, R.raw.sound);

                Stop start = (Stop) intent.getSerializableExtra("startingStop");
                Stop destination = (Stop) intent.getSerializableExtra("destination");
                routeNo = intent.getStringExtra("selRoute");
                destLat = Double.parseDouble(destination.getLatitude());
                destLong = Double.parseDouble(destination.getLongitude());

                Intent intent1 = new Intent("delete");
                PendingIntent deleteIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
                registerReceiver(receiver, new IntentFilter("delete"));

                Intent intent2 = new Intent("clickOnIt");
                PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
                registerReceiver(receiver, new IntentFilter("clickOnIt"));


                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("Time your trip")
                        .setContentText("")
                        .setSmallIcon(R.mipmap.appicon)
                        .setDeleteIntent(deleteIntent)
                        .setContentIntent(clickIntent);

                PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
                wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                wakeLock.acquire();

                if(extras.containsKey("Restart")){
                    gpsHandler.requestGPSUpdates(500);
                    sendMessage(1,"Loading...");
                }
                else {
                    new TranslinkHandler(this).getEstimatedTimeFromGoogle(start.getLatitude(), start.getLongitude(), destination.getLatitude(), destination.getLongitude(), "now");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void getNearestBusStopServingRouteReturned(String latitude, String longitude, String errorMsg) {
        if (errorMsg == null) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW,"");
            new TranslinkHandler(this).getEstimatedTimeFromGoogle(latitude, longitude, String.valueOf(destLat), String.valueOf(destLong), "now");
        } else {
            System.out.println(errorMsg);
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "No Internet Connection");
            if(errorMsg.equalsIgnoreCase("404")){
                sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Your selected route does not seem to have any stops near you. Please select another route!");
            }
            gpsHandler.removeUpdates();
            runnable = new Runnable() {

                @Override
                public void run() {
                    if (!gpsHandler.requestGPSUpdates(1000)) {
                        gpsProviderDisabled();
                    }
                }
            };

            myHandler.postDelayed(runnable, 4000);


        }
    }

    public void setOutMessenger(Messenger messenger){
        outMessenger = messenger;
    }

    public void gotGPSUpdate(Location location) {
        System.out.println("gotGPSUpdate");

        double distance = gpsHandler.distance(destLat, location.getLatitude(), destLong, location.getLongitude());

        if (distance < 300) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Distance to destination: " + String.format("%.0f", distance) + " Meters");
            timer.cancel();
            sendMessage(UPDATE_TIME_TEXTVIEW, "a few seconds");

            if (!hasPlayedAlarm) {
                if (alarmEnabled) {
                    mp.start();
                    vib.vibrate(5000);

                    hasPlayedAlarm = true;
                }
            }
            if (!hasSetGPSTo3000) {
                gpsHandler.removeUpdates();
                gpsHandler.requestGPSUpdates(500);
                hasSetGPSTo3000 = true;
                runnable = new Runnable() {

                    @Override
                    public void run() {
                        doStopSelf();
                    }
                };

                myHandler.postDelayed(runnable, 120000);
            }

        } else if (distance < 600) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Distance to destination: " + String.format("%.0f", distance) + " Meters");
            if (!hasSetGPSTo5000) {
                gpsHandler.removeUpdates();
                gpsHandler.requestGPSUpdates(1000);
                hasSetGPSTo5000 = true;
            }


        }
        else if (distance < 1200) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Distance to destination: " + String.format("%.0f", distance) + " Meters");

            if (!hasSetGPSTo10000) {
                gpsHandler.removeUpdates();
                gpsHandler.requestGPSUpdates(3000);
                hasSetGPSTo10000 = true;
            }

        } else {
            hasSetGPSTo10000 = false;
            hasSetGPSTo5000 = false;
            hasSetGPSTo3000 = false;
            gpsHandler.removeUpdates();
            new TranslinkHandler(this).getNearestBusStopServingRoute(location.getLatitude(), location.getLongitude(), routeNo);
        }
    }

    public void setAlarmEnabled(boolean alarmEnabled) {
        this.alarmEnabled = alarmEnabled;
        if (!alarmEnabled) {
            mp.stop();
            vib.cancel();
        }
        System.out.println("Change alarmEnabled");
    }

    public void estimatedTimeReturned(String duration, String errorMsg) {
        if (errorMsg == null) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW,"");
            if (timer != null) {
                timer.cancel();
            }
            gpsHandler.removeUpdates();
            setTimer(Integer.parseInt(duration) * 1000);


            long nextGPSUpdate = Integer.parseInt(duration) * 1000 / 3;

            runnable = new Runnable() {

                @Override
                public void run() {
                    if (!gpsHandler.requestGPSUpdates(10000)) {
                        gpsProviderDisabled();
                    }
                }
            };

            myHandler.postDelayed(runnable, nextGPSUpdate - 10000);


        } else {
            System.out.println(errorMsg + " query returned with error");
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "No Internet Connection");
            gpsHandler.removeUpdates();
            if (!gpsHandler.requestGPSUpdates(5000)) {
                gpsProviderDisabled();
            }
            //timerTextView.setText("A server error occured. " + errorMsg);
        }
    }

    private void setTimer(final long countTime) {

        this.timer = new CountDownTimer(countTime, 1000) {

            public void onTick(long millisUntilFinished) {

                int totalSeconds = (int) millisUntilFinished / 1000;

                int hours = (totalSeconds % 86400) / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = (totalSeconds % 60);

                String time;
                if (hours > 0) {
                    time = hours + " hours\n" + minutes + " minutes\n" + seconds + " seconds!";
                } else if (minutes > 0) {
                    time = minutes + " minutes\n" + seconds + " seconds!";
                } else {
                    time = seconds + " seconds!";
                }
                sendMessage(UPDATE_TIME_TEXTVIEW, time);
            }


            public void onFinish() {
                sendMessage(UPDATE_TIME_TEXTVIEW, "Done!");
            }
        };

        timer.start();

    }

    private void sendMessage(int code, String messageString) {
        if(code == 1||code == 2){
            notificationBuilder.setContentText(messageString);
            notificationManager.notify(
                    1,
                    notificationBuilder.build());

        }
        try {
            Message message = new Message();
            message.arg1 = code;
            message.obj = messageString;
            outMessenger.send(message);
        } catch (RemoteException e) {

        }
    }

    public void gpsProviderDisabled() {
        sendMessage(NO_GPS_CONNECTION, "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        System.out.println("Service got destroyed");
        sendMessage(SERVICE_GOT_DESTROYED, "");
        myHandler.removeCallbacks(runnable);
        if (timer != null) {
            timer.cancel();
        }
        gpsHandler.removeUpdates();
        mp.stop();
        vib.cancel();
        notificationManager.cancelAll();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate TimerService");
    }
}
