package com.planmytrip.johan.planmytrip;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by johan on 20.10.2016.
 */

public class TranslinkHandler {

    private Context context;

    //constructor
    public TranslinkHandler(Context activity){
        this.context = activity;
    }

    //function that sends an http request for buses based on stopcode
    public void getNextBuses(String stopNo){
        String url = "http://api.translink.ca/rttiapi/v1/stops/"+ stopNo + "/estimates?apikey=1Y8IBRRxW0yYIhxyWswH";
        myJSONArrayRequest(url, 1);
    }

    public void getNearestStops(String lat, String lon){
        String url = "http://api.translink.ca/rttiapi/v1/stops?apikey=1Y8IBRRxW0yYIhxyWswH&lat="+ lat + "&long=" + lon + "&radius=300";
        myJSONArrayRequest(url, 2);
    }

    public void getStopsForRoute() {
        String url = "http://api.translink.ca/rttiapi/v1/routes/351?apikey=1Y8IBRRxW0yYIhxyWswH";
        myJSONObjectRequest(url, 3);
    }

    public void getCoordinatesForStop(String stopNo) {
        String url = "http://api.translink.ca/rttiapi/v1/stops/" + stopNo +" ?apikey=1Y8IBRRxW0yYIhxyWswH";
        myJSONObjectRequest(url, 5);
    }

