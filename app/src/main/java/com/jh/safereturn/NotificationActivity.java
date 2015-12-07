package com.jh.safereturn;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by Jiyoung on 2015-12-07.
 */
public class NotificationActivity extends Activity {
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    DatePicker mDate;
    //시작 설정 클래스
    TimePicker mTime;


    NotificationManager mNotification;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //통지 매니저를 취득
        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();

        Button b = (Button)findViewById(R.id.btnAlaram);
        b.setOnClickListener (new View.OnClickListener() {

            public void onClick (View v) {

                mTime = (TimePicker)findViewById(R.id.timePicker);

                setAlarm();


            }
        });

        //일시 설정 클래스로 현재 시각을 설정
        mDate = (DatePicker)findViewById(R.id.datePicker);
        // mDate.init(mCalendar.YEAR, mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

    }

    //알람의 설정
    private void setAlarm() {
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(NotificationActivity.this, AlarmReceiver.class);
        // PendingIntent pIntent= PendingIntent.getService(CB_Home.this, 0, new Intent(CB_Home.this, AlarmReceive.class),0);
        Calendar calendar = Calendar.getInstance();
        int hour = mTime.getCurrentHour();
        int minute = mTime.getCurrentMinute();

        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.SECOND,0);
        mCalendar.set(Calendar.MILLISECOND,0);
        mCalendar.set(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(),hour,minute);

        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());


    }

    //알람의 설정 시각에 발생하는 인텐트 작성
    private PendingIntent pendingIntent() {
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        return pi;
    }
}