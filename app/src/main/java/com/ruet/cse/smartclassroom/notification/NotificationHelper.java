package com.ruet.cse.smartclassroom.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.ruet.cse.smartclassroom.R;
public class NotificationHelper {

    public static final String CHANNEL_ID = "class_reminders";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Class Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Reminders for upcoming classes");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
    public static void showReminder(Context context, String title, String courseLine, int notificationId) {
        createChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_routine)
                .setContentTitle(title)
                .setContentText(courseLine)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(courseLine))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }
}
