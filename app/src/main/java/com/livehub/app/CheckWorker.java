package com.livehub.app;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Vérifie périodiquement (via WorkManager) :
 * 1. Si un live démarre dans moins d'une heure -> notification de rappel
 * 2. Si le contenu a changé depuis la dernière vérification -> notification "nouveau contenu"
 */
public class CheckWorker extends Worker {

    public CheckWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("livehub", Context.MODE_PRIVATE);
        try {
            String raw = ContentRepository.fetchRaw(MainActivity.CONTENT_URL);
            JSONObject data = new JSONObject(raw);

            // 1. Nouveau contenu ? (comparaison d'empreinte)
            String signature = String.valueOf(raw.hashCode());
            String previous = prefs.getString("signature", null);
            if (previous != null && !previous.equals(signature)) {
                String annonce = data.optString("annonce", "Du nouveau contenu est disponible !");
                NotificationHelper.notify(ctx, 1001, "Nouveauté \uD83C\uDF89", annonce);
            }
            prefs.edit().putString("signature", signature).apply();

            // 2. Live imminent ?
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);
            JSONArray planning = data.optJSONArray("planning");
            long now = System.currentTimeMillis();
            if (planning != null) {
                for (int i = 0; i < planning.length(); i++) {
                    JSONObject live = planning.getJSONObject(i);
                    String dateStr = live.optString("date", "");
                    Date d = fmt.parse(dateStr);
                    if (d == null) continue;
                    long delta = d.getTime() - now;
                    String key = "notif_" + dateStr;
                    if (delta > 0 && delta <= 60 * 60 * 1000 && !prefs.getBoolean(key, false)) {
                        String heure = dateStr.length() >= 16 ? dateStr.substring(11) : "";
                        NotificationHelper.notify(ctx, 2000 + i,
                                "\uD83D\uDD34 Live bientôt !",
                                live.optString("titre", "Live") + " à " + heure + " — Sois prêt !");
                        prefs.edit().putBoolean(key, true).apply();
                    }
                }
            }
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
