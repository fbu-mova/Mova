package com.example.mova.components;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalPromptComponent extends Component<Post> {
    protected DelegatedResultActivity activity;
    protected ViewHolder holder;

    public JournalPromptComponent(Post item) {
        super(item);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.component_journal_prompt, parent, false);
        holder = new ViewHolder(view);
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public void render() {
        holder.bCompose.setOnClickListener((view) -> {
            Mood.Status mood = holder.moodSelector.getSelectedItem();
            Intent intent = new Intent(activity, JournalComposeActivity.class);
            intent.putExtra(JournalComposeActivity.KEY_MOOD, mood.toString());

            activity.startActivityForDelegatedResult(intent, JournalComposeActivity.COMPOSE_REQUEST_CODE,
                (int requestCode, int resultCode, Intent data) -> {
                    if (requestCode == JournalComposeActivity.COMPOSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                        Post journalEntry = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST);
                        ArrayList<Tag> tags = (ArrayList<Tag>) data.getSerializableExtra(JournalComposeActivity.KEY_COMPOSED_POST_TAGS);
                        ((User) User.getCurrentUser()).postJournalEntry(journalEntry, tags, (entry) -> {});
                    }
                });
        });
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvGreeting)   public TextView tvGreeting;
        @BindView(R.id.moodSelector) public Mood.SelectorLayout moodSelector;
        @BindView(R.id.bCompose)     public Button bCompose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
