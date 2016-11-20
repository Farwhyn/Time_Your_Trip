package com.planmytrip.johan.planmytrip;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Navjashan on 19/11/2016.
 */

public class busTests {

    private Bus testerBus = new Bus("testedBusRoute", "testedBusNumber", "10:30am", "testedBusDestination");

    @Test
    public void testBusRoute(){
        assertEquals("testedBusRoute", testerBus.getRouteName());
        assertNotEquals("SomeRandomRoute", testerBus.getRouteName());
    }

    @Test
    public void testBusNumber(){
        assertEquals("testedBusNumber", testerBus.getBusNo());
        assertNotEquals("SomeRandomBusNumber", testerBus.getBusNo());
    }

    @Test
    public void testBusEstimatedLeaveTime(){
        assertEquals("10:30am", testerBus.getEstimatedLeaveTime());
        assertNotEquals("10:31am", testerBus.getEstimatedLeaveTime());
    }

    @Test
    public void testBusDestination(){
        assertEquals("testedBusDestination", testerBus.getDestination());
        assertNotEquals("StraightIntoUBC", testerBus.getDestination());
    }

}
