package com.planmytrip.johan.planmytrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import android.widget.TextView;

/**
 * Created by james on 01/11/2016.
 */

public class OfflineStops extends AppCompatActivity{
    private ListView listView;
    private ArrayList<Stop> stops;
    private Stop origStop;
    private String selRoute;
    private TextView text_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_stops);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Select Destination");
        setSupportActionBar(toolbar);

        //Intent myIntent = getIntent();
        //Bus bus = (Bus)myIntent.getSerializableExtra("selectedBus");
        Intent myIntent = getIntent(); // gets the previously created intent
        selRoute = myIntent.getStringExtra("selectedRoute"); // will return "FirstKeyValue"

        this.listView = (ListView) findViewById(R.id.listView);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        stops = databaseAccess.getAllStopsAlongRoute(selRoute);
        databaseAccess.close();
        listView.setAdapter(new NextStopsAdapter(this, stops));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Intent intent = new Intent(ConnectDatabase.this, ConnectDatabase.class);
                // intent.putExtra("selectedRoute",nextBuses.get(position).getBusNo());
                //startActivity(intent);)

                    Intent intent = new Intent(OfflineStops.this, OfflineAlarm.class);
                    intent.putExtra("destination", stops.get(position));
                    startActivity(intent);

            }
        });

    }






}
