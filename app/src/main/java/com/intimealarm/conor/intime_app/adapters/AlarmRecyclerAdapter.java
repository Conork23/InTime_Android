package com.intimealarm.conor.intime_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intimealarm.conor.intime_app.AddAlarmActivity;
import com.intimealarm.conor.intime_app.logic.AlarmController;
import com.intimealarm.conor.intime_app.database.DbHelper;
import com.intimealarm.conor.intime_app.R;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.utilities.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 09/01/2017.
 */

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.ViewHolder> {

    // Variables
    private ArrayList<Alarm> dataset;
    DbHelper db;
    Context context;
    AlarmController aController;

    // Constructor
    public AlarmRecyclerAdapter(Context c) {
        this.context = c;
        db = new DbHelper(c);
        dataset = db.allAlarms();
        aController = new AlarmController(c);
    }

    // Get Alarm at Position X
    public Alarm getItemAtPosition(int position) {
        return dataset.get(position);
    }

    // Add Alarm to List
    public void add(Alarm alarm) {
        dataset.add(db.addAlarm(alarm));
        aController.setAlarm(alarm);
        notifyDataSetChanged();
    }

    // Add Alarm at Position X
    public void add(int position, Alarm alarm) {
        dataset.add(position, alarm);
        notifyItemInserted(position);
    }

    // Delete Alarm at Position X
    public void remove(Alarm alarm) {
        if(alarm.getActive()){
            alarm.setActive(false);
            aController.setAlarm(alarm);
        }
        db.deleteAlarm(alarm);
    }

    // Temp Delete Alarm at Position X
    public void tempRemove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    // Create Instance of View Holder
    @Override
    public AlarmRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    // Bind Alarm to View
    @Override
    public void onBindViewHolder(AlarmRecyclerAdapter.ViewHolder holder, final int position) {
        final Alarm alarm = dataset.get(position);
        holder.time.setText(alarm.getTime());

        Location locTo = db.getLoc(alarm.getTo());
        Location locFrom = db.getLoc(alarm.getFrom());

        String to = (locTo.getLable() == null)? "???":locTo.getLable();
        String from = (locFrom.getLable() == null)? "???":locFrom.getLable();
        String trip = (to.equals("???") && from.equals("???"))? "No Location Set" : from+" - "+to ;
        holder.location.setText(trip);
        holder.isActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIsActive(alarm, position);
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddAlarmActivity.class);
                i.putExtra(Constants.EXTRA_ALARM, alarm);
                i.putExtra(Constants.EXTRA_ALARM_POS, position);
                ((Activity) context).startActivityForResult(i, Constants.EDIT_ALARM);
            }
        });


        setActive(holder, alarm);
    }

    // change active status
    private void updateIsActive(Alarm a, int p) {
        if(a.getActive()){
            a.setActive(false);
        }else{
            a.setActive(true);
        }

        updateAlarm(a, p);
    }

    // update alarm
    public void updateAlarm(Alarm a, int p) {
        aController.setAlarm(a);
        db.updateAlarm(a);
        dataset.remove(p);
        dataset.add(p,a);
        notifyItemChanged(p);
    }

    // Get Total Number of Alarms
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    // View Holder Class
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_active)
        ImageButton isActive;
        @BindView(R.id.tf_time)
        protected TextView time;
        @BindView(R.id.tf_active_day_m)
        protected TextView day_m;
        @BindView(R.id.tf_active_day_tu)
        protected TextView day_tu;
        @BindView(R.id.tf_active_day_w)
        protected TextView day_w;
        @BindView(R.id.tf_active_day_th)
        protected TextView day_th;
        @BindView(R.id.tf_active_day_f)
        protected TextView day_f;
        @BindView(R.id.tf_active_day_sa)
        protected TextView day_sa;
        @BindView(R.id.tf_active_day_su)
        protected TextView day_su;
        @BindView(R.id.tf_location)
        protected TextView location;

        protected TextView[] days;

        View rootView;
        Context context;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            rootView = v;
            context = itemView.getContext();
            days = new TextView[]{day_m, day_tu, day_w, day_th, day_f, day_sa, day_su};

        }

    }

    // Set alarm UI Active or Inactive
    private void setActive(ViewHolder holder, Alarm alarm){
        if(!alarm.getActive()) {
            holder.isActive.setColorFilter(ContextCompat.getColor(holder.context,R.color.red));
            for(int i = 0; i<alarm.getDays().length; i++){
                TextView tf =  holder.days[i];
                tf.setTextColor(ContextCompat.getColor(holder.context,R.color.gray));
                if(alarm.getDays()[i] == 1){
                    tf.setTextColor(ContextCompat.getColor(holder.context,R.color.gray));
                    tf.setTextSize(12);
                }
                else {
                    tf.setTextColor(ContextCompat.getColor(holder.context,R.color.lightGray));
                    tf.setTextSize(10);
                }
            }
            holder.time.setTextColor(ContextCompat.getColor(holder.context,R.color.gray));
            holder.location.setTextColor(ContextCompat.getColor(holder.context,R.color.gray));
        }else{
            holder.isActive.setColorFilter(ContextCompat.getColor(holder.context,R.color.colorAccent));
            for(int i = 0; i<alarm.getDays().length; i++){
                TextView tf =  holder.days[i];
                if(alarm.getDays()[i] == 1){
                    tf.setTextColor(ContextCompat.getColor(holder.context,R.color.colorAccent));
                    tf.setTextSize(12);
                }
                else {
                    tf.setTextColor(ContextCompat.getColor(holder.context,R.color.gray));
                    tf.setTextSize(10);
                }
            }
            holder.time.setTextColor(ContextCompat.getColor(holder.context,R.color.black));
            holder.location.setTextColor(ContextCompat.getColor(holder.context,R.color.black));
        }

    }

}
