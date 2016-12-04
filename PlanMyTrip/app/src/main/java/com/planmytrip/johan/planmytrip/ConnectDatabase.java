package com.planmytrip.johan.planmytrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ConnectDatabase extends AppCompatActivity{
    private ListView listView;
    private ArrayList<Stop> stops;
    private Stop origStop;
    private String selRoute;
    private TextView text_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);


        //Intent myIntent = getIntent();
        //Bus bus = (Bus)myIntent.getSerializableExtra("selectedBus");
        Intent myIntent = getIntent(); // gets the previously created intent
        selRoute = myIntent.getStringExtra("selectedRoute"); // will return "FirstKeyValue"
        String input = myIntent.getStringExtra("stopNo");
        String destination = myIntent.getStringExtra("dest");

        text_view = (TextView) this.findViewById(R.id.textView5);
        text_view.setText("Upcoming stops for bus # " + selRoute);

        this.listView = (ListView) findViewById(R.id.listView);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        stops = databaseAccess.getStops(selRoute, destination);
        origStop = databaseAccess.getOriginalStop(input);
        databaseAccess.close();
        listView.setAdapter(new NextStopsAdapter(this, stops));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Intent intent = new Intent(ConnectDatabase.this, ConnectDatabase.class);
               // intent.putExtra("selectedRoute",nextBuses.get(position).getBusNo());
                //startActivity(intent);)


                if(origStop.getStopCode().equals(stops.get(position).getStopCode())) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "This is your current location!", duration);
                    toast.show();
                }




                else {

                    Intent intent = new Intent(ConnectDatabase.this, alarmTimer.class);
                    intent.putExtra("startingStop", origStop);
                    intent.putExtra("destination", stops.get(position));
                    intent.putExtra("selRoute",selRoute);
                    startActivity(intent);
                }


            }
        });

    }






}
