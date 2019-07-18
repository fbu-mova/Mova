package com.example.mova.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.utils.LocationUtils;
import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.utils.TimeUtils;
import com.example.mova.model.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.ViewHolder> {

    private List<Post> entries;
    private Activity activity;

    public JournalEntryAdapter(Activity activity, List<Post> entries) {
        this.activity = activity;
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_journal_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post entry = entries.get(position);
        holder.tvTime.setText(TimeUtils.toTimeString(entry.getCreatedAt()));
        holder.tvBody.setText(entry.getBody());
        holder.tvLocation.setText(LocationUtils.makeLocationText(activity, entry.getLocation()));
        Mood.Status mood = entry.getMood();
        holder.tvMood.setText((mood == null) ? "" : mood.toString()); // TODO: Hide mood image, etc.
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void changeSource(List<Post> newEntriesSource) {
        this.entries = newEntriesSource;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTime)     public TextView tvTime;
        @BindView(R.id.tvLocation) public TextView tvLocation;
        @BindView(R.id.ivMood)     public ImageView ivMood;
        @BindView(R.id.tvMood)     public TextView tvMood;
        @BindView(R.id.tvBody)     public TextView tvBody;
        @BindView(R.id.flMedia)    public FrameLayout flMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