    public void getEstimatedTimeFromGoogle(String startLatitude, String startLongitude, String destLatitude, String destLongitude,String departureTime) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ startLatitude + "," + startLongitude +"&destinations=" + destLatitude + "," + destLongitude +"&mode=transit&departure_time="+departureTime+"&key=AIzaSyAIKdSYquNCT6LaIAK1iVzv-CxO9HbPzNg";
        myJSONObjectRequest(url, 4);
    }

    public void getStopInfo(String stopNo) {
        String url = "http://api.translink.ca/rttiapi/v1/stops/" + stopNo + "?apikey=1Y8IBRRxW0yYIhxyWswH";
        myJSONObjectRequest(url, 5);
    }

    public void getNearestBusStopServingRoute(double latitude, double longitude, String routeNo){
        String latitudeString = new DecimalFormat("##.######").format(latitude);
        String longitudeString = new DecimalFormat("##.######").format(longitude);
        String url = "http://api.translink.ca/rttiapi/v1/stops?apikey=1Y8IBRRxW0yYIhxyWswH&lat="+ latitudeString + "&long="+longitudeString+"&routeNo="+routeNo+"&radius=1999";
        myJSONArrayRequest(url, 6);
    }

    private void getNearestStopsReturned(JSONArray response, String errorMsg){
        //System.out.print(response.toString());
        if (errorMsg == null) {
            ArrayList<Stop> nearestStops = new ArrayList<Stop>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonobject;
                try {
                    jsonobject = response.getJSONObject(i);
                    String stopNo = jsonobject.getString("StopNo");
                    String stopName = jsonobject.getString("Name");
                    String latitude = jsonobject.getString("Latitude");
                    String longitude = jsonobject.getString("Longitude");
                    Stop stop = new Stop(null, stopNo, stopName, latitude, longitude);
                    nearestStops.add(stop);

                } catch (JSONException e) {
                    System.out.print("Error parsing JSONArray" + e.toString());
                }
            }

            System.out.print(nearestStops.toString());
            ((MainActivity)context).nearestStopsQueryReturned(nearestStops, null);



        }
        else{
            ((MainActivity)context).nearestStopsQueryReturned(null, errorMsg);

        }


    }

    private void getNextBusesReturned(JSONArray response, String errorMsg){

        if (errorMsg == null) {
            ArrayList<Bus> nextBuses = new ArrayList<Bus>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonobject;
                try {
                    jsonobject = response.getJSONObject(i);
                    String routeNo = jsonobject.getString("RouteNo");
                    String routeName = jsonobject.getString("RouteName");
                    JSONArray busArray = jsonobject.getJSONArray("Schedules");
                    for (int j = 0; j <1; j++) {
                        JSONObject jsonobjectBusInfo;
                        try {
                            jsonobjectBusInfo = busArray.getJSONObject(j);
                            String destination = jsonobjectBusInfo.getString("Destination");
                            String expectedLeaveTime = jsonobjectBusInfo.getString("ExpectedLeaveTime");
                            Bus bus = new Bus(routeName,routeNo,expectedLeaveTime,destination);
                            nextBuses.add(bus);

                        } catch (JSONException e) {
                            System.out.print("Error parsing JSONArray" + e.toString());
                        }

                    }


                } catch (JSONException e) {
                    System.out.print("Error parsing JSONArray" + e.toString());
                }
            }

            System.out.print(nextBuses.toString());
            if(this.context instanceof MainActivity)
                ((MainActivity)context).nextBusesQueryReturned(nextBuses, null);
            else
                ((Favourite)context).nextBusesQueryReturned(nextBuses, null);



        }
        else{
            //got error 302, 404,
            //302 connected to network but no internet
            ((MainActivity)context).nextBusesQueryReturned(null, "A server error occured. Please check if the stop number is correct. (" + errorMsg+ ")");
        }

    }

    private void getEstimatedTimeFromGoogleReturned(JSONObject response, String errorMsg){

        if (errorMsg == null) {
            String durationInSeconds = "";
            try {
                JSONArray rows = response.getJSONArray("rows");
                JSONObject row0 = rows.getJSONObject(0);
                JSONArray elements = row0.getJSONArray("elements");
                JSONObject element0 = elements.getJSONObject(0);
                JSONObject duration = element0.getJSONObject("duration");
                durationInSeconds = duration.getString("value");


            } catch (JSONException e) {
                System.out.print("Error parsing JSONArray" + e.toString());
            }


            System.out.println("hallo hallo hallo hallo " +durationInSeconds + "!!!!!!");

            ((TimerService)context).estimatedTimeReturned(durationInSeconds, null);



        }
        else{
            ((TimerService)context).estimatedTimeReturned(null, errorMsg);

        }


    }

    private void getStopsForRouteReturned(JSONObject response, String errorMsg){
        System.out.printf(response.toString());
        ((TranslinkUI)context).routeStopsQueryReturned(response.toString(), null);

    }

    private void getCoordinatesForStopReturned(JSONObject response, String errorMsg){
        Stop stop = null;
        if(errorMsg == null) {
            try {
                String name = response.getString("Name");
                String stopCode = response.getString("StopNo");
                String latitude = response.getString("Latitude");
                String longitude = response.getString("Longitude");
                stop = new Stop(null, stopCode, name, latitude, longitude);
                //call someone who needs this information
            }
            catch(JSONException e){
                System.out.print("Error parsing JSONArray" + e.toString());
            }
            ((MainActivity)context).getStopInfo(stop, null);
        }
        else{
            System.out.println("TranslinkHandler: error getCoordinatesForStopReturned");
            ((MainActivity)context).getStopInfo(null, errorMsg);
        }
    }

    private void getNearestBusStopServingRouteReturned(JSONArray response, String errorMsg){
        if (errorMsg == null) {
            String latitude = "";
            String longitude = "";
            try {
                System.out.println(response.toString());
                JSONObject firstStop = response.getJSONObject(0);
                latitude = firstStop.getString("Latitude");
                longitude = firstStop.getString("Longitude");
            }
            catch(JSONException e){
                System.out.print("Error parsing JSONArray" + e.toString());
            }

            ((TimerService)context).getNearestBusStopServingRouteReturned(latitude, longitude, null);
        }
        else{
            System.out.println("TranslinkHandler: error getNearestBusStopsReturned");
            ((TimerService)context).getNearestBusStopServingRouteReturned(null, null, errorMsg);
        }
    }

    private void translinkRequestResponded(int inputID, JSONArray jsonArray, JSONObject jsonObject, String errorMsg){

        switch(inputID){
            case 1:
                getNextBusesReturned(jsonArray, errorMsg);
                break;
            case 2:
                getNearestStopsReturned(jsonArray,errorMsg);
                break;
            case 3:
                getStopsForRouteReturned(jsonObject,errorMsg);
                break;
            case 4:
                getEstimatedTimeFromGoogleReturned(jsonObject,errorMsg);
                break;
            case 5:
                getCoordinatesForStopReturned(jsonObject,errorMsg);
                break;
            case 6:
                getNearestBusStopServingRouteReturned(jsonArray,errorMsg);
                break;
            default: break;
        }

    }

    private void myJSONObjectRequest(String url, final int inputID){

        RequestQueue queue = Volley.newRequestQueue(context);

        Response.Listener<JSONObject> myResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                translinkRequestResponded(inputID,null,response,null);
            }
        };

        Response.ErrorListener myErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                translinkRequestResponded(inputID,null,null,error.toString());
            }
        };


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,myResponseListener,myErrorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/JSON");
                return params;
            }

        };

        queue.add(jsonRequest);


    }

    private void myJSONArrayRequest(String url, final int inputID){

        RequestQueue queue = Volley.newRequestQueue(context);

        Response.Listener<JSONArray> myResponseListener = new Response.Listener<JSONArray>()
        {

            @Override
            public void onResponse(JSONArray response) {
                translinkRequestResponded(inputID,response,null,null);


            }

        };
        Response.ErrorListener myErrorListener = new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse != null) {
                    translinkRequestResponded(inputID, null, null, String.valueOf(error.networkResponse.statusCode));
                }
                else{
                    translinkRequestResponded(inputID, null, null, "No response");
                }
            }

        };



        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,myResponseListener,myErrorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/JSON");
                return params;
            }

        };

        queue.add(jsonRequest);


    }

    private void myStringRequest(String url, final int inputID){

        RequestQueue queue = Volley.newRequestQueue(context);

        Response.Listener<String> myResponseListener = new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {
                //translinkRequestResponded(inputID,new JSONObject(response),null,null);
                System.out.println(response);

            }

        };
        Response.ErrorListener myErrorListener = new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error) {
                translinkRequestResponded(inputID,null,null,String.valueOf(error.networkResponse.statusCode));
            }

        };



        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url,myResponseListener,myErrorListener)
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/JSON");
                return params;
            }

        };
        queue.add(jsonRequest);
    }

}
