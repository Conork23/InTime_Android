package com.intimealarm.conor.intime_app.utilities;

import android.text.TextUtils;

import com.intimealarm.conor.intime_app.models.Alarm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 23/01/2017.
 */

public class Helper {

    // Helper Constructor
    public Helper() {
    }

    // Get Calender object from Time String
    public Calendar getTimeFromString(String time){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getHourFromTime(time));
        c.set(Calendar.MINUTE, getMinFromTime(time));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c;
    }

    // Get Hour from Time String
    public int getHourFromTime(String t){
        return Integer.parseInt(t.substring(0,2));
    }

    // Get Minute From Time String
    public int getMinFromTime(String t){
        return Integer.parseInt(t.substring(3));
    }

    // Integer Array to String
    public String arrToString(int[] arr){
        String s = "";
        for (int i = 0;i<arr.length; i++) {
            s = s+arr[i];
            if(i<arr.length-1){
                s = s+",";
            }
        }
        return s;
    }

    // String Array to String
    public String arrToString(String[] arr){
        String s = "";
        for (int i = 0;i<arr.length; i++) {
            s = s+arr[i];
            if(i<arr.length-1){
                s = s+",";
            }
        }
        return s;
    }

    // String to Integer Array
    public int[] stringToArr(String s){
        String[] arr_string = s.split(",");
        int[] arr = new int[7];

        for(int i = 0; i<arr_string.length; i++){
            arr[i]= Integer.parseInt(arr_string[i]);
        }

        return arr;
    }

    // String to String Array
    public String[] stringToStringArr(String s){
        String[] arr_string = s.split(",");
        String[] arr = new String[3];

        for(int i = 0; i<arr_string.length; i++){
            arr[i]= arr_string[i];
        }

        return arr;
    }

    // Primitive Int Array to ArrayList
    public ArrayList<Integer> getActiveDays(int[] days) {

        ArrayList<Integer> activeDays = new ArrayList<>();
        for (int i = 0; i < days.length ; i++) {
            int day =(i != 6)? i + 2 : 1;
            if(days[i]==1){
                if (day == 1){
                    activeDays.add(0,day);
                }else {
                activeDays.add(day);
                }
            }
        }

        return activeDays;
    }

    // ArrayList to String
    public String intArrayListToString(ArrayList<Integer> a){
        String s = "";
        for (int i: a) {
            s = (s.equals(""))? s+i : s+", "+i;
        }
        return s;
    }

    // Calender to Time String
    public String calToShortString(Calendar c){
        int hour, min;
        hour = c.get(c.HOUR_OF_DAY);
        min = c.get(c.MINUTE);


        return  addLeadingZero(hour) + ":" + addLeadingZero(min);
    }

    // Time Printout to Calender
    public Calendar timePrintOutToCal(String s){
        Calendar cal = Calendar.getInstance();

        // 2 0 1 7 - 0 1 - 0 1    1  0  :  1  0  :  1  0
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18

        int year,month,day,hour,min,sec;
        year = Integer.parseInt(s.substring(0,4));
        month = Integer.parseInt(s.substring(5,7)) - 1;
        day = Integer.parseInt(s.substring(8,10));
        hour = Integer.parseInt(s.substring(11,13));
        min = Integer.parseInt(s.substring(14,16));
        sec = Integer.parseInt(s.substring(17));

        cal.set(cal.DATE, day);
        cal.set(cal.MONTH, month);
        cal.set(cal.HOUR_OF_DAY, hour);
        cal.set(cal.MINUTE, min);
        cal.set(cal.SECOND, sec);
        cal.set(cal.YEAR, year );
        cal.set(cal.MILLISECOND, 0 );

        return cal;

    }

    // Calendar to Time Printout
    public String timePrintOut(Calendar c){
        int day, month, year, hour, min, sec;
        day = c.get(c.DATE);
        month = c.get(c.MONTH)+1;
        hour = c.get(c.HOUR_OF_DAY);
        min = c.get(c.MINUTE);
        sec = c.get(c.SECOND)+10;
        year = c.get(c.YEAR);

        return  year+ "-" + addLeadingZero(month)+"-"+ addLeadingZero(day)
                +" "+
                addLeadingZero(hour) + ":" + addLeadingZero(min)+ ":" + addLeadingZero(sec);
    }

    // Add leading zero to Integers < 10
    public String addLeadingZero(int i){
        return (i < 10)? "0"+i : i+"";
    }

    // Alarm To Time Printout
    public String timePrintOut(Alarm a) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getHourFromTime(a.getTime()));
        c.set(Calendar.MINUTE, getMinFromTime(a.getTime()));
        return timePrintOut(c);
    }

    // Alarm to Time Printout of Departure Time
    public String departureTimePrintOut(Alarm a) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getHourFromTime(a.getTime()));
        c.set(Calendar.MINUTE, (getMinFromTime(a.getTime()) + a.getPreptime()));
        return timePrintOut(c);
    }

    // Check if feilds contain text
    public boolean checkFields(String... fields){
        boolean isValid = true;

        for (String x : fields) {
            if (TextUtils.isEmpty(x)) {
                isValid = false;
            }
        }

        return isValid;
    }

}
