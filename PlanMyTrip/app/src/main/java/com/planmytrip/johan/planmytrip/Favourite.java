package com.planmytrip.johan.planmytrip;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import static com.planmytrip.johan.planmytrip.R.id.loadingPanel;

public class Favourite extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Stop> stops;
    private DatabaseAccess db;
    private TranslinkHandler handler;
    private String stopNo;
    private RelativeLayout loadingPanel; // loading circle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.GONE);
        this.listView = (ListView) findViewById(R.id.listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Favourite Stops");
        setSupportActionBar(toolbar);
        handler = new TranslinkHandler(this);
        db = DatabaseAccess.getInstance(this);
        db.open();
        stops = db.getFromFavourite();
        db.close();
        listView.setAdapter(new NextStopsAdapter(this, stops));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String code = stops.get(position).getStopCode();
                stopNo = code;
                if(code.length() == 5 && isInteger(code)){
                    if (isNetworkAvailable()) {
                        handler.getNextBuses(code); //initialize a get bus http request based on input
                        loadingPanel.setVisibility(View.VISIBLE); //set the loading wheel to visible
                    }
                    else {
                        showError("NO NETWORK AVAILABLE"); //toast that no network is available
                    }
                }
                else{
                    showError("INVALID BUS STOP NUMBER");
                }


            }
        });

    }

    public boolean isInteger(String a){
        int counter = 0;
        for(int i = 0; i < a.length(); i++){
            if(Character.getNumericValue(a.charAt(i)) >= 0 && Character.getNumericValue(a.charAt(i)) <= 9 ){
                counter++;
                continue;
            }
            else
                return  false;
        }
        if(counter == a.length() && Integer.parseInt(a) > 9999 && Integer.parseInt(a) < 100000)
            return true;
        else
            return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void showError(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
    public void nextBusesQueryReturned(ArrayList<Bus> result, String errorMsg){
        loadingPanel.setVisibility(View.GONE); //remove the loading wheel
        if(errorMsg != null){
            showError(errorMsg); //show the error returned by the request
        }
        else {
            // databaseAccess.close();
            Intent intent = new Intent(this, TranslinkUI.class); //pass the intent to TranslinkUI class
            intent.putExtra("busStopNo", stopNo); //store the stop number the user entered
            intent.putExtra("busList", result); //pass the array of buses that the http request returned
            startActivity(intent); //transition to the TranslinkUI activity
        }
    }



}
