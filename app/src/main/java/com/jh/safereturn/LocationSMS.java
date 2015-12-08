package com.jh.safereturn;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by HUNNY on 2015-12-03.
 */
public class LocationSMS extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GpsInfo gps;
    private PolylineOptions polylineOptions;
    double latitude, longitude;
    LatLng receiveLoc;
    String receivelat, receivelon = "";
    ArrayList<LatLng> arrayPoints;
    String sender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onResume();

        gps = new GpsInfo(this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }

        // BitmapDescriptorFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());
        arrayPoints = new ArrayList<LatLng>();
        this.init();
        ((Button)findViewById(R.id.sendLocation)).setOnClickListener(mClickListener);
        ((Button)findViewById(R.id.receiveLocation)).setOnClickListener(mClickListener);
    }

    public void init() {
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiverBR, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));
    }

    BroadcastReceiver mReceiverBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String result = "";
            StringTokenizer stz1;
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                for(int i = 0;i<pdus.length;i++) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte [])pdus[i]);
                    result = msg.getMessageBody() + "\n";
                    sender = msg.getOriginatingAddress();
                }
                stz1 = new StringTokenizer(result,"\n");
                receivelat = stz1.nextToken();
                receivelon = stz1.nextToken();
            }
        }
    };

    public void smsLatLon() {
        double lon = Double.parseDouble(receivelon);
        double lat = Double.parseDouble(receivelat);
        receiveLoc = new LatLng(lat,lon);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(receiveLoc, 15));
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(receiveLoc)
                .title(sender)
                .snippet("위치" + (arrayPoints.size()+1)));

        arrayPoints.add(receiveLoc);
        if(mMap != null && arrayPoints.size() > 1) {
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
            polylineOptions.addAll(arrayPoints);
            mMap.addPolyline(polylineOptions);
        }
    }

    Button.OnClickListener mClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sendLocation:
                    final LinearLayout linear = (LinearLayout)
                            View.inflate(LocationSMS.this, R.layout.dialog, null);

                    new AlertDialog.Builder(LocationSMS.this)
                            .setTitle("수신자 번호를 '-'없이 입력")
                            .setView(linear)
                            .setPositiveButton("내 위치 전송", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    EditText number = (EditText)linear.findViewById(R.id.input_number);
                                    String phoneNo = number.getText().toString();
                                    String cordinate = Double.toString(latitude) + "\n" + Double.toString(longitude);

                                    if (phoneNo.length()>0 && cordinate.length()>0)
                                        sendSMS(phoneNo, cordinate);
                                    else
                                        Toast.makeText(getBaseContext(),
                                                "Please enter both phone number and message.",
                                                Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();
                    break;
                case R.id.receiveLocation:
                    smsLatLon();
                    break;
            }
        }
    };

   public void sendSMS(String phoneNumber, String message)
    {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_SENT"), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_DELIVERED"), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    public void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
