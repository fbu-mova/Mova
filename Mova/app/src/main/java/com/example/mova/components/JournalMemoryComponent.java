package com.example.mova.components;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.model.Mood;
import com.example.mova.utils.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.JournalComposeActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TextUtils;
import com.example.mova.utils.TimeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Random;

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

        this.holder.tvMood.setText(entry.getMood().toString().toLowerCase());
        this.holder.tvExcerpt.setText(TextUtils.ellipsize(entry.getBody(), MAX_EXCERPT_CHAR_LENGTH));
        this.holder.tvDate.setText(TimeUtils.toLongRelativeDateString(entry.getCreatedAt()));

        // Update color based on mood
        int color = Mood.getColor(entry.getMood());
        this.holder.tvMood.setTextColor(color);
        this.holder.tvQuotes.setTextColor(color);
        this.holder.bReflect.setBackgroundTintList(ColorStateList.valueOf(color));
        this.holder.tvPrompt.setText(getPrompt());

        configureComposeClick();
    }

    @Override
    protected void onDestroy() {

    }

    private String getPrompt() {
        // TODO: Write prompt based on mood (and eventually, other mood data patterns)
        // For now, choose one of a few random prompts
        switch (new Random().nextInt(5)) {
            case 0:  return "How have things changed?";
            case 1:  return "What made you feel that way?";
            case 2:  return "Write a note to your past self.";
            case 3:  return "How do you remember that day now?";
            default: return "What made that moment stand out?";
        }
    }

    private void configureComposeClick() {
        holder.bReflect.setOnClickListener((view) -> {
//            Media media = new Media();
//            media.setContent(holder.tvPrompt.getText().toString());
//            Intent intent = new Intent(getActivity(), JournalComposeActivity.class);
//            intent.putExtra(JournalComposeActivity.KEY_MEDIA, media);

//            getActivity().startActivityForDelegatedResult(intent, COMPOSE_REQUEST_CODE, (int requestCode, int resultCode, Intent data) -> {
//                if (requestCode == COMPOSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//                    Post reflection = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST);
//                    ArrayList<Tag> tags = (ArrayList<Tag>) data.getSerializableExtra(JournalComposeActivity.KEY_COMPOSED_POST_TAGS);
//                    Media outMedia = data.getParcelableExtra(JournalComposeActivity.KEY_COMPOSED_POST_MEDIA);
//
//                    // TODO: Switch to different activity rather than coercing JournalComposeActivity's results to a normal post
//                    reflection.removeMood();
//                    reflection.setParent(entry);
//
//                    AsyncUtils.ItemCallback<Post> saveOnParent = (postFromCb) ->
//                            entry.relComments.add(reflection, (postFromCb2) -> {
//                                onPost.call(reflection);
//                            });
//
//                    PostConfig config = new PostConfig(reflection);
//                    config.tags = tags;
//                    config.media = outMedia;
//
//                    config.post.savePost(saveOnParent);
//                }
//            });

            PostConfig config = new PostConfig();
            config.media = new Media(holder.tvPrompt.getText().toString());
            config.isPersonal = true;

            new ComposePostDialog.Builder(getActivity())
                    .setConfig(config)
                    .setOnPost((post) -> {
                        BottomNavigationView menu = getActivity().findViewById(R.id.bottom_navigation_personal);
                        PersonalFragment.journalDate = entry.getCreatedAt();
                        menu.setSelectedItemId(R.id.action_journal);
                    })
                    .show(holder.getView());
        });
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvQuotes)      public TextView tvQuotes;
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
}
