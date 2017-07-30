package com.intimealarm.conor.intime_app;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 21/10/2016.
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import com.intimealarm.conor.intime_app.adapters.AlarmRecyclerAdapter;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.utilities.Constants;

import android.os.Handler;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    // Variables
    AlarmRecyclerAdapter adapter;

    // ButterKife Bindings
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.list_alarms) RecyclerView alarmList;

    // Add Alarm Button
    @OnClick(R.id.fab)
    public void addAlarm(View view) {
        startActivityForResult(new Intent(this, AddAlarmActivity.class), Constants.ADD_ALARM);
    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Initilizing Recycler View
        alarmList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmRecyclerAdapter(this);
        alarmList.setAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback());
        touchHelper.attachToRecyclerView(alarmList);
        askForStatsPermission();
    }

    // Create menu at top of Screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Menu item click listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_locations){
            startActivity(new Intent(this, LocationActivity.class));
        }

        // Launch Evaluation Intent.
        // To enable uncomment here and in res/menu/menu_main.xml

//        else if (id == R.id.action_evaluation) {
//            startActivity(new Intent(this, EvaluationActivity.class));
//        }

        return super.onOptionsItemSelected(item);
    }

    // Recycler View touch handler
    private ItemTouchHelper.Callback touchHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {

                    // [UNUSED] On Move Method
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    // On Swipe Method
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        deleteItem(viewHolder.getAdapterPosition());
                    }

                    // Drawing Under View Method
                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                            View itemView = viewHolder.itemView;

                            Paint paint = new Paint();
                            Bitmap deleteIcon;

                            paint.setColor(getResources().getColor(R.color.colorAccent));
                            deleteIcon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_delete);
                            float height = (itemView.getHeight() / 2) - (deleteIcon.getHeight() / 2);
                            float bitmapWidth = deleteIcon.getWidth() + 50f;
                            float iconRight = (float) itemView.getRight() - bitmapWidth;

                            c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                            if (dX < -bitmapWidth) {
                                c.drawBitmap(deleteIcon, iconRight, (float) itemView.getTop() + height, null);
                            }


                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


                        }
                    }

                };
        return simpleCallback;
    }

    // Method to delete alarm
    private void deleteItem(int adapterPosition) {
        Alarm a = adapter.getItemAtPosition(adapterPosition);
        adapter.tempRemove(adapterPosition);
        offerUndo(adapterPosition, a);
    }

    // Method to Display Undo SnackBar
    private void offerUndo(final int position, final Alarm a) {
        Snackbar snack = Snackbar.make(findViewById(R.id.content_main), "Undo", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.add(position, a);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent));

        snack.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    adapter.remove(a);
                }
                super.onDismissed(snackbar, event);
            }
        });

        snack.show();
    }

    // Activity result listener for adding and editing alarms
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.ADD_ALARM){
            if (resultCode == RESULT_OK){
                Alarm a = (Alarm) data.getSerializableExtra(Constants.EXTRA_ALARM);
                adapter.add(a);
            }
        }else if (requestCode == Constants.EDIT_ALARM){
            if (resultCode == RESULT_OK){
                Alarm a = (Alarm) data.getSerializableExtra(Constants.EXTRA_ALARM);
                int position = data.getIntExtra(Constants.EXTRA_ALARM_POS,0);
                adapter.updateAlarm(a,position);
            }
        }
    }

    // Ask Permission to collect anonymous data
    private void askForStatsPermission() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences prefs = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
                boolean isFirstStart = prefs.getBoolean(Constants.SHARED_FIRST_START, true);
                if (isFirstStart) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder
                            .setTitle(R.string.stats_dialog_title)
                            .setMessage(R.string.stats_dialog_message)
                            .setPositiveButton(R.string.stats_dialog_positive_btn,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            SharedPreferences.Editor editor = prefs.edit()
                                                    .putBoolean(Constants.SHARED_STATS, true);
                                            editor.apply();

                                        }
                                    })
                            .setNegativeButton(R.string.stats_dialog_negative_btn,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            SharedPreferences.Editor editor = prefs.edit()
                                                    .putBoolean(Constants.SHARED_STATS, false);
                                            editor.apply();
                                        }
                                    })
                            .create();

                    dialog.show();

                    SharedPreferences.Editor editor = prefs.edit()
                            .putBoolean(Constants.SHARED_FIRST_START, false);
                    editor.apply();
                }



                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.RC_LOCATION);

                }
            }
        });


    }
}
