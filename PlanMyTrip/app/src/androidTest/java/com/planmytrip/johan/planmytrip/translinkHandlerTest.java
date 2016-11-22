package com.planmytrip.johan.planmytrip;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Created by Navjashan on 21/11/2016.
 */

@RunWith(AndroidJUnit4.class)
public class translinkHandlerTest {

    Context mContext;
    private TranslinkHandler handlerTester;
    private final String latitude = "49.1231";
    private final String longitude = "-122.232432";
    private final String destLatitude = "59.1231";
    private final String destLongitude = "-123.232432";
    private final String stopNo = "50270";

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        handlerTester = new TranslinkHandler(mContext);



    }

    @Test
    public void testGetBuses(){
        handlerTester.getNearestStops(latitude, longitude);
        String stopQuery = "http://api.translink.ca/rttiapi/v1/stops?apikey=1Y8IBRRxW0yYIhxyWswH&lat="+latitude+ "&long=" +longitude+ "&radius=300";
        String invalidQuery = "http://api.translink.ca/rttiapi/v1/stops?apikey=1Y8IBRRxW0yYIhxyWswH&lat="+longitude+ "&long=" +latitude+ "&radius=300";
        assertEquals(stopQuery, handlerTester.queryTesting());
        assertNotEquals(invalidQuery, handlerTester.queryTesting());
         }

    @Test
    public void testStopNo(){
        handlerTester.getNextBuses(stopNo);
        String busQuery = "http://api.translink.ca/rttiapi/v1/stops/"+ stopNo + "/estimates?apikey=1Y8IBRRxW0yYIhxyWswH";
        assertEquals(busQuery, handlerTester.queryTesting());
    }

    @Test
    public void testStopsforRoutes(){
        handlerTester.getStopsForRoute();
        String expectedStopQuery = "http://api.translink.ca/rttiapi/v1/routes/351?apikey=1Y8IBRRxW0yYIhxyWswH";
        assertEquals(expectedStopQuery, handlerTester.queryTesting());
    }

    @Test
    public void testCoordinatesForStops(){
        handlerTester.getCoordinatesForStop(stopNo);
        String expectedStopQuery = "http://api.translink.ca/rttiapi/v1/stops/" + stopNo +" ?apikey=1Y8IBRRxW0yYIhxyWswH";
        assertEquals(expectedStopQuery, handlerTester.queryTesting());
    }

    @Test
    public void testGoogleQuery(){
        handlerTester.getEstimatedTimeFromGoogle(latitude, longitude, destLatitude, destLongitude, "whenever");
        String expectedGoogleQuery = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ latitude + "," + longitude +"&destinations=" + destLatitude + "," + destLongitude +"&mode=transit&departure_time="+"whenever"+"&key=AIzaSyAIKdSYquNCT6LaIAK1iVzv-CxO9HbPzNg";
        assertEquals(expectedGoogleQuery, handlerTester.queryTesting());
    }

    @Test
    public void testStopInfo(){
        handlerTester.getStopInfo(stopNo);
        String expectedStopQuery = "http://api.translink.ca/rttiapi/v1/stops/"+stopNo+"?apikey=1Y8IBRRxW0yYIhxyWswH";
        assertEquals(expectedStopQuery, handlerTester.queryTesting());
    }

    @Test
    public void testTranslinkRequestResponse(){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        JSONArray busArray = new JSONArray();
        JSONObject busObject = new JSONObject();

        try{
            busObject.put("Destination","AnyDestination");
            busObject.put("ExpectedLeaveTime", "10:30am");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        busArray.put(busObject);

        try {
            jsonObject.put("RouteNo", "AnyRoute");
            jsonObject.put("RouteName", "AnyName");
            jsonObject.put("Schedules", busArray );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jsonObject);

        handlerTester.translinkRequestResponded(7, jsonArray, null, null);
        assertEquals(handlerTester.queryTesting(), "JSON TESTS PASSED");
        assertNotEquals(handlerTester.queryTesting(), "JSON TESTS FAILED");

    }

}
