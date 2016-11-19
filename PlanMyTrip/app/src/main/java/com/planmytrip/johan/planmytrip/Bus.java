package com.planmytrip.johan.planmytrip;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by johan on 23.10.2016.
 */

public class Bus implements Serializable, Comparable<Bus>
{
    private String routeName;
    private String busNo;
    private String estimatedLeaveTime;
    private String destination;

    public Bus(String routeName,String busNo,String estimatedLeaveTime, String destination){
        this.routeName = routeName;
        this.busNo = busNo;
        this.estimatedLeaveTime = estimatedLeaveTime;
        this.destination = destination;

    }

    public String getRouteName() {
        return routeName;
    }

    public String getBusNo() {
        return busNo;
    }

    public String getDestination() {
        return destination;
    }

    public String getJustTime(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mma yyyy-MM-dd");
            Date date = sdf.parse(getEstimatedLeaveTime());
            SimpleDateFormat sdfOut = new SimpleDateFormat("hh:mma");
            return sdfOut.format(date);

        }
        catch(ParseException e){

            try{
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mma");
                Date date = sdf.parse(getEstimatedLeaveTime());
                return sdf.format(date);

            }
            catch(ParseException e1){
                System.out.println("Parse exception date!!!!");
                return getEstimatedLeaveTime();
            }
        }

    }


    public String getEstimatedLeaveTime() {

        return estimatedLeaveTime;
    }

    @Override
    public String toString() {
        return  busNo + " " + destination + "          " + estimatedLeaveTime ;
    }

    @Override
    public int compareTo(Bus bus) {


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mma yyyy-MM-dd");
            Date date1 = sdf.parse(bus.getEstimatedLeaveTime());
            Date date2 = sdf.parse(getEstimatedLeaveTime());
            if (date1.compareTo(date2) <= 0) {
                return 1;
            }
            else{
                return -1;
            }
        }
        catch(ParseException e){

            try{
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mma");
                Date date1 = sdf.parse(bus.getEstimatedLeaveTime());
                Date date2 = sdf.parse(getEstimatedLeaveTime());
                if (date1.compareTo(date2) <= 0) {
                    return 1;
                }
                else{
                    return -1;
                }

            }
            catch(ParseException e1){
                System.out.println("Parse exception date!!!!");
                return 1;
            }
        }

    }

}
