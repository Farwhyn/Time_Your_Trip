package com.planmytrip.johan.planmytrip;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.Menu;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import android.view.Window;
import android.graphics.Color;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    private String stopNumber; //stores the stop number entered by user
    private RelativeLayout loadingPanel; // loading circle
    private TranslinkHandler transHandler;
    //below is for sliding menu
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle = "Time Your Trip";
    private String[] menuArray = { "Bus Stops Around Me", "Online Mode",  "View My Location", "Show Bus Routes", "Show Skytrain",
            "My Favourite List", "Rate the app", "Feedback"};
    private Integer[] iconArray = {
            R.mipmap.list,
            R.mipmap.setting,
            R.mipmap.mylocation,
            R.mipmap.bus,
            R.mipmap.skytrain,
            R.mipmap.favorite,
            R.mipmap.rate,
            R.mipmap.feedback
    };

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private DatabaseAccess databaseAccess;
    private ArrayList<Marker> markers;

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 1;


    private GPSchecker locationManager;
    private LocationManager locationManagerContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManagerContext = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                            if(locationManager == null) {
                            locationManager = new GPSchecker(locationManagerContext);
                      }

        /*
        if(!locationManager.isLocationEnabled()){
            showAlert();
        }
*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //databaseAccess = DatabaseAccess.getInstance(this);
       // databaseAccess.open();
        markers = new ArrayList<Marker>();
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFDA24"));
        getSupportActionBar().setTitle(mActivityTitle);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.GONE);
        transHandler = new TranslinkHandler(this); //initialize new translink handler class
        //below is for sliding menu
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();
//for the burger view on the left top corner
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    private void addDrawerItems() {
        mAdapter = new menuAdapter(this, menuArray, iconArray);
        mDrawerList.setAdapter(mAdapter);
//click an item
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String menuItem = String.valueOf(parent.getItemAtPosition(position));
                Intent intent;
                switch(menuItem) {
                    case "My Favourite List":
                        intent = new Intent(MainActivity.this, Favourite.class);
                        startActivity(intent);
                        break;
                    case "Show Bus Routes":
                        intent = new Intent(MainActivity.this, AllRoutes.class);
                        //intent.putExtra("selectedRoute",nextBuses.get(position).getBusNo());
                        startActivity(intent);
                        break;
                    case "View My Location":
                        if (mLastLocation != null) {
                            //Toast.makeText(getApplicationContext(), mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                            initCamera(mLastLocation);
                        }
                        break;
                    default:
                        Toast.makeText(MainActivity.this, menuItem, Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------//
    //create the searchView layout
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchview= (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchview.setOnQueryTextListener(this);
        searchview.setQueryHint("Enter Stopcode...");
        //searchview.setBackgroundColor(Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * *SearchView
     * @param query
     * @return
     */
  @Override
    public boolean onQueryTextSubmit(String query){
      //initialize http request based on user's stopcode input
     //submitCode(query);
      findStopCode(query);

      return false;
  }

   @Override
   public boolean onQueryTextChange(String newText) {

       return false;
   }

    //function that checks if the Wifi is available on the phone
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

    //helper function that initializes an http request for buses based on user input
    public void submitCode(String code) {
        if(code.length() == 5 && isInteger(code)){
            if (isNetworkAvailable()) {
                stopNumber = code; //store the user's input in the global variable
                transHandler.getNextBuses(code); //initialize a get bus http request based on input
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

    //helper function that initializes an http request for buses based on user input
    public void findStopCode(String code) {
        if(code.length() == 5 && isInteger(code)){
            if (isNetworkAvailable()) {
                stopNumber = code; //store the user's input in the global variable
                transHandler.getStopInfo(code); //initialize a get bus http request based on input
                loadingPanel.setVisibility(View.VISIBLE); //set the loading wheel to visible
            }
            else {
                showError("NO NETWORK AVAILABLE"); //toast that no network is available
                //loadingPanel.setVisibility(View.VISIBLE);
                //offLineStops(code);
            }
        }
        else{
            showError("INVALID BUS STOP NUMBER");
        }

    }

    public void showError(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    //function that is called by the TranslinkHandler class when an http request is successfully returned
    public void nextBusesQueryReturned(ArrayList<Bus> result, String errorMsg){
        loadingPanel.setVisibility(View.GONE); //remove the loading wheel
        if(errorMsg != null){
            showError(errorMsg); //show the error returned by the request
        }
        else {
           // databaseAccess.close();
            Intent intent = new Intent(this, TranslinkUI.class); //pass the intent to TranslinkUI class
            intent.putExtra("busStopNo", stopNumber); //store the stop number the user entered
            intent.putExtra("busList", result); //pass the array of buses that the http request returned
            startActivity(intent); //transition to the TranslinkUI activity
        }
    }

    public void getStopInfo(Stop stop, String errorMsg) {
        loadingPanel.setVisibility(View.GONE); //remove the loading wheel
        if(errorMsg != null){
            showError(errorMsg); //show the error returned by the request
        }

        else {
            LatLng coor = new LatLng(Double.parseDouble(stop.getLatitude()), Double.parseDouble(stop.getLongitude()));
            String address = getAddressFromLatLng(coor);

            int i = findMarker(stop.getStopCode());
            if(i > 0) {
                markers.get(i).showInfoWindow();

            }
            else {
                setSingleMarker(stop);
                int j = findMarker(stop.getStopCode());
                markers.get(j).showInfoWindow();

            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(coor)      // Sets the center of the map to Mountain View
                    .zoom( 18f )
                    .bearing( 0.0f )
                    .tilt( 0.0f )                // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
        }
    }

        //------------------------------MAP STUFF (JAMES) ---------------------------------------------//
        @Override
        public void onConnected(Bundle bundle) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                //Toast.makeText(getApplicationContext(), mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                initCamera(mLastLocation);
                if(mLastLocation != null) {
                    DecimalFormat df = new DecimalFormat("#.####");
                    df.setRoundingMode(RoundingMode.CEILING);
                    String la = df.format(mLastLocation.getLatitude());
                    String lo = df.format(mLastLocation.getLongitude());

                    submitArea(la, lo);
                    String address = getAddressFromLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                }
            } else {
                //Toast.makeText(getApplicationContext(), "Please turn on Location Service", Toast.LENGTH_SHORT).show();
                if(!locationManager.isLocationEnabled()){
                    showAlert();
                }
            }


        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            submitCode(marker.getTitle());
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            return true;
        }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( 49.2827, 123.1207), 10));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "Cannot do this shit", Toast.LENGTH_SHORT).show();
        }
        initListeners();
    }



    public void submitArea(String lat, String lon) {
        if (isNetworkAvailable()) {
            transHandler.getNearestStops(lat, lon);

        } else {
            Toast.makeText(getApplicationContext(),"NO NETWORK AVAILABLE",Toast.LENGTH_SHORT).show(); //toast that no network is available
        }
    }

    public void nearestStopsQueryReturned(ArrayList<Stop> result, String errorMsg){
        if(errorMsg != null){
            Toast.makeText(getApplicationContext(), "Cannot obtain current location", Toast.LENGTH_SHORT).show();
        }
        else {
            setMarkers(result);
        }
    }

    private void setMarkers(ArrayList<Stop> result) {
        //Toast.makeText(getApplicationContext(), "blah", Toast.LENGTH_SHORT).show();
        for(int i = 0; i < result.size(); i++) {
            setSingleMarker(result.get(i));
        }

    }

    private void setSingleMarker(Stop stop) {
        double a, b;
        a = Double.parseDouble(stop.getLatitude());
        b = Double.parseDouble(stop.getLongitude());
        MarkerOptions options = new MarkerOptions().position(new LatLng(a, b));
        options.title(stop.getStopCode()).snippet(stop.getName());

        options.icon(BitmapDescriptorFactory.defaultMarker());
        markers.add(mMap.addMarker(options));

    }

    private int findMarker(String title) {
        for(int i = 0; i < markers.size(); i ++)
        {
            if(markers.get(i).getTitle().equals(title)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onStart() {

        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initCamera( Location location ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( location.getLatitude(),
                        location.getLongitude() ) )
                .zoom( 16 )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        mMap.animateCamera( CameraUpdateFactory.newCameraPosition( position ), null );
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( location.getLatitude(),location.getLongitude() ), 15));
        mMap.setMapType( MAP_TYPES[curMapTypeIndex] );
        mMap.setTrafficEnabled( true );
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setZoomControlsEnabled( true );

    }
    private void initListeners() {
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener( this );
    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder(this);

        String address = "";
        try {
            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }
/*
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
*/

}
