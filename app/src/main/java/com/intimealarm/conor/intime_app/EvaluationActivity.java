package com.intimealarm.conor.intime_app;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 11/11/2016.
 *
 * This class was used to evaluate the system based on the estimated and actual travel time.
 * It was used during development but its usage has been commented out for production/upload.
 * Furthermore, the evaluation of the system can now be based of the anonymous data collected
 * from the users.
 * If you wise to reenable this class uncomment the code in res/menu/menu_main and also
 * the onOptionsItemSelected method in MainActivity
 *
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.services.EvaluationService;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EvaluationActivity extends AppCompatActivity {

    // Variables
    final private int PUBLIC = R.id.publicRadio;
    final private int PRIVATE = R.id.privateRadio;
    Helper help;
    DbHelper db;
    ArrayList<Location> locationArr;
    ArrayList<String> locLbls;
    ArrayAdapter<String> adapter;
    int to = -1, from =-1;

    // ButterKnife Bindings
    @BindView(R.id.typeGroup) RadioGroup typeGroup;
    @BindView(R.id.eval_from_spinner) Spinner fromSpinner;
    @BindView(R.id.eval_to_spinner) Spinner toSpinner;

    // Start Evaluation
    @OnClick(R.id.startBtn)
    public void startClick(View view) {

        Calendar now = Calendar.getInstance();

        String time = help.calToShortString(now);
        boolean isPublic = false;
        switch (typeGroup.getCheckedRadioButtonId()){
            case PUBLIC: isPublic = true;
                break;
            case PRIVATE: isPublic = false;
                break;
        }

        Alarm a = new Alarm(new Random().nextInt(99999), time, new int[] {1,0,0,0,0,0,0}, from, to, "00:00", true, isPublic,new String[]{},0);

        Intent i = new Intent(this, EvaluationService.class);
        i.putExtra(Constants.EXTRA_ALARM, a);
        i.putExtra(Constants.EXTRA_ISPUBLIC, isPublic);
        i.putExtra(Constants.EXTRA_EVAL_POST, false);
        Toast.makeText(this, a.getTime()+" "+a.getTo() +" "+a.getFrom(), Toast.LENGTH_SHORT).show();
        this.startService(i);
    }

    // Stop Evaluation
    @OnClick(R.id.stopBtn)
    public void stopClick(View view){
        Intent i = new Intent(this, EvaluationService.class);
        i.putExtra(Constants.EXTRA_EVAL_POST, true);
        this.startService(i);
    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ButterKnife.bind(this);
        help = new Helper();
        db = new DbHelper(this);

        locLbls = new ArrayList<>();
        locationArr = new ArrayList<>();
        updateLocations();

        toSpinner.setOnItemSelectedListener(spinnerSelectedListener());
        fromSpinner.setOnItemSelectedListener(spinnerSelectedListener());
    }

    // Location spinner click listener
    private AdapterView.OnItemSelectedListener spinnerSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int locId = -1;
                int requestcode;
                if (i == locLbls.size()-1){
                    if(adapterView.getId() == R.id.eval_to_spinner){
                        requestcode = Constants.NEW_LOC_TO_REQUEST;
                    }else{
                        requestcode = Constants.NEW_LOC_FROM_REQUEST;
                    }
                    startActivityForResult(new Intent(EvaluationActivity.this,AddLocationActivity.class), requestcode);
                }else{
                    locId = locationArr.get(i).getId();
                }

                if(adapterView.getId() == R.id.eval_to_spinner){
                    to = locId;
                }else if(adapterView.getId() == R.id.eval_from_spinner){
                    from = locId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    // On Activity result for new location
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.NEW_LOC_TO_REQUEST){
            if (resultCode == RESULT_OK){
                Location loc = (Location) data.getSerializableExtra(Constants.EXTRA_LOCATION);
                db.addLocation(loc);
                updateLocations();
                setLocActive(loc, "TO");
            }
        }
        if(requestCode == Constants.NEW_LOC_FROM_REQUEST){
            if (resultCode == RESULT_OK){
                Location loc = (Location) data.getSerializableExtra(Constants.EXTRA_LOCATION);
                loc = db.addLocation(loc);
                updateLocations();
                setLocActive(loc, "FROM");
            }
        }
    }

    // Set Location Spinner
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

        if (spinner.equals("TO")){
            to = id;
            toSpinner.setSelection(pos);
        }else{
            from = id;
            fromSpinner.setSelection(pos);
        }
    }

    // Update Spinner when new location is added.
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

}
