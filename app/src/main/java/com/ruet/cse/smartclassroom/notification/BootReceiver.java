package com.ruet.cse.smartclassroom.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ReminderScheduler.scheduleTodayAndTomorrowReminders(context.getApplicationContext());
        }
    }
}
