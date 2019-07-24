package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Post;
import com.example.mova.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalMemoryComponent extends Component {

    private ComponentManager componentManager;

    private Post entry;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;

    private static int MAX_EXCERPT_CHAR_LENGTH = 500;

    public JournalMemoryComponent(Post entry) {
        this.entry = entry;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_journal_memory, parent, attachToRoot);
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
        return "JournalMemoryComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
        // TODO: Update color based on mood
        // TODO: Set prompt based on mood (and eventually, other mood data patterns)
        // TODO: On button click, open reply to post display with media
        // TODO: On tap on excerpt, go to that journal entry

        holder.tvMood.setText(entry.getMood().toString().toLowerCase());
        holder.tvExcerpt.setText(truncateEntry(entry));
        holder.tvDate.setText(TimeUtils.toLongRelativeDateString(entry.getCreatedAt()));
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.flAccentColor) public FrameLayout flAccentColor;
        @BindView(R.id.tvDate)        public TextView tvDate;
        @BindView(R.id.tvMood)        public TextView tvMood;
        @BindView(R.id.tvExcerpt)     public TextView tvExcerpt;
        @BindView(R.id.tvPrompt)      public TextView tvPrompt;
        @BindView(R.id.bReflect)      public Button bReflect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static String truncateEntry(Post entry) {
        String out;
        String ellipsis = "...";
        if (entry.getBody().length() > MAX_EXCERPT_CHAR_LENGTH) {
            out = entry.getBody().substring(0, MAX_EXCERPT_CHAR_LENGTH - ellipsis.length()) + ellipsis;
        } else {
            out = entry.getBody();
        }
        return out;
    }
}
