package com.planmytrip.johan.planmytrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AllRoutes extends AppCompatActivity implements SearchView.OnQueryTextListener{

    TextView text_view;
    ListView listView;
    ArrayList<String> allRoutes;
    ArrayList<String> filteredRoutes;
    //DatabaseAccess db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("All Routes");
        setSupportActionBar(toolbar);

        this.listView = (ListView) findViewById(R.id.listView);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        allRoutes = databaseAccess.getAllRoutes();
        databaseAccess.close();
        filteredRoutes = allRoutes;
        listView.setAdapter(new ArrayAdapter(this,  android.R.layout.simple_list_item_1, filteredRoutes));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_allroutes, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchview= (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchview.setOnQueryTextListener(this);
        searchview.setQueryHint("Enter Route");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query){
        //initialize http request based on user's stopcode input
        //submitCode(query);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        System.out.println(newText);
        filteredRoutes = new ArrayList<String>();
        for (String curVal : allRoutes) {
            if (curVal.toLowerCase().contains(newText.toLowerCase())) {
                filteredRoutes.add(curVal);
            }
        }
        listView.setAdapter(new ArrayAdapter(this,  android.R.layout.simple_list_item_1, filteredRoutes));

        return false;
    }
}
