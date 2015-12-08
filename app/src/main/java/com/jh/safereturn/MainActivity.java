package com.jh.safereturn;

/**
 * Created by HUNNY on 2015-12-03.
 */
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    CharSequence title;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ListView navList;
    String[] navItems ={"위험알리미", "경찰서 찾기", "위치 알리미", "귀가 알리미", "홈"};
    ArrayAdapter<String> adapterDrawerList;

    FindPolice policeFr;
    FragmentHome homeFr;
    SirenMaker sirenFr;
    //////////////////////
    long lastTime;
    float speed;
    float lastX;
    float lastY;
    float lastZ;
    float x, y, z;

    private static final int SHAKE_THRESHOLD = 8000;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    SensorManager sensorManager;
    Sensor accelerormeterSensor;
    MediaPlayer mp;
    /////////////////////////////////////////
    double latitude, longitude;
    private GpsInfo gps;
    ///////////////////////////////////////////
    String savedphoneNum1;
    String savedphoneNum2;
    String getphoneNum;
    String myPhoneNum;
    //////////////////////////////////////////

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

        navList = (ListView)findViewById(R.id.nav_list);
        adapterDrawerList=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,navItems);
        navList.setAdapter(adapterDrawerList);
        navList.setOnItemClickListener(new DrawerItemClickListener());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        gps = new GpsInfo(this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
           latitude = gps.getLatitude();
           longitude = gps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*내 번호 받아오기*/
        TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        myPhoneNum = telManager.getLine1Number();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            switch (position){
                case 0:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, sirenFr).commit();
                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, policeFr).commit();
                    break;
                case 2:
                    Intent locationIntent = new Intent(MainActivity.this, LocationSMS.class);
                    startActivity(locationIntent);
                    break;
                case 3:
                    Intent notifyIntent = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(notifyIntent);
                    break;
                case 4:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_place, homeFr).commit();
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
        if (accelerormeterSensor != null) {
            sensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_GAME);
            mp = MediaPlayer.create(this, R.raw.police);
        }
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
                    load();
                    String message = latitude + "\n" + longitude;
                    sendSMS(getphoneNum, message);
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

     public void sendSMS(String phoneNumber, String message)
    {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_SENT"), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_DELIVERED"), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public void setting(){
        final LinearLayout linear = (LinearLayout)
                View.inflate(this, R.layout.dialog_setting, null);

        new AlertDialog.Builder(this)
                .setTitle("두 가지 번호를 '-'없이 입력")
                .setView(linear)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        TextView text1 = (TextView) linear.findViewById(R.id.textView);
                        EditText senderNum = (EditText) linear.findViewById(R.id.input_sender);
                        TextView text2 = (TextView) linear.findViewById(R.id.textView2);
                        EditText receiverNum = (EditText) linear.findViewById(R.id.input_receiver);
                        savedphoneNum1 = senderNum.getText().toString();
                        savedphoneNum2 = receiverNum.getText().toString();
                        save();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    private void save(){
        try {
            ParseACL defaultACL = new ParseACL();
            defaultACL.setPublicReadAccess(true); // 해당 데이터에 대한 접근 권한을 모든 사람이 읽을 수 있도록 합니다.

            ParseObject data = new ParseObject("Emergency"); // object 생성 및 추가될 class 이름 입력
            data.put("sender", savedphoneNum1); // 데이터 입력
            data.put("receiver", savedphoneNum2); // 데이터 입력
            data.setACL(defaultACL); // object에 ACL set
            data.save(); // parse.com에 해당 object save

            Toast.makeText(this, "입력이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void load(){
        try {
            ArrayList<ParseObject> datas = new ArrayList<ParseObject>(); // parse.com에서 읽어온 object들을 저장할 List
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Emergency"); // 서버에 mydatas class 데이터 요청
            query.whereEqualTo("sender", myPhoneNum); // my_type이 1인 object만 읽어옴. 해당 함수 호출하지 않으면 class의 모든 데이터를 읽어옴.
            datas.addAll(query.find()); // 읽어온 데이터를 List에 저장
            StringBuffer str = new StringBuffer();
            for (ParseObject object : datas) {
                str.append(object.get("receiver"));
            }
            datas.clear();
            getphoneNum = str.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
