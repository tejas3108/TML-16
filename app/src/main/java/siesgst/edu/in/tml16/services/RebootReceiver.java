package siesgst.edu.in.tml16.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class RebootReceiver extends BroadcastReceiver {

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public RebootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent serviceIntent = new Intent(context, ServerCommunicationService.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, serviceIntent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 3);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
        }
    }
}
