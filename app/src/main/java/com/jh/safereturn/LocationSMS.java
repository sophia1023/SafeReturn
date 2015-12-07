package com.jh.safereturn;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
public class LocationSMS extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private PolylineOptions polylineOptions;
    LocationManager locationManager;
    String provider;
    LatLng latlng;
    double latitude, longitude;
    LatLng receiveLoc;
    String receivelat, receivelon;
    boolean locationTag = true;
    ArrayList<LatLng> arrayPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        int googlePlayServiceResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(LocationSMS.this);
        if( googlePlayServiceResult !=   ConnectionResult.SUCCESS){ //구글 플레이 서비스를 활용하지 못할경우 <계정이 연결이 안되어 있는 경우
            //실패
            GooglePlayServicesUtil.getErrorDialog(googlePlayServiceResult, this, 0, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    finish();
                }
            }).show();
        }else { //구글 플레이가 활성화 된 경우
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);

            if (provider == null) {  //위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
                new AlertDialog.Builder(LocationSMS.this)
                        .setTitle("위치서비스 동의")
                        .setNeutralButton("이동", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                        .show();
            } else {   //위치 정보 설정이 되어 있으면 현재위치를 받아옵니다
                locationManager.requestLocationUpdates(provider,1,1, LocationSMS.this); //기본 위치 값 설정
                setUpMapIfNeeded(); //Map ReDraw
            }
            setMyLocation(); //내위치 정하는 함수
        }

        // BitmapDescriptorFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());
        arrayPoints = new ArrayList<LatLng>();
        this.init();
        ((Button)findViewById(R.id.sendLocation)).setOnClickListener(mClickListener);
        ((Button)findViewById(R.id.receiveLocation)).setOnClickListener(mClickListener);
    }

    public void setMyLocation(){
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

   public GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            latlng = new LatLng(location.getLatitude(), location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    };

    public void init() {
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(LocationSMS.this);
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        registerReceiver(mReceiverBR, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));
    }

    /* public void onPause() {
         super.onPause();
         unregisterReceiver(mReceiverBR);
     }
 */
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
                }
                stz1 = new StringTokenizer(result,"\n");
                receivelat = stz1.nextToken();
                receivelon = stz1.nextToken();
            }
        }
    };

    public void SmsLatLon() {
        double lon = Double.parseDouble(receivelon);
        double lat = Double.parseDouble(receivelat);
        receiveLoc = new LatLng(lat,lon);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(receiveLoc, 15));
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(receiveLoc)
                .title("친구의 위치")
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
                    SmsLatLon();
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

    @Override
    public void onLocationChanged(Location location) {
        if(locationTag){//한번만 위치를 가져오기 위해서 tag를 주었습니다
            Log.d("myLog", "onLocationChanged: !!" + "onLocationChanged!!");
            double lat =  location.getLatitude();
            double lng = location.getLongitude();

            Toast.makeText(LocationSMS.this, "위도  : " + lat + " 경도: " + lng, Toast.LENGTH_SHORT).show();
            locationTag=false;
        }

    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
