package com.planmytrip.johan.planmytrip;

/**
 * Created by james on 10/11/2016.
 */

import static android.support.test.InstrumentationRegistry.getTargetContext;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class DataBaseTest {
    private DatabaseAccess access;
    private SQLiteDatabase database;

    @Before
    public void setUp(){

         access = DatabaseAccess.getInstance(InstrumentationRegistry.getTargetContext());
         access.open();
    }

    @After
    public void finish() {
       access.close();
    }

    @Test
    public void testGetOriginalStop() throws Exception {
        String stopcode = "50913";
        Stop stop = new Stop("11606", stopcode, "WB E BROADWAY NS COMMERCIAL BAY 2", "49.262416", "-123.06875");
        Stop stopa = access.getOriginalStop(stopcode);

        assertEquals(stop.getStopID(), stopa.getStopID());
        assertEquals(stop.getName(), stopa.getName());

        stopcode = "52247";
        stop = new Stop("2269", stopcode, "NB 6 ST NS 12 AVE", "49.220388", "-122.92926");
        stopa = access.getOriginalStop(stopcode);
        assertEquals(stop.getStopID(), stopa.getStopID());
        assertEquals(stop.getName(), stopa.getName());

        stopcode = "00000";
        stop = null;
        stopa = access.getOriginalStop(stopcode);
        assertEquals(stop, stopa);
        //assertEquals(stop.getName(), stopa.getName());
    }

}