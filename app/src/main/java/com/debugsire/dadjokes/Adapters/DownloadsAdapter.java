package com.debugsire.dadjokes.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debugsire.dadjokes.Model.DownloadsModel;
import com.debugsire.dadjokes.R;

import java.util.ArrayList;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {
    private static final String TAG = "DownloadsAdapter -- ";

    ArrayList<DownloadsModel> models;
    LayoutInflater mInflater;

    public DownloadsAdapter(ArrayList<DownloadsModel> models, LayoutInflater mInflater) {
        this.models = models;
        this.mInflater = mInflater;
    }

    @NonNull
    @Override
    public DownloadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_download, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsAdapter.ViewHolder holder, final int position) {
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
