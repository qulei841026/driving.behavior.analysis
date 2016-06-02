package com.carsmart.driving;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrivingLineChart dlc;
    VelocityLineChart vlc;
    TextView tv0;
    TextView tv1;
    TextView tv2;
    TextView tv3;

    SensorProvider sensorProvider;

    LogProvider logProvider;
    LogicProcessor logicProcessor;

    int up;
    int down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        logProvider = new LogProvider(this);
        logicProcessor = new LogicProcessor();

        logicProcessor.setListener(new LogicProcessor.OnLogicProcessorListener() {
            @Override
            public void onStatusChanged(int status) {
                if (status == 1) {
                    up++;
                    tv0.setText("加速：" + up + "，减速：" + down + "，当前：" + "加速");
                } else if (status == 2) {
                    down++;
                    tv0.setText("加速：" + up + "，减速：" + down + "，当前：" + "减速");
                }
            }
        });

        sensorProvider = new SensorProvider(getBaseContext());
        sensorProvider.setSensorListener(new SensorProvider.OnSensorListener() {
            @Override
            public void onSensorOriginal(float x, float y, float z) {
                logProvider.logOriginal(x, y, z);
            }


            @Override
            public void onSensorDenoise(float x, float y, float z) {
                logProvider.logDenoise(x, y, z);
            }

            @Override
            public void onSensorAcceleration(float acceleration) {
                tv2.setText("加速度：" + (int) (acceleration * 1000) / 1000f);
                logProvider.logFilter(acceleration);
                showAcceleration(acceleration);
            }

        });

        sensorProvider.setVelocityListener(new OnVelocityListener() {
            @Override
            public void onChanged(float velocity) {
                logProvider.logVelocity(velocity);
                showVelocity(velocity);
            }
        });


        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logProvider.init();
                sensorProvider.init();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up = 0;
                down = 0;
                logicProcessor.setVelocity(0f);
                tv0.setText("");
                logProvider.destroy();
                sensorProvider.destroy();
                accelerations.clear();
                velocities.clear();
            }
        });

        tv0 = (TextView) findViewById(R.id.tv0);

        tv1 = (TextView) findViewById(R.id.tv1);

        tv2 = (TextView) findViewById(R.id.tv2);

        tv3 = (TextView) findViewById(R.id.tv3);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        dlc = (DrivingLineChart) findViewById(R.id.dlc);
        vlc = (VelocityLineChart) findViewById(R.id.vlc);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logProvider.destroy();
        sensorProvider.destroy();
        accelerations.clear();
        velocities.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv1.setText("Filter : " + Utils.getString(this, "filter", "3")
                + " , DelayMillis : " + Utils.getString(this, "delay_millis", "200")
                + " , Axis : " + getResources().getStringArray(R.array.axis_titles)
                [Integer.valueOf(Utils.getString(this, "axis", "4")) - 1]
        );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAcceleration(float f) {
        if (accelerations.size() > dlc.maxCount) {
            accelerations.remove(0);
        }
        accelerations.add(f);
        dlc.setValues(accelerations);
    }

    private void showVelocity(float f) {
        tv3.setText("速度：" + (int) (f * 3.6 * 1000) / 1000f + "km/h");
        if (velocities.size() > vlc.maxCount) {
            velocities.remove(0);
        }
        logicProcessor.setVelocity(f);
        velocities.add(f);
        vlc.setValues(velocities);
    }

    LinkedList<Float> accelerations = new LinkedList<>();

    LinkedList<Float> velocities = new LinkedList<>();
}
