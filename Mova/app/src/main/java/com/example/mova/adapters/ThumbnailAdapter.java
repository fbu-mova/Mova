package com.example.mova.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    private List<Object> entries; // either groups, events, or goals
    private Activity activity;

    ThumbnailAdapter(Activity activity, List<Object> entries) {
        this.entries = entries;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_thumbnail_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object entry = entries.get(position);

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvFromGroup) public TextView tvFromGroup; // only for events
        @BindView(R.id.ivPhoto)     public ImageView ivPhoto; // all
        @BindView(R.id.tvName)      public TextView tvName; // groups, goals
        @BindView(R.id.pbProgress)  public ProgressBar pbProgress; // goals
        @BindView(R.id.tvEventName) public TextView tvEventName; // events
        @BindView(R.id.tvLocation)  public TextView tvLocation; // events
        @BindView(R.id.tvDate)      public TextView tvDate; // events

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
