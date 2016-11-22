package com.planmytrip.johan.planmytrip;

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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by johan on 21.11.2016.
 */

public class OfflineService extends Service {

    final int UPDATE_TIME_TEXTVIEW = 1;
    final int UPDATE_DISTANCE_TEXTVIEW = 2;
    final int NO_GPS_CONNECTION = 3;
    final int SERVICE_GOT_DESTROYED = 4;
    final int UNBIND_SERVICE = 5;

    private MediaPlayer mp = new MediaPlayer();
    Vibrator vib;
    Stop destination;
    private Handler myHandler;
    private Runnable runnable;
    private GPSHandler gpsHandler;
    private boolean hasPlayedAlarm = false;
    private boolean hasSetGPSTo10000 = false;
    private boolean hasSetGPSTo5000 = false;
    private boolean hasSetGPSTo3000 = false;
    boolean alarmEnabled = true;
    PowerManager.WakeLock wakeLock;
    String lastMessage = "";

    NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    private final IBinder mBinder = new OfflineService.MyBinder();
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
        OfflineService getService() {
            return OfflineService.this;
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
                intent1.setComponent(new ComponentName("com.planmytrip.johan.planmytrip", "com.planmytrip.johan.planmytrip.OfflineAlarm"));
                intent1.putExtra("UserClickedOnPermanentNotification", "UserClickedOnPermanentNotification");
                intent1.putExtra("destination",destination);
                intent1.putExtra("lastMessage", lastMessage);
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
        System.out.println("OnStartService OfflineService");
        // Get messager from the Activity
        if (extras != null) {
            if(intent.getAction()== "continue"){

            }
            else {
                System.out.println("OnStartService OfflineService");

                Log.d("service", "onBind with extra");
                myHandler = new Handler();
                gpsHandler = new GPSHandler(this);
                vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                //Media Player to be used
                mp = MediaPlayer.create(this, R.raw.sound);

                destination = (Stop) intent.getSerializableExtra("destination");

                Intent intent1 = new Intent("delete");
                PendingIntent deleteIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
                registerReceiver(receiver, new IntentFilter("delete"));

                Intent intent2 = new Intent("clickOnIt");
                PendingIntent clickIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
                registerReceiver(receiver, new IntentFilter("clickOnIt"));


                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("Time your trip")
                        .setContentText("You've received new messages.")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDeleteIntent(deleteIntent)
                        .setContentIntent(clickIntent);

                PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
                wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                wakeLock.acquire();

                if (!gpsHandler.requestGPSUpdates(3000)) {
                    gpsProviderDisabled();
                }

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void setOutMessenger(Messenger messenger){
        outMessenger = messenger;
    }

    private String convertDistance(double distance){
        if(distance > 1000){
            return String.format("%.1f", distance/1000) + " Kilometers";
        }
        else{
            return String.format("%.0f", distance) + " Meters";
        }
    }

    public void gotGPSUpdate(Location location) {
        System.out.println("gotGPSUpdate");

        double distance = gpsHandler.distance(Double.parseDouble(destination.getLatitude()), location.getLatitude(), Double.parseDouble(destination.getLongitude()), location.getLongitude());

        if (distance < 300) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Distance to destination: " + convertDistance(distance));

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

        } else if (distance < 1000) {
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Distance to destination: " + convertDistance(distance));
            if (!hasSetGPSTo5000) {
                gpsHandler.removeUpdates();
                gpsHandler.requestGPSUpdates(1000);
                hasSetGPSTo5000 = true;
            }


        } else{
            sendMessage(UPDATE_DISTANCE_TEXTVIEW, "Distance to destination: " + convertDistance(distance));

            if (!hasSetGPSTo10000) {
                gpsHandler.removeUpdates();
                gpsHandler.requestGPSUpdates(15000);
                hasSetGPSTo10000 = true;
            }

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        System.out.println("Service got destroyed");
        sendMessage(SERVICE_GOT_DESTROYED, "");
        myHandler.removeCallbacks(runnable);
        gpsHandler.removeUpdates();
        mp.stop();
        vib.cancel();
        notificationManager.cancelAll();
    }

    public void gpsProviderDisabled() {
        sendMessage(NO_GPS_CONNECTION, "");
    }

    private void sendMessage(int code, String messageString) {
        if(code == 1||code == 2){
            notificationBuilder.setContentText(messageString);
            notificationManager.notify(1, notificationBuilder.build());
            lastMessage = messageString;

        }
        try {
            Message message = new Message();
            message.arg1 = code;
            message.obj = messageString;
            outMessenger.send(message);
        } catch (RemoteException e) {

        }
    }

}
