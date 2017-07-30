package com.intimealarm.conor.intime_app;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAlarmActivity extends AppCompatActivity {

    // Variables
    final Calendar CURRENT_TIME = Calendar.getInstance();
    int hour, min, to = -1, from = -1, id, listPosition = -1;
    int[] days = {0,0,0,0,0,0,0};
    boolean isSmart = false, isEdit = false, isPublic = true;
    ArrayList<Location> locationArr;
    ArrayList<String> locLbls;
    DbHelper db;
    ArrayAdapter<String> adapter;
    Helper help;

    // ButterKife Bindings
    @BindView(R.id.from_spinner) Spinner fromSpinner;
    @BindView(R.id.to_spinner) Spinner toSpinner;
    @BindView(R.id.add_alarm_time) TextView timeTF;
    @BindView(R.id.to_spinner_lbl) TextView toSpinnerLbl;
    @BindView(R.id.from_spinner_lbl) TextView fromSpinnerLbl;
    @BindView(R.id.due_time_lbl) TextView dueTimeLbl;
    @BindView(R.id.due_time_tv) TextView dueTime;
    @BindView(R.id.btn_due_time) Button dueTimeBtn;
    @BindView(R.id.rg_smart_alarm) RadioGroup rgSmartAlarm;
    @BindView(R.id.rg_ispublic_alarm) RadioGroup rgIsPublic;
    @BindView(R.id.ispublic_alarm_lbl) TextView isPublicLbl;
    @BindView(R.id.busCB) CheckBox busCB;
    @BindView(R.id.trainCB) CheckBox trainCB;
    @BindView(R.id.tramCB) CheckBox tramCB;
    @BindView(R.id.tmodes_cb) LinearLayout tmodesCB;
    @BindView(R.id.tmodes_lbl) TextView tmodesLbl;
    @BindView(R.id.prepTime_et) EditText prepET;
    @BindView(R.id.prepTime_lbl) TextView prepLbl;


    // Save click
    @OnClick(R.id.add_save_btn)
    public void onSaveClick(View v){
        int prep = -1;
        boolean errorFree = false;

        if (isSmart){
            if (help.checkFields(prepET.getText().toString())){
                prep = Integer.parseInt(prepET.getText().toString());
                errorFree = true;
            }
        }else{
            errorFree = true;
        }

        if (errorFree){
            Alarm a = new Alarm(id,
                    timeTF.getText().toString(),
                    days,
                    from,
                    to,
                    dueTime.getText().toString(),
                    true,
                    isPublic,
                    getTmodes(),
                    prep);

            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_ALARM, a);
            if (isEdit){
                intent.putExtra(Constants.EXTRA_ALARM_POS, listPosition);
            }
            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(this, R.string.prepTimeError, Toast.LENGTH_SHORT).show();
        }

    }

    // Change Alarm time click
    @OnClick({R.id.add_change_btn, R.id.add_alarm_time})
    public void onChangeClick(View v){
        showTimePicker(false);
    }

    // Change Due time click
    @OnClick({R.id.btn_due_time, R.id.due_time_tv})
    public void onDueTimeClick(View v){
        showTimePicker(true);
    }

    // Active Days Click
    @OnClick({ R.id.btn_m, R.id.btn_tu,R.id.btn_w, R.id.btn_th, R.id.btn_f, R.id.btn_sa, R.id.btn_su })
    public void clickDay(View v) {
        int id = v.getId();
        int state;

        switch (id){
            case R.id.btn_m:
                days[0] = (days[0] == 0)? 1:0;
                state = days[0];
                break;
            case R.id.btn_tu:
                days[1] = (days[1] == 0)? 1:0;
                state = days[1];
                break;
            case R.id.btn_w:
                days[2] = (days[2] == 0)? 1:0;
                state = days[2];
                break;
            case R.id.btn_th:
                days[3] = (days[3] == 0)? 1:0;
                state = days[3];
                break;
            case R.id.btn_f:
                days[4] = (days[4] == 0)? 1:0;
                state = days[4];
                break;
            case R.id.btn_sa:
                days[5] = (days[5] == 0)? 1:0;
                state = days[5];
                break;
            case R.id.btn_su:
                days[6] = (days[6] == 0)? 1:0;
                state = days[6];
                break;
            default:
                state = 0;

        }

        changeColor(id,state);

    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        ButterKnife.bind(this);
        db = new DbHelper(this);
        help = new Helper();
        Intent intent = getIntent();

        locLbls = new ArrayList<>();
        locationArr = new ArrayList<>();
        updateLocations();

        toSpinner.setOnItemSelectedListener(spinnerSelectedListener());
        fromSpinner.setOnItemSelectedListener(spinnerSelectedListener());

        // Check if adding a new alarm of editing existing
        if (intent.hasExtra(Constants.EXTRA_ALARM)){
            isEdit = true;
            listPosition = intent.getIntExtra(Constants.EXTRA_ALARM_POS,0);
            setAlarmValues((Alarm)intent.getSerializableExtra(Constants.EXTRA_ALARM));
            String time = help.addLeadingZero(hour)+":"+help.addLeadingZero(min);
            timeTF.setText(time);
        }else{
            id = new Random().nextInt(99999);
            hour = CURRENT_TIME.get(Calendar.HOUR_OF_DAY);
            min = CURRENT_TIME.get(Calendar.MINUTE);
            showTimePicker(false);

        }

        // Smart Alarm Radio Listeners
        rgSmartAlarm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                isSmart = (i == R.id.radio_smart_yes);
                setSmartVisability(isSmart);
            }
        });

        // Is Puble Radio Listeners
        rgIsPublic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                isPublic = !(isPublic);
                setModesVisibility(isPublic);
            }
        });

    }

    // Setting Transport Mode Inputs visibility
    private void setModesVisibility(boolean isPublic) {
        if (isPublic){
            tmodesCB.setVisibility(View.VISIBLE);
            tmodesLbl.setVisibility(View.VISIBLE);
        }else{
            tmodesCB.setVisibility(View.GONE);
            tmodesLbl.setVisibility(View.GONE
            );
        }
    }

    // Set alla values if an alarm is being edited
    private void setAlarmValues(Alarm alarm) {
        id = alarm.getId();
        hour = help.getHourFromTime(alarm.getTime());
        min = help.getMinFromTime(alarm.getTime());
        days = alarm.getDays();
        isPublic = alarm.getPublic();
        if (isPublic){
            ((RadioButton) findViewById(R.id.radio_ispublic_yes)).setChecked(true);
            setModes(alarm.getTmodes());
        }else{
            ((RadioButton) findViewById(R.id.radio_ispublic_no)).setChecked(true);

        }
        setModesVisibility(isPublic);

        isSmart = (alarm.getTo() != -1 && alarm.getFrom() != -1 );
        if (isSmart){
            ((RadioButton) findViewById(R.id.radio_smart_yes)).setChecked(true);
        }
        setSmartVisability(isSmart);
        dueTime.setText(alarm.getDueTime());
        changeColor(R.id.btn_m, days[0]);
        changeColor(R.id.btn_tu, days[1]);
        changeColor(R.id.btn_w, days[2]);
        changeColor(R.id.btn_th, days[3]);
        changeColor(R.id.btn_f, days[4]);
        changeColor(R.id.btn_sa, days[5]);
        changeColor(R.id.btn_su, days[6]);
        to = alarm.getTo();
        from = alarm.getFrom();
        setLocActive(db.getLoc(to), Constants.TO_LOC_SPINNER);
        setLocActive(db.getLoc(from), Constants.FROM_LOC_SPINNER);
        prepET.setText(alarm.getPreptime()+"");
    }

    // Set the relevant transport modes checked
    private void setModes(String[] tmodes) {
        boolean bus = (Arrays.asList(tmodes).contains(Constants.BUS));
        boolean train = (Arrays.asList(tmodes).contains(Constants.TRAIN));
        boolean tram = (Arrays.asList(tmodes).contains(Constants.TRAM));

        busCB.setChecked(bus);
        trainCB.setChecked(train);
        tramCB.setChecked(tram);
    }

    // Set the dues time
    private void setDueTime() {
        Calendar dueCal = help.getTimeFromString(timeTF.getText().toString());
        dueCal.set(Calendar.MINUTE, 0);
        dueCal.add(Calendar.HOUR, 1);
        dueTime.setText(help.calToShortString(dueCal));
    }

    // Show the time picker
    private void showTimePicker(Boolean isDueTime){
        TimePickerDialog timePicker = new TimePickerDialog(this, timePickerSelect(isDueTime),hour, min, true );
        timePicker.setTitle("");
        timePicker.show();
    }

    // Time Picker on time set listener
    private TimePickerDialog.OnTimeSetListener timePickerSelect(final Boolean isDueTime){
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int h, int m) {
                String time = help.addLeadingZero(h)+":"+help.addLeadingZero(m);
                if (isDueTime){
                    dueTime.setText(time);
                }else{
                    timeTF.setText(time);
                }
            }
        };
    }

    // Change color of day button
    private void changeColor(int id, int state){
        Button btn = (Button) findViewById(id);

        if(state == 1){
           btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }else{
           btn.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }

    // set the visibility of if the alarm is smart or not
    private void setSmartVisability(boolean makeVisible){
        if(makeVisible){
            setDueTime();
            rgIsPublic.setVisibility(View.VISIBLE);
            isPublicLbl.setVisibility(View.VISIBLE);
            fromSpinner.setVisibility(View.VISIBLE);
            toSpinner.setVisibility(View.VISIBLE);
            fromSpinnerLbl.setVisibility(View.VISIBLE);
            toSpinnerLbl.setVisibility(View.VISIBLE);
            dueTime.setVisibility(View.VISIBLE);
            dueTimeLbl.setVisibility(View.VISIBLE);
            dueTimeBtn.setVisibility(View.VISIBLE);
            prepET.setVisibility(View.VISIBLE);
            prepLbl.setVisibility(View.VISIBLE);
            setModesVisibility(isPublic);
        }else{
            to = -1;
            from = -1;
            rgIsPublic.setVisibility(View.GONE);
            isPublicLbl.setVisibility(View.GONE);
            fromSpinner.setVisibility(View.GONE);
            toSpinner.setVisibility(View.GONE);
            fromSpinnerLbl.setVisibility(View.GONE);
            toSpinnerLbl.setVisibility(View.GONE);
            dueTime.setVisibility(View.GONE);
            dueTimeLbl.setVisibility(View.GONE);
            dueTimeBtn.setVisibility(View.GONE);
            prepET.setVisibility(View.GONE);
            prepLbl.setVisibility(View.GONE);
            setModesVisibility(false);

        }

    }

    // Spinner Selected listener
    private AdapterView.OnItemSelectedListener spinnerSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int locId = -1;
                int requestcode;
                if (i == locLbls.size()-1){
                    if(adapterView.getId() == R.id.to_spinner){
                        requestcode = Constants.NEW_LOC_TO_REQUEST;
                    }else{
                        requestcode = Constants.NEW_LOC_FROM_REQUEST;
                    }
                    startActivityForResult(new Intent(AddAlarmActivity.this,AddLocationActivity.class), requestcode);
                }else{
                    locId = locationArr.get(i).getId();
                }

                if(adapterView.getId() == R.id.to_spinner){
                    to = locId;
                }else if(adapterView.getId() == R.id.from_spinner){
                    from = locId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    // On activity Result from adding a new location
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.NEW_LOC_TO_REQUEST){
            if (resultCode == RESULT_OK){
                Location loc = (Location) data.getSerializableExtra(Constants.EXTRA_LOCATION);
                db.addLocation(loc);
                updateLocations();
                setLocActive(loc, Constants.TO_LOC_SPINNER);
            }
        }
        if(requestCode == Constants.NEW_LOC_FROM_REQUEST){
            if (resultCode == RESULT_OK){
                Location loc = (Location) data.getSerializableExtra(Constants.EXTRA_LOCATION);
                loc = db.addLocation(loc);
                updateLocations();
                setLocActive(loc, Constants.FROM_LOC_SPINNER);
            }
        }
    }

    // Set the desired location active
    private void setLocActive(Location loc, String spinner ) {
        int pos = -1;
        int id = -1;
        for (int i = 0; i < locationArr.size(); i++){
            if (locationArr.get(i).getId() == loc.getId()){
                pos = i;
                id  = loc.getId();
                break;
            }
        }

        if (spinner.equals(Constants.TO_LOC_SPINNER)){
            to = id;
            toSpinner.setSelection(pos);
        }else{
            from = id;
            fromSpinner.setSelection(pos);
        }
    }

    // Update locations spinner if a new location has been added
    private void updateLocations(){
        locLbls = new ArrayList<>();
        locationArr = db.allLocations();
        for (Location l: locationArr){
            locLbls.add(l.getLable());
        }
        locLbls.add(getString(R.string.spinner_add_location));

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, locLbls);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(adapter);
    }

    // Get the Public Transport Modes
    private String[] getTmodes() {
        if (!isPublic){
            return new String[0];
        }

        ArrayList<String> modes = new ArrayList<>();
        if (busCB.isChecked()){
            modes.add(Constants.BUS);
        }
        if (trainCB.isChecked()){
            modes.add(Constants.TRAIN);
        }
        if (tramCB.isChecked()){
            modes.add(Constants.TRAM);
        }

        return modes.toArray(new String[modes.size()]);
    }
}
