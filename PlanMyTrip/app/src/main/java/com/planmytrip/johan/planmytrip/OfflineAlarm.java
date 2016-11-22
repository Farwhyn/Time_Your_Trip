package com.planmytrip.johan.planmytrip;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OfflineAlarm extends AppCompatActivity {

    TextView locationTextView;
    Button stopAlarm;
    boolean alarmEnabled = true;
    Intent intentFromLastActivity;
    Toolbar toolbar;

    private OfflineService myServiceBinder;
    public ServiceConnection myConnection = new ServiceConnection() {


        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((OfflineService.MyBinder) binder).getService();
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_alarm);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Your Alarm Is Set");
        setSupportActionBar(toolbar);

        locationTextView = (TextView) findViewById(R.id.locationTextView);
        Intent intent = getIntent();

        if (!(intent.hasExtra("UserClickedOnPermanentNotification"))) {

            intentFromLastActivity = intent;
            doBindService();
            doStartService(intentFromLastActivity);
            locationTextView.setText("Waiting for GPS-Signal...");
        }
        else{

            Intent intent1 = new Intent(OfflineAlarm.this, OfflineAlarm.class);
            intent1.putExtra("destination", intent.getSerializableExtra("destination"));
            intentFromLastActivity = intent1;
            doBindService();
            locationTextView.setText(intent.getStringExtra("lastMessage"));

        }


        //Alarm configurations for setting and stopping it
        stopAlarm = (Button) this.findViewById(R.id.button3);
        stopAlarm.setText("Stop Alarm");
        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmEnabled) {
                    stopAlarm.setText("Set Alarm");
                    alarmEnabled = false;
                    toolbar.setTitle("Please Set Your Alarm");
                    doStopService();

                } else {
                    stopAlarm.setText("Stop Alarm");
                    alarmEnabled = true;
                    doBindService();
                    toolbar.setTitle("Your Alarm Is Set");
                    doStartService(intentFromLastActivity);
                    locationTextView.setText("Waiting for GPS-Signal...");
                }
            }
        });

        LocationManager locationManagerContext = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GPSchecker locationManager = new GPSchecker(locationManagerContext);
        if (!locationManager.isLocationEnabled()) {
            showAlert();
        }

    }


    public Handler myHandler1 = new Handler() {
        public void handleMessage(Message message) {
            switch (message.arg1) {
                case 1:
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
        this.locationTextView.setText("Done!");
        stopAlarm.setText("Set Alarm");
        alarmEnabled = false;
        toolbar.setTitle("Please Set Your Alarm");

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
        stopService(new Intent(this, OfflineService.class));

    }

    public void doBindService() {
        Intent intent = new Intent(this, OfflineService.class);
        Messenger messenger = new Messenger(myHandler1);
        intent.putExtra("MESSENGER", messenger);
        bindService(intent, myConnection, Context.BIND_IMPORTANT);
        System.out.println("OfflineAlarm: doBindService");
    }

    public void doStartService(Intent timeIntent) {

        Intent intent = new Intent(this, OfflineService.class);
        Stop destination = (Stop) timeIntent.getSerializableExtra("destination");
        intent.putExtra("destination", destination);
        System.out.println("OfflineAlarm: doStartService1");

        startService(intent);
        System.out.println("OfflineAlarm: doStartService2");

    }

    public void updateDistance(Message message) {
        locationTextView.setText((String) message.obj);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doStopService();
    }


}
