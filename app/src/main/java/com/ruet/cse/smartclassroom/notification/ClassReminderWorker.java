package com.ruet.cse.smartclassroom.notification;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
public class ClassReminderWorker extends Worker {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public ClassReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(KEY_TITLE);
        String body = getInputData().getString(KEY_BODY);
        int notificationId = getInputData().getInt(KEY_NOTIFICATION_ID, (int) System.currentTimeMillis());

        NotificationHelper.showReminder(getApplicationContext(),
                title != null ? title : "Class Reminder",
                body != null ? body : "",
                notificationId);

        return Result.success();
    }
}
