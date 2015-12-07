package com.jh.safereturn;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * Created by Jiyoung on 2015-12-07.
 */
public class NotificationActivity extends Activity {
    private AlarmManager aManager;
    //시작 설정 클래스
    TimePicker mTime;
    NotificationManager mNotification;
    EditText editText;
    static String setMsg;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //통지 매니저를 취득
        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //알람 매니저를 취득
        aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mTime = (TimePicker)findViewById(R.id.timePicker);
        editText = (EditText)findViewById(R.id.editText);

        Button b = (Button)findViewById(R.id.btnAlaram);
        b.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                setAlarm();
            }
        });
    }

    //알람의 설정
    private void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        int hour = mTime.getCurrentHour();
        int minute = mTime.getCurrentMinute();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        setMsg = editText.getText().toString();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(year,month,day,hour,minute);

        Intent intent = new Intent(NotificationActivity.this, MyReceiver.class);
        PendingIntent pIntent= PendingIntent.getBroadcast(NotificationActivity.this, 0, intent,0);
        aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        aManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pIntent);
       // intent.putExtra("sentMSG", setMsg);
       // startActivity(intent);
    }
}