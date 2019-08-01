package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
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
    private DataComponentAdapter<Post> commentAdapter;

    private ViewHolder holder;
    private ComponentManager manager;

    public JournalEntryComponent(Post entry) {
        this.entry = entry;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
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
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        displayBasicInfo();
        displayMood();
        displayMedia();
        displayTags();
        displayComments();
    }

    @Override
    protected void onDestroy() {

    }

    private void displayBasicInfo() {
        holder.tvTime.setText(TimeUtils.toTimeString(entry.getCreatedAt()));
        holder.tvBody.setText(entry.getBody());
        holder.tvLocation.setText(LocationUtils.makeLocationText(getActivity(), entry.getLocation()));
    }

    private void displayMood() {
        Mood.Status mood = entry.getMood();
        holder.tvMood.setText((mood == null) ? "" : mood.toString());
        // TODO: Hide mood image, etc.
    }

    private void displayMedia() {
        Media media = entry.getMedia();
        Component mediaComponent = (media == null) ? null : media.makeComponent();
        holder.clMedia.clear();
        if (mediaComponent != null) {
            holder.clMedia.setMarginTop(16).setMarginBottom(16);
            holder.clMedia.inflateComponent(getActivity(), mediaComponent);
        }
    }

    private void displayTags() {
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

    private void hideComments() {
        holder.rvComments.setVisibility(View.GONE);
    }

    private void hideCommentsToggle() {
        holder.sComments.setVisibility(View.GONE);
    }

    private void showComments() {
        holder.rvComments.setVisibility(View.VISIBLE);
    }

    private void showCommentsToggle() {
        holder.sComments.setVisibility(View.VISIBLE);
    }

    private void displayComments() {
        hideCommentsToggle();
        hideComments();
        ParseQuery<Post> commentsQuery = entry.relComments.getQuery();
        commentsQuery.include("author");
        commentsQuery.include("media");
        commentsQuery.findInBackground((comments, e) -> {
            if (e != null) {
                Log.e("JournalEntryComponent", "Failed to load comments on journal entry", e);
            } else {
                commentAdapter = new DataComponentAdapter<Post>(getActivity(), comments) {
                    @Override
                    protected Component makeComponent(Post item, Component.ViewHolder holder) {
                        return new JournalResponseComponent(item);
                    }

                    @Override
                    protected Component.Inflater makeInflater(Post item) {
                        return new JournalResponseComponent.Inflater();
                    }
                };

                holder.rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
                holder.rvComments.setAdapter(commentAdapter);

                if (comments.size() > 0) {
                    showCommentsToggle();
                    holder.sComments.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                        if (isChecked) showComments();
                        else hideComments();
                    });
                }
            }
        });
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvTime)     public TextView tvTime;
        @BindView(R.id.tvLocation) public TextView tvLocation;
        @BindView(R.id.tvBody)     public TextView tvBody;

        @BindView(R.id.ivMood)     public ImageView ivMood;
        @BindView(R.id.tvMood)     public TextView tvMood;

        @BindView(R.id.clMedia)    public ComponentLayout clMedia;
        @BindView(R.id.tvTags)     public TextView tvTags;

        @BindView(R.id.sComments)  public Switch sComments;
        @BindView(R.id.rvComments) public RecyclerView rvComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_journal_entry, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
