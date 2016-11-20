package com.planmytrip.johan.planmytrip;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Navjashan on 10/11/2016.
 */

public class MainActivityTest {
    private MainActivity tester = new MainActivity();

    @Test
    public void integerTest_OnInvalidStopNumbers(){

        //InValid Bus Stop Numbers tester
        assertFalse(tester.isInteger("2342"));
        assertFalse(tester.isInteger("a12sasd242342"));
        assertFalse(tester.isInteger("134234"));
        assertFalse(tester.isInteger("-234"));
        assertFalse(tester.isInteger("0"));
        assertFalse(tester.isInteger("PleaseFail"));
    }

    @Test
    public void integerTest_OnValidStopNumbers(){

        //Valid Bus Stop Numbers tester
        assertTrue(tester.isInteger("52365"));
        assertTrue(tester.isInteger("56919"));
        assertTrue(tester.isInteger("50268"));
        assertTrue(tester.isInteger("51930"));
        assertTrue(tester.isInteger("50347"));


    }

}
