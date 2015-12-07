package com.jh.safereturn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Jiyoung on 2015-12-07.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_SHORT).show();

        // TODO Auto-generated method stub
        Intent contentIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent i = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        CharSequence from = "SafeReturn";
        CharSequence message = "집에 갈 시간이야!! 통금엄수!!";

        Notification notify = new Notification(R.drawable.gohome, "!!!!!!!",
                System.currentTimeMillis());
        // notifi.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
        // notifi.flags = Notification.FLAG_INSISTENT;
        notify.setLatestEventInfo(context, from, message, i);
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(7777, notify);

    }
}
