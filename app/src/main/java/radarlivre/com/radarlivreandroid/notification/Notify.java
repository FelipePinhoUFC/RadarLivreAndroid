package radarlivre.com.radarlivreandroid.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import radarlivre.com.radarlivreandroid.R;
import radarlivre.com.radarlivreandroid.application.activities.MainActivity;

/**
 * Created by felipe on 02/12/15.
 */
public class Notify {
    public void show(Context context, String ticker, String title, String text) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.mipmap.airplane);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(b);
        builder.setContentIntent(pi);

        builder.setSound(som, NotificationCompat.STREAM_DEFAULT);

        builder.setAutoCancel(true);

        Notification n = builder.build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.vibrate = new long[] {1000, 200, 1000};
        nm.notify(R.mipmap.ic_launcher, n);
    }
}
