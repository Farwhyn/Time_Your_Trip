package com.planmytrip.johan.planmytrip;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Navjashan on 22/11/2016.
 */

public class offlineServiceTests {

    private OfflineService offlineService = new OfflineService();
    private String distance;
    double valueGreaterThenThousand = 1123.12;
    double valueLessThenThousand = 999.0;

    @Test
    public void testConvertDistanceGreaterthen1000(){
        distance = offlineService.convertDistance(valueGreaterThenThousand);
        assertEquals(distance, "1.1 Kilometers");
        assertNotEquals(distance, "1.12312 Kilometers");
       }

    @Test
    public void testConvertDistanceLessThen1000(){
        distance = offlineService.convertDistance(valueLessThenThousand);
        assertEquals(distance, "999 Meters");
        assertNotEquals(distance, "0.9 Kilometers");

    }


}
