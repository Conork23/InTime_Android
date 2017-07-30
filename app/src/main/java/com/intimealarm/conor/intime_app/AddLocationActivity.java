package com.intimealarm.conor.intime_app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddLocationActivity extends AppCompatActivity {

    // Variables
    int status;
    Helper help;

    // ButterKnife Bindings
    @BindView(R.id.loc_add_lable) EditText lableTf;
    @BindView(R.id.loc_add_address) EditText addressTf;
    @BindView(R.id.loc_add_lat) EditText latTf;
    @BindView(R.id.loc_add_lng) EditText lngTf;

    // Add location click
    @OnClick(R.id.loc_add_btn)
    public void addLocation(View view){
        boolean completed = help.checkFields(lableTf.getText().toString(),
                addressTf.getText().toString(),
                latTf.getText().toString(),
                lngTf.getText().toString());
        if (completed){
            Location loc = new Location(
                    new Random().nextInt(99999),
                    lableTf.getText().toString(),
                    addressTf.getText().toString(),
                    Double.parseDouble(latTf.getText().toString()),
                    Double.parseDouble(lngTf.getText().toString()));

            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_LOCATION, loc);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(this, R.string.complete_all_fields, Toast.LENGTH_SHORT).show();
        }

    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        ButterKnife.bind(this);
        help = new Helper();

        checkServices();

    }

    // Check if User has Google Play Services
    private void checkServices() {
        GoogleApiAvailability apiAvalibility = GoogleApiAvailability.getInstance();
        status = apiAvalibility.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (apiAvalibility.isUserResolvableError(status)) {
                apiAvalibility.getErrorDialog(this, status, Constants.SERVICES_REQUEST);
            }
        }else {
            pickPlace();
        }
    }

    // On Activity Result for Place Picker and Google Play Services
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PICK_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                getPlaceInfo(place);
                add(place);
            }
        }else if(requestCode == Constants.SERVICES_REQUEST){
            if (resultCode == RESULT_OK) {
                pickPlace();
            }else{
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        }
    }

    // Add Location
    private void add(Place place) {
        Location loc = new Location(
                new Random().nextInt(99999),
                place.getName().toString(),
                place.getAddress().toString(),
                place.getLatLng().latitude ,
                place.getLatLng().longitude);

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOCATION, loc);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // Show Place Picker
    private void pickPlace(){
        if (status == ConnectionResult.SUCCESS) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            builder.setLatLngBounds(new LatLngBounds( new LatLng(53.243595,-6.370697), new LatLng(53.455221,-6.155777)));
            try {
                startActivityForResult(builder.build(this), Constants.PICK_PLACE);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    // Get Location Info from Place
    private void getPlaceInfo(Place place){
        addressTf.setText(place.getAddress());
        lableTf.setText(place.getName());
        latTf.setText(place.getLatLng().latitude +"");
        lngTf.setText(place.getLatLng().longitude+"");

    }
}
