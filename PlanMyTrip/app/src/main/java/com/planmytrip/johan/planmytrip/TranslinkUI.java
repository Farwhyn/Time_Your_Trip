package com.planmytrip.johan.planmytrip;

/**
 * Created by Navjashan on 23/10/2016.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Collections;
import android.support.v7.widget.Toolbar;


public class TranslinkUI extends AppCompatActivity {
    private DatabaseAccess db;
    private TextView text_view;
    private ArrayList<Bus> nextBuses;
    private ListView listView;
    private String stopNo;
   // private String TranslinkUITitle = "Buses for bus stop N ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.translinkui_activity_main);


        db = DatabaseAccess.getInstance(this);
        //Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(Color.YELLOW);
        //getSupportActionBar().setTitle(mActivityTitle);

        Intent myIntent = getIntent(); // gets the previously created intent
        stopNo = myIntent.getStringExtra("busStopNo"); // store the stopcode entered by user earlier

        text_view = (TextView) this.findViewById(R.id.textView4);
        text_view.setText("Buses for #" + stopNo);

        nextBuses = (ArrayList<Bus>)myIntent.getSerializableExtra("busList"); //stores the array of buses returned by the translink handler class
        Collections.sort(nextBuses); //sort the array based on arrival time of buses

        listView = (ListView) findViewById(R.id.list_view);

        listView.setAdapter(new NextBusesAdapter(this, nextBuses));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String a = nextBuses.get(position).getDestination();
                String c = a;
                if(a.indexOf("'") != -1) {
                    int i = a.indexOf("'");
                    c = a.substring(0, i) + "'" + a.substring(i, a.length());
                }
                //String b = c.replaceAll("-", "");
                //Toast.makeText(getApplicationContext(), b, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TranslinkUI.this, ConnectDatabase.class);
                intent.putExtra("selectedRoute", nextBuses.get(position).getBusNo());
                intent.putExtra("stopNo", stopNo);
                intent.putExtra("dest", c);
                startActivity(intent);
            }
        });
    }

    public void onClickStar(View view){
        db.open();
        if(!db.checkFavorite(stopNo)) {
            Stop stop = db.getOriginalStop(stopNo);
            db.writeToFavourite(stop);
            Toast.makeText(getBaseContext(), "Stop " + stopNo + " has been added to favorite list",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getBaseContext(), "You've already added this stop", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void routeStopsQueryReturned(String result, String errorMsg){
        if(errorMsg != null){
            text_view.setText(errorMsg);
        }
        else {
            text_view.setText(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
