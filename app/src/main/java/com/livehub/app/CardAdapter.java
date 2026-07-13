package com.livehub.app;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.VH> {

    private final List<Item> items;

    public CardAdapter(List<Item> items) { this.items = items; }

    static class VH extends RecyclerView.ViewHolder {
        TextView badge, titre, sousTitre;
        Button btn;
        VH(View v) {
            super(v);
            badge = v.findViewById(R.id.cardBadge);
            titre = v.findViewById(R.id.cardTitle);
            sousTitre = v.findViewById(R.id.cardSub);
            btn = v.findViewById(R.id.cardBtn);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Item it = items.get(pos);
        h.badge.setText(it.badge);
        h.titre.setText(it.titre);
        h.sousTitre.setText(it.sousTitre);
        if (it.url == null || it.url.isEmpty()) {
            h.btn.setVisibility(View.GONE);
        } else {
            h.btn.setVisibility(View.VISIBLE);
            h.btn.setOnClickListener(v ->
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(it.url))));
        }
    }

    @Override
    public int getItemCount() { return items.size(); }
}
