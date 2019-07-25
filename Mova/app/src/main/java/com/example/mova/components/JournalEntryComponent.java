package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.TextUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseQuery;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalEntryComponent extends Component {

    private Post entry;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    public JournalEntryComponent(Post entry) {
        this.entry = entry;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.item_journal_entry, parent, attachToRoot);
        holder = new ViewHolder(view);
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public String getName() {
        return "JEntry_" + entry.getObjectId();
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        holder.tvTime.setText(TimeUtils.toTimeString(entry.getCreatedAt()));
        holder.tvBody.setText(entry.getBody());
        holder.tvLocation.setText(LocationUtils.makeLocationText(activity, entry.getLocation()));

        Mood.Status mood = entry.getMood();
        holder.tvMood.setText((mood == null) ? "" : mood.toString()); // TODO: Hide mood image, etc.

        Media media = entry.getMedia();
        Component mediaComponent = (media == null) ? null : media.makeComponent();
        holder.clMedia.clear();
        if (mediaComponent != null) {
            holder.clMedia.setMarginTop(16).setMarginBottom(16);
            holder.clMedia.inflateComponent(activity, mediaComponent);
        }

        ParseQuery<Tag> tagQuery = entry.relTags.getQuery();
        tagQuery.findInBackground((tags, e) -> {
            if (e != null) {
                Log.e("JournalEntryComponent", "Failed to load tags on journal entry", e);
                holder.tvTags.setText("Failed to load");
            } else {
                TextUtils.writeCommaSeparated(tags, "No tags", holder.tvTags, (tag) -> tag.getName());
            }
        });
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvTime)     public TextView tvTime;
        @BindView(R.id.tvLocation) public TextView tvLocation;
        @BindView(R.id.ivMood)     public ImageView ivMood;
        @BindView(R.id.tvMood)     public TextView tvMood;
        @BindView(R.id.tvBody)     public TextView tvBody;
        @BindView(R.id.clMedia)    public ComponentLayout clMedia;
        @BindView(R.id.tvTags)     public TextView tvTags;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
