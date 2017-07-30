package com.intimealarm.conor.intime_app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.intimealarm.conor.intime_app.R;
import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.utilities.Constants;
import com.intimealarm.conor.intime_app.utilities.Helper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 25/01/2017.
 */
public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.ViewHolder> {

    // Variables
    Context context;
    DbHelper db;
    Helper help;
    ArrayList<Location> dataset = new ArrayList<>();

    // Constructor
    public LocationRecyclerAdapter(Context context) {
        this.context = context;
        help = new Helper();
        db = new DbHelper(context);
        this.dataset = db.allLocations();
        Log.d(Constants.TAG_LOCATION_ADAPTER, "LocationRecyclerAdapter: "+getItemCount());
    }

    // Create Instance of View Holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        LocationRecyclerAdapter.ViewHolder holder = new LocationRecyclerAdapter.ViewHolder(v);
        return holder;
    }

    // Bind Location to View
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Location location = dataset.get(position);
        Log.d(Constants.TAG_LOCATION_ADAPTER, "onBindViewHolder: "+ location.getId());
        holder.lable.setText(location.getLable());
        holder.address.setText(location.getAddress());
        holder.rootView.setOnClickListener(editlabelClick(location));
    }

    // Location Click listener to allow user to change location label with a dialog
    private View.OnClickListener editlabelClick(final Location location){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_input, null);
                final EditText input = (EditText) dialogView.findViewById(R.id.label_change_input);
                input.setText(location.getLable());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                AlertDialog dialog = builder.setTitle(R.string.change_label_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.change_label_dialog_positive_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newLabel = input.getText().toString();
                        if(help.checkFields(newLabel)){
                            location.setLable(newLabel);
                            setLabel(location);
                        }else{
                            Toast.makeText(context, R.string.complete_all_fields,Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .setNegativeButton(R.string.change_label_dialog_negative_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.show();

                input.requestFocus();

            }

        };
    }

    // save the label to the Database and update the view
    private void setLabel(Location loc) {
        db.updateLocation(loc);
        this.notifyDataSetChanged();
    }

    // Get total number of locations
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // Temp Delete location
    public void tempRemove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    // Get Location at Position X
    public Location getItemAtPosition(int position) {
        return dataset.get(position);
    }

    // add location at position
    public void add(int position, Location loc) {
        dataset.add(position,loc);
        notifyItemInserted(position);
    }

    // fully delet location from db
    public void remove(Location loc) {
        db.deleteLocation(loc);
    }

    // add location
    public void add(Location loc) {
        dataset.add(db.addLocation(loc));
        notifyDataSetChanged();
    }

    // View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        @BindView(R.id.location_lable)
        TextView lable;

        @BindView(R.id.location_address)
        TextView address;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            rootView = v;
        }

    }
}

