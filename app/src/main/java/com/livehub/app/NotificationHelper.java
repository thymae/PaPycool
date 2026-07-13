package com.livehub.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    public static final String CHANNEL_ID = "livehub_channel";

    public static void createChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Lives et nouveautés", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Rappels de lives et nouveaux contenus");
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    public static void notify(Context ctx, int id, String titre, String texte) {
        createChannel(ctx);
        Intent i = new Intent(ctx, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle(titre)
                .setContentText(texte)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texte))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        try {
            androidx.core.app.NotificationManagerCompat.from(ctx).notify(id, b.build());
        } catch (SecurityException ignored) { }
    }
}
