package com.example.mova.components;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.Mood;
import com.example.mova.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalPromptComponent extends Component {

    protected ViewHolder holder;
    protected AsyncUtils.ItemCallback<Post> onPostJournalEntry;

    protected ComponentManager componentManager;

    public static final int COMPOSE_REQUEST_CODE = 31;

    public JournalPromptComponent() {
        onPostJournalEntry = (entry) -> {};
    }

    public JournalPromptComponent(AsyncUtils.ItemCallback<Post> onPostJournalEntry) {
        this.onPostJournalEntry = onPostJournalEntry;
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
        return "JournalPromptComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        this.holder.bCompose.setOnClickListener((view) -> {
            Mood.Status mood = this.holder.moodSelector.getSelectedItem();
            Intent intent = new Intent(getActivity(), JournalComposeActivity.class);
            intent.putExtra(JournalComposeActivity.KEY_MOOD, mood.toString());

            getActivity().startActivityForDelegatedResult(intent, COMPOSE_REQUEST_CODE,
                (int requestCode, int resultCode, Intent data) -> {
                    if (requestCode == COMPOSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                        Post journalEntry = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST);
                        ArrayList<Tag> tags = (ArrayList<Tag>) data.getSerializableExtra(JournalComposeActivity.KEY_COMPOSED_POST_TAGS);

                        PostConfig config = new PostConfig(journalEntry);
                        config.tags = tags;

                        User.getCurrentUser().postJournalEntry(config, onPostJournalEntry);
                    }
            });
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvGreeting)   public TextView tvGreeting;
        @BindView(R.id.moodSelector) public Mood.SelectorLayout moodSelector;
        @BindView(R.id.bCompose)     public Button bCompose;
        @BindView(R.id.card)         public CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_journal_prompt, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
