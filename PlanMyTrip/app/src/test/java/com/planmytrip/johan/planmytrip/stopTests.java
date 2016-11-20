package com.planmytrip.johan.planmytrip;

/**
 * Created by Navjashan on 19/11/2016.
 */
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

public class stopTests {

//    public Stop(String stopID, String stopCode, String name, String latitude, String longitude){

        private Stop stopTested = new Stop("51290" , "X122DAS", "CLARK DR" , "121.23422", "213.23423" );

    @Test
    public void testStopID(){
        assertEquals("51290", stopTested.getStopID());
        assertNotEquals("51291", stopTested.getStopID());
    }
    @Test
    public void stopCode(){
        assertEquals("X122DAS", stopTested.getStopCode());
        assertNotEquals("X122DAD", stopTested.getStopCode());
    }
    @Test
    public void testStopName(){
        assertEquals("CLARK DR", stopTested.getName());
        assertNotEquals("WHAT DR", stopTested.getName());
    }
    @Test
    public void testStopLatitude(){
        assertEquals("121.23422", stopTested.getLatitude());
        assertNotEquals("WHAT DR", stopTested.getLatitude());
    }
    @Test
    public void testStopLongitude(){
        assertEquals("213.23423", stopTested.getLongitude());
        assertNotEquals("214.1231", stopTested.getLongitude());
    }
}
