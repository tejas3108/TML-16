package siesgst.edu.in.tml16.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GcmListenerService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;

import siesgst.edu.in.tml16.HomeActivity;
import siesgst.edu.in.tml16.R;

public class TMLGCMListenerService extends GcmListenerService {

    private static final String TAG = "TMLGcmListenerService";
    Resources res;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        res = getApplicationContext().getResources();

        String title = data.getString("title");
        String body = data.getString("body");

        sendNotification(title, body);
    }

    public void sendNotification(String title, String body) {
        Intent intent = new Intent(this, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Bitmap imageBitmap;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notify)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLights(0xFFC107, 500, 3000)
                .setVibrate(new long[]{200, 200, 200, 200})
                .setSound(defaultRingtone)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int id = (int) Calendar.getInstance().getTimeInMillis();

        notificationManager.notify( id /* ID of notification */, notificationBuilder.build());
        /*try {
            imageBitmap = Picasso.with(getApplicationContext()).load("https://fbcdn-sphotos-g-a.akamaihd.net/hphotos-ak-xtl1/v/t1.0-9/s720x720/12717827_1079182818808498_6987844781615082742_n.jpg?oh=097818aee5121b8a37c03468b3a9cf0b&oe=57618CA9&__gda__=1461890998_31f21230ab4034884448e156f4ddd784").get();
            bigPictureStyle.bigPicture(imageBitmap);
            bigPictureStyle.setSummaryText(body);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, notificationBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}