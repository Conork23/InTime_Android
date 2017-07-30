package com.intimealarm.conor.intime_app.utilities;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 06/03/2017.
 */

public class Constants {

    // Alarm Constants
    public final static int INTERVAL = -20;
    public final static double RADIUS = 0.002;
    public final static int SINGLE_ALARM = 0;
    public final static int REPEATING_ALARM = 1;
    public final static int SMART_SINGLE_ALARM = 2;
    public final static int SMART_REPEATING_ALARM = 3;
    public final static int SINGLE_NO_DISABLE = 4;
    public final static int STATS_ALARM = 5;

    // Tags
    public final static String TAG_ALARM_ADAPTER = "Alarm_Adapter";
    public final static String TAG_LOCATION_ADAPTER = "Alarm_Adapter_Location";
    public final static String TAG_ALARM_CONTROLLER  = "Alarm_Controller";
    public final static String TAG_ALARM_RECIEVER = "Alarm_Receiver" ;
    public final static String TAG_ALARM_REQUESTS = "Alarm_Requests";
    public final static String TAG_ALARM_NETWORK_SERVICE = "Alarm_Service";
    public final static String TAG_ALARM_RINGTONE = "Alarm_Ringtone";
    public final static String TAG_ALARM_ADD = "Alarm_Add_Activity";
    public final static String TAG_ALARM_EVAL = "Alarm_Eval_Service";
    public final static String TAG_L_SERVICE = "Alarm_Location_Service";

    // Extra Keys
    public final static String EXTRA_ALARM_TYPE = "TYPE";
    public final static String EXTRA_ALARM = "ALARM";
    public final static String EXTRA_STOP_RINGTONE = "STOP";
    public final static String EXTRA_ALARM_ID = "ID";
    public final static String EXTRA_LOCATION = "LOCATION";
    public final static String EXTRA_EVAL_POST = "EvaluationPost";
    public final static String EXTRA_EVAL = "EVALUATION";
    public final static String EXTRA_ISPUBLIC = "ISPUBLIC";
    public static final String EXTRA_ALARM_POS = "EXTRA_ALARM_POS" ;

    //Request Codes
    public static final int NEW_LOC_TO_REQUEST = 100;
    public static final int NEW_LOC_FROM_REQUEST = 101;
    public static final int ADD_ALARM = 102;
    public static final int RC_LOCATION = 103;
    public static final int EDIT_ALARM = 104;
    public static final int PICK_PLACE = 105;
    public static final int SERVICES_REQUEST = 106;
    public static final int ADD_LOCATION = 107;

    // Shared Preference Constants
    public static final String PREFS_FILE = "Prefs_File";
    public static final String SHARED_EVAL = "SHARED_EVAL";
    public static final String TO_LOC_SPINNER ="TO_LOC_SPINNER" ;
    public static final String FROM_LOC_SPINNER ="FROM_LOC_SPINNER" ;
    public static final String SHARED_FIRST_START ="FIRST_START" ;
    public static final String SHARED_STATS ="SHARED_STATS" ;

    //Transport Types
    public static final String BUS = "bus";
    public static final String TRAIN = "train";
    public static final String TRAM = "tram";

}
