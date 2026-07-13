package com.livehub.app;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // ============================================================
    // ⚠️ MODIFIE CETTE LIGNE avec ton pseudo et ton repo GitHub :
    public static final String CONTENT_URL =
            "https://raw.githubusercontent.com/thymae/PaPycool/main/content.json";
    // ============================================================

    private final List<Item> planning = new ArrayList<>();
    private final List<Item> moments = new ArrayList<>();
    private final List<Item> tempsForts = new ArrayList<>();
    private final List<Item> resume = new ArrayList<>();
    private final List<Item> affiches = new ArrayList<>();

    private CardAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView headerTitle, headerSub;
    private Button btnTikTok;
    private String tiktokUrl = "";
    private android.widget.ScrollView accueilScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerTitle = findViewById(R.id.headerTitle);
        headerSub = findViewById(R.id.headerSub);
        btnTikTok = findViewById(R.id.btnTikTok);
        swipe = findViewById(R.id.swipe);
        accueilScroll = findViewById(R.id.accueilScroll);
        RecyclerView recycler = findViewById(R.id.recycler);
        BottomNavigationView nav = findViewById(R.id.bottomNav);

        adapter = new CardAdapter(affiches);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnTikTok.setOnClickListener(v -> {
            if (!tiktokUrl.isEmpty())
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tiktokUrl)));
        });

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tab_accueil) {
                accueilScroll.setVisibility(android.view.View.VISIBLE);
                swipe.setVisibility(android.view.View.GONE);
            } else {
                accueilScroll.setVisibility(android.view.View.GONE);
                swipe.setVisibility(android.view.View.VISIBLE);
                if (id == R.id.tab_planning) show(planning);
                else if (id == R.id.tab_moments) show(moments);
                else if (id == R.id.tab_forts) show(tempsForts);
                else if (id == R.id.tab_resume) show(resume);
            }
            return true;
        });

        swipe.setOnRefreshListener(this::charger);

        // Permission notifications (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        // Vérification en arrière-plan toutes les 30 minutes
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(
                CheckWorker.class, 30, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "livehub_check", ExistingPeriodicWorkPolicy.KEEP, work);

        charger();
    }

    private void show(List<Item> source) {
        affiches.clear();
        affiches.addAll(source);
        adapter.notifyDataSetChanged();
    }

    private void charger() {
        swipe.setRefreshing(true);
        new Thread(() -> {
            try {
                String raw = ContentRepository.fetchRaw(CONTENT_URL);
                JSONObject data = new JSONObject(raw);
                runOnUiThread(() -> {
                    remplir(data);
                    swipe.setRefreshing(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipe.setRefreshing(false);
                    Toast.makeText(this,
                            "Impossible de charger le contenu. Vérifie ta connexion ou l'URL du fichier.",
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void remplir(JSONObject data) {
        JSONObject createur = data.optJSONObject("createur");
        if (createur != null) {
            headerTitle.setText(createur.optString("pseudo", "LiveHub"));
            headerSub.setText(createur.optString("description", ""));
            tiktokUrl = createur.optString("tiktok", "");
        }

        planning.clear();
        JSONArray p = data.optJSONArray("planning");
        if (p != null) for (int i = 0; i < p.length(); i++) {
            JSONObject o = p.optJSONObject(i);
            planning.add(new Item("\uD83D\uDD34 LIVE",
                    o.optString("titre", "Live"),
                    o.optString("date", "") + (o.has("theme") ? " • " + o.optString("theme") : ""),
                    ""));
        }

        moments.clear();
        remplirListe(data.optJSONArray("moments"), moments, "\u2B50 MOMENT");
        tempsForts.clear();
        remplirListe(data.optJSONArray("temps_forts"), tempsForts, "\uD83D\uDD25 TEMPS FORT");
        resume.clear();
        remplirListe(data.optJSONArray("resume"), resume, "\uD83D\uDCF9 RÉSUMÉ");

        show(planning); // prépare la liste, l'affichage dépend de l'onglet actif
    }

    private void remplirListe(JSONArray arr, List<Item> cible, String badge) {
        if (arr == null) return;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            cible.add(new Item(badge,
                    o.optString("titre", ""),
                    o.optString("date", ""),
                    o.optString("url", "")));
        }
    }
}
