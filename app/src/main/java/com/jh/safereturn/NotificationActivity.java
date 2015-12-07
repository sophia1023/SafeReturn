package com.jh.safereturn;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * Created by Jiyoung on 2015-12-07.
 */
public class NotificationActivity extends Activity {
    private AlarmManager aManager;
    // 설정 일시
   // private GregorianCalendar mCalendar;
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
        aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        //mCalendar = new GregorianCalendar();
        mTime = (TimePicker)findViewById(R.id.timePicker);

        Button b = (Button)findViewById(R.id.btnAlaram);
        b.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                setAlarm();
            }
        });

        //일시 설정 클래스로 현재 시각을 설정
        //mDate = (DatePicker)findViewById(R.id.datePicker);
        // mDate.init(mCalendar.YEAR, mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
    }

    //알람의 설정
    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        int hour = mTime.getCurrentHour();
        int minute = mTime.getCurrentMinute();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.YEAR, calendar.MONTH,calendar.DAY_OF_MONTH,hour,minute);

        Intent intent = new Intent(NotificationActivity.this, MyReceiver.class);
        PendingIntent pIntent= PendingIntent.getBroadcast(NotificationActivity.this, 0, intent,0);
        aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        aManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
    }
}