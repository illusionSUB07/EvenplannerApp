package com.example.Eveplanner;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * A {@link BroadcastReceiver} subclass that will handle the broadcast sent from the {@link AddReminderActivity}.
 * Once the broadcast is received, a notification will be built and displayed.
 */
public class ReminderReceiver extends BroadcastReceiver {

    // Channel ID for the notification. This is used for Android Oreo and higher.
    private static final String CHANNEL_ID = "REMINDER_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderText = intent.getStringExtra("reminderText");

        // Create a pending intent that will open the app when the notification is clicked.
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline)
                .setContentTitle("Reminder")
                .setContentText(reminderText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel.
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Reminder Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel for reminder notifications");

            // Register the channel with the system.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Display the notification.
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
