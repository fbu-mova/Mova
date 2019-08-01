package com.example.mova.components;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalMemoryComponent extends Component {

    private ComponentManager componentManager;

    private Post entry;
    private AsyncUtils.ItemCallback<Post> onPost;

    private ViewHolder holder;

    private static int MAX_EXCERPT_CHAR_LENGTH = 500;

    private static final int COMPOSE_REQUEST_CODE = 32;

    public JournalMemoryComponent(Post entry) {
        this.entry = entry;
        this.onPost = (post) -> {};
    }

    public JournalMemoryComponent(Post entry, AsyncUtils.ItemCallback<Post> onPost) {
        this.entry = entry;
        this.onPost = onPost;
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
        return "JournalMemoryComponent";
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

        // TODO: Update color based on mood
        // TODO: Set prompt based on mood (and eventually, other mood data patterns)
        // TODO: On tap on excerpt, go to that journal entry

        this.holder.tvMood.setText(entry.getMood().toString().toLowerCase());
        this.holder.tvExcerpt.setText(truncateEntry(entry));
        this.holder.tvDate.setText(TimeUtils.toLongRelativeDateString(entry.getCreatedAt()));

        configureComposeClick();
    }

    @Override
    protected void onDestroy() {

    }

    private void configureComposeClick() {
        holder.bReflect.setOnClickListener((view) -> {
            Media media = new Media();
            media.setContent(holder.tvPrompt.getText().toString());
            Intent intent = new Intent(getActivity(), JournalComposeActivity.class);
            intent.putExtra(JournalComposeActivity.KEY_MEDIA, media);
            getActivity().startActivityForDelegatedResult(intent, COMPOSE_REQUEST_CODE, (int requestCode, int resultCode, Intent data) -> {
                if (requestCode == COMPOSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    Post reflection = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST);
                    ArrayList<Tag> tags = (ArrayList<Tag>) data.getSerializableExtra(JournalComposeActivity.KEY_COMPOSED_POST_TAGS);
                    Media outMedia = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST_MEDIA);

                    // TODO: Switch to different activity rather than coercing JournalComposeActivity's results to a normal post
                    reflection.removeMood();
                    reflection.setParent(entry);

                    AsyncUtils.ItemCallback<Post> saveOnParent = (postFromCb) ->
                            entry.relComments.add(reflection, (postFromCb2) -> {
                                onPost.call(reflection);
                            });

                    PostConfig config = new PostConfig(reflection);
                    config.tags = tags;
                    config.media = outMedia;

                    config.post.savePost(saveOnParent);
                }
            });
        });
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

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_journal_memory, parent, attachToRoot);
            return new ViewHolder(view);
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
