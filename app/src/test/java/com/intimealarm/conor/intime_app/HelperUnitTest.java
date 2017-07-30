package com.intimealarm.conor.intime_app;

import android.text.TextUtils;

import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.utilities.Helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class HelperUnitTest {
    Helper help;

    @Before
    public void before(){
        help = new Helper();
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CharSequence s = (CharSequence) invocation.getArguments()[0];
                boolean isEmpty = !(s != null && s.length() > 0);
                return isEmpty;
            }
        });


    }

    @Test
    public void getCalendarFromString() {
        String time = "10:05";

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 05);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        assertEquals(c, help.getTimeFromString(time));
    }

    @Test
    public void getHourFromTimeString(){
        String time = "10:05";
        assertEquals(10, help.getHourFromTime(time));
    }

    @Test
    public void getMinFromTimeString(){
        String time = "10:05";
        assertEquals(5, help.getMinFromTime(time));
    }

    @Test
    public void arrToString(){
        String string = "0,0,1,1,0,1,0";
        int[] arr = {0,0,1,1,0,1,0};
        assertEquals(string, help.arrToString(arr));

    }

    @Test
    public void stringToArr(){
        String string = "0,0,1,1,0,1,0";
        int[] arr = {0,0,1,1,0,1,0};
        int[] result = help.stringToArr(string);
        String ansString ="";
        String arrString ="";

        for (int i = 0; i<arr.length; i++){
            ansString += arr[i]+" ";
        }
        for (int i = 0; i<arr.length; i++){
            arrString += result[i]+" ";
        }
        assertEquals(ansString, arrString);

    }

    @Test
    public void getActiveDays(){
        int[] arr = {0,0,1,1,0,1,0};
        ArrayList<Integer> activeDays = new ArrayList<>();
        activeDays.add(4);
        activeDays.add(5);
        activeDays.add(7);

        assertEquals(activeDays, help.getActiveDays(arr));

    }

    @Test
    public void intArrayListToString(){
        String ans = "2, 3, 5";
        ArrayList<Integer> activeDays = new ArrayList<>();
        activeDays.add(2);
        activeDays.add(3);
        activeDays.add(5);

        assertEquals(ans, help.intArrayListToString(activeDays));

    }

    @Test
    public void calToShortString(){
        String time = "10:05";

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 05);
        c.set(Calendar.SECOND, 0);

        assertEquals(time, help.calToShortString(c));

    }

    @Test
    public void timePrintOutToCal(){
        String timePrintOut = "2017-04-04 17:34:00";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 17);
        c.set(Calendar.MINUTE, 34);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.YEAR, 2017);
        c.set(Calendar.MONTH, 3);
        c.set(Calendar.DAY_OF_MONTH, 4);
        c.set(Calendar.MILLISECOND, 0);


        assertEquals(c, help.timePrintOutToCal(timePrintOut));

    }

    @Test
    public void timePrintOutCalToString(){
        String timePrintOut = "2017-04-04 17:34:10";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 17);
        c.set(Calendar.MINUTE, 34);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.YEAR, 2017);
        c.set(Calendar.MONTH, 3);
        c.set(Calendar.DAY_OF_MONTH, 4);
        c.set(Calendar.MILLISECOND, 0);


        assertEquals(timePrintOut, help.timePrintOut(c));
    }

    @Test
    public void timePrintOutAlarmToString(){
        Calendar c = Calendar.getInstance();

        String timePrintOut = c.get(Calendar.YEAR)+"-"+help.addLeadingZero(c.get(Calendar.MONTH)+1)+"-"+help.addLeadingZero(c.get(Calendar.DAY_OF_MONTH))+" 17:34:00";

        Alarm a = new Alarm(-1,"17:34",new int[]{1,0,0,0,0,0,0},-1,-1,"00:00", false, true,new String[]{}, -1);

        assertEquals(timePrintOut.substring(0,15), help.timePrintOut(a).substring(0,15));
    }

    @Test
    public void addLeadingZero(){
        assertEquals("01", help.addLeadingZero(1));
    }

    @Test
    public void dontAddLeadingZero(){
        assertEquals("11", help.addLeadingZero(11));
    }

    @Test
    public void CheckFieldsPositive() throws Exception {
        String s = "This is a String";
        String s1 = "This is another String";
        String s2 = "This is yet another String";
        boolean pass = help.checkFields(s,s1,s2);
        assertEquals(true, pass);
    }

    @Test
    public void CheckFieldsNegative() throws Exception {
        String s = "";
        String s1 = " ";
        String s2 = null;
        boolean pass = help.checkFields(s,s1,s2);
        assertEquals(false, pass);
    }

}
