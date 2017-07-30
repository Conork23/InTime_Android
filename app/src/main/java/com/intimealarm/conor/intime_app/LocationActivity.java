package com.intimealarm.conor.intime_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;

import com.intimealarm.conor.intime_app.adapters.AlarmRecyclerAdapter;
import com.intimealarm.conor.intime_app.adapters.LocationRecyclerAdapter;
import com.intimealarm.conor.intime_app.models.Alarm;
import com.intimealarm.conor.intime_app.models.Location;
import com.intimealarm.conor.intime_app.utilities.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationActivity extends AppCompatActivity {

    // Variables
    LocationRecyclerAdapter adapter;

    // ButterKinfe Bindings
    @BindView(R.id.location_list) RecyclerView location_list;

    // Add Location Click
    @OnClick(R.id.loc_fab)
    public void addLocation(View v){
        startActivityForResult(new Intent(this, AddLocationActivity.class), Constants.ADD_LOCATION);
    }

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

        // Initilizing Recycler View
        location_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationRecyclerAdapter(this);
        location_list.setAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback());
        touchHelper.attachToRecyclerView(location_list);

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

    // Delete Location
    private void deleteItem(int adapterPosition) {
        Location loc = adapter.getItemAtPosition(adapterPosition);
        adapter.tempRemove(adapterPosition);
        offerUndo(adapterPosition, loc);
    }

    // Offer ability to undo delete
    private void offerUndo(final int adapterPosition, final Location loc) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Undo", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.add(adapterPosition, loc);
                    }
                })
                .setActionTextColor(getResources().getColor(R.color.colorAccent));

        snack.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    adapter.remove(loc);
                }
                super.onDismissed(snackbar, event);

            }
        });

        snack.show();
    }

    // On Activity result for adding alarm
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.ADD_LOCATION){
            if (resultCode == RESULT_OK){
                Location loc = (Location) data.getSerializableExtra(Constants.EXTRA_LOCATION);
                adapter.add(loc);
            }
        }
    }

}
