package com.debugsire.dadjokes.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debugsire.dadjokes.Model.FavoritesModel;
import com.debugsire.dadjokes.R;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    ArrayList<FavoritesModel> models;
    LayoutInflater mInflater;

    public FavoritesAdapter(ArrayList<FavoritesModel> models, LayoutInflater mInflater) {
        this.models = models;
        this.mInflater = mInflater;
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_favorites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, final int position) {
        holder.joke.setText(models.get(position).getJoke());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView joke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            joke = itemView.findViewById(R.id.tv_joke);
        }

    }
}
