package com.jh.safereturn;


import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.Parse;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    CharSequence title;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ListView navList;
    String[] navItems ={"Police", "Siren", "home", "Test"};
    ArrayAdapter<String> adapterDrawerList;

    FindPolice policeFr;
    FragmentHome homeFr;
    SirenMaker sirenFr;
    ShowLocation locationFr;





    //////////////////////
    long lastTime;
    float speed;
    float lastX;
    float lastY;
    float lastZ;
    float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    SensorManager sensorManager;
    Sensor accelerormeterSensor;
    MediaPlayer mp;
    /////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        Parse.initialize(this, "GMPoXbwsPM7sNnBQYUUFYnkMkC4LiMxzOYaHcXgh", "1UfwfA5whNUf85Jwl1xbYgEjtRFCEixmKmjZOs44");


        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        policeFr = new FindPolice();
        homeFr = new FragmentHome();
        sirenFr = new SirenMaker();
        locationFr = new ShowLocation();

        navList = (ListView)findViewById(R.id.nav_list);
        adapterDrawerList=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,navItems);
        navList.setAdapter(adapterDrawerList);
        navList.setOnItemClickListener(new DrawerItemClickListener());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();


        /////////////////////////////
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mp = MediaPlayer.create(this, R.raw.police);
        ///////////////////////////////////////////////////////////
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            switch (position){
                case 0:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, policeFr).commit();

                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, sirenFr).commit();
                    break;
                case 2:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();
                    break;
                case 3:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, locationFr).commit();
                    break;

            }
            drawerLayout.closeDrawer(navList);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
        mp.stop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    // 이벤트발생!!
                    mp.start();
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }

        }

    }
}
