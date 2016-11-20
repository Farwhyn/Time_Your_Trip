package com.planmytrip.johan.planmytrip;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Navjashan on 29.10.2016.
 */

public class alarmTimer extends AppCompatActivity {

    private TextView locationTextView;
    private TextView timerTextView;
    boolean alarmEnabled = true;
    private Button stopAlarm;


    private TimerService myServiceBinder;
    public ServiceConnection myConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((TimerService.MyBinder) binder).getService();
            Log.d("ServiceConnection", "connected");
            myServiceBinder.setOutMessenger(new Messenger(myHandler1));
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d("ServiceConnection", "disconnected");
            myServiceBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("onCreateAlarmTimer");

        //Handles the setting up part for the class
        super.onCreate(savedInstanceState);

        Intent intentFromLastActivity = getIntent();

        if (!(intentFromLastActivity.hasExtra("UserClickedOnPermanentNotification"))) {

            doBindService();
            doStartService(intentFromLastActivity);
        }
        else{
            doBindService();
        }
        setContentView(R.layout.activity_alarm_timer);
        timerTextView = (TextView) findViewById(R.id.textView3);
        timerTextView.setText("Loading...");
        locationTextView = (TextView) findViewById(R.id.locationTextView);

        //Alarm configurations for setting and stopping it
        stopAlarm = (Button) this.findViewById(R.id.button3);
        stopAlarm.setText("Stop Alarm");
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmEnabled) {
                    stopAlarm.setText("Set Alarm");
                    myServiceBinder.setAlarmEnabled(false);
                    alarmEnabled = false;
                    System.out.println("onClick stop alarm");

                } else {
                    stopAlarm.setText("Stop Alarm");
                    alarmEnabled = true;
                    myServiceBinder.setAlarmEnabled(true);
                }
            }
        });

        LocationManager locationManagerContext = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GPSchecker locationManager = new GPSchecker(locationManagerContext);
        if (!locationManager.isLocationEnabled()) {
            showAlert();
        }


    }


    public void onClick(View view){

        doStopService();
        Intent Reset = new Intent(this, MainActivity.class);
        startActivity(Reset);

    }


    public Handler myHandler1 = new Handler() {
        public void handleMessage(Message message) {
            switch (message.arg1) {
                case 1:
                    updateClock(message);
                    break;
                case 2:
                    updateDistance(message);
                    break;
                case 3:
                    showAlert();
                    break;
                case 4:
                    serviceGotDestroyed();
                case 5:
                    doUnbindService();
                    break;
                default:
                    break;
            }
        }
    };

    private void serviceGotDestroyed() {
        timerTextView.setText("Done!");
    }

    private void doUnbindService() {
        if (myServiceBinder != null) {
            unbindService(myConnection);
            myServiceBinder = null;
        }
    }

    private void doStopService() {

        if (myServiceBinder != null) {
            unbindService(myConnection);
            myServiceBinder = null;
        }
        stopService(new Intent(this, TimerService.class));

    }

    public void doBindService() {
        Intent intent = new Intent(this, TimerService.class);
        Messenger messenger = new Messenger(myHandler1);
        intent.putExtra("MESSENGER", messenger);
        bindService(intent, myConnection, Context.BIND_IMPORTANT);
        System.out.println("alarmTimer: doBindService");
    }

    public void doStartService(Intent timeIntent) {

        Intent intent = new Intent(this, TimerService.class);
        Stop start = (Stop) timeIntent.getSerializableExtra("startingStop");
        Stop destination = (Stop) timeIntent.getSerializableExtra("destination");
        String routeNo = timeIntent.getStringExtra("selRoute");
        intent.putExtra("startingStop", start);
        intent.putExtra("destination", destination);
        intent.putExtra("selRoute", routeNo);
        System.out.println("alarmTimer: doStartService1");

        startService(intent);
        System.out.println("alarmTimer: doStartService2");

    }

    @Override
    protected void onResume() {

        Log.d("activity", "onResume");
        if (myServiceBinder == null) {
            doBindService();
            System.out.println("OnResume");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("activity", "onPause");
        doUnbindService();
        super.onPause();
    }

    public void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void updateClock(Message message) {
        timerTextView.setText((String) message.obj);
    }

    public void updateDistance(Message message) {
        locationTextView.setText((String) message.obj);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                System.out.println("In onRequestPermissionResult case 10");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doStopService();
    }

}