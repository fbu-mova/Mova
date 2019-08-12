package com.example.mova.components;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.model.Mood;
import com.example.mova.views.EdgeFloatingActionButton;
import com.example.mova.views.MoodSelectorLayout;
import com.example.mova.views.PersonalSocialToggle;
import com.example.mova.utils.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Group;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.Wrapper;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ComposePostComponent extends Component {

    // TODO: Handle tags

    private PostConfig postConfig;
    private Group inGroup;

    private ViewHolder holder;

    private ComponentManager manager;
    private String managerMediaKey;

    public ComposePostComponent(String managerMediaKey) {
        this(managerMediaKey, new PostConfig());
    }

    public ComposePostComponent(String managerMediaKey, PostConfig postConfig) {
        this.managerMediaKey = managerMediaKey;
        this.postConfig = postConfig;
        this.inGroup = (postConfig.post == null) ? null : postConfig.post.getGroup();
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
        return "ComposePostDialog";
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

        this.holder.psToggle.setPersonal(postConfig.isPersonal);
        this.holder.psToggle.setOnToggle((isPersonal) -> {
            postConfig.isPersonal = isPersonal;
            displayPostType();
        });

        displayToReplyTo();
        displayPostType();
        displayMood();
        displayMedia();
        configureClickEvents();
    }

    @Override
    protected void onDestroy() {

    }

    public void setMedia(Media media) {
        postConfig.media = media;
        displayMedia();
    }

    private void displayToReplyTo() {
        // FIXME: Should post be fetched in background if needed?
        if (postConfig.postToReply == null) {
            holder.flReplyContent.setVisibility(View.GONE);
        } else {
            PostComponent.Config config = new PostComponent.Config(null, true, false, false);
            config.showMedia = false;
            config.allowCompose = false;
            PostComponent postComponent = new PostComponent(postConfig.postToReply, config);
            holder.flReplyContent.setVisibility(View.VISIBLE);
            holder.clPostToReply.setMargin(getActivity().getResources().getDimensionPixelOffset(R.dimen.innerMargin));
            holder.clPostToReply.inflateComponent(getActivity(), postComponent);
        }
    }

    private void displayMood() {
        if (postConfig.displayMoodSelector) {
            holder.llMood.setVisibility(View.VISIBLE);
            Mood.Status mood = (postConfig.post == null) ? null : postConfig.post.getMood();
            if (mood != null && mood != Mood.Status.Empty) {
                holder.moodSelector.setItem(mood);
            }
        } else {
            holder.llMood.setVisibility(View.GONE);
        }
    }

    private void displayPostType() {
        Wrapper<String> type = new Wrapper<>();
        type.item = "";

        Wrapper<Boolean> wroteGroup = new Wrapper<>(false);
        Wrapper<Boolean> wroteReply = new Wrapper<>(false);

        Resources res = getActivity().getResources();
        if (postConfig.isPersonal) {
            holder.efabPost.setImageTint(res.getColor(R.color.blueUltraLight));
            holder.efabPost.setBackgroundTint(res.getColor(R.color.blueMid));
        } else {
            holder.efabPost.setImageTint(res.getColor(R.color.purpleUltraLight));
            holder.efabPost.setBackgroundTint(res.getColor(R.color.purpleMid));
        }

        List<AsyncUtils.ExecuteManyCallback> asyncActions = new ArrayList<>();

        if (postConfig.postToReply != null) {
            asyncActions.add((i, cb) ->
                postConfig.postToReply.getAuthor().fetchIfNeededInBackground((parseObject, e) -> {
                    Throwable errResult = e;
                    if (e != null) {
                        Log.e("ComposePostDialog", "Failed to get reply to author", e);
                    } else if (!(parseObject instanceof User)) {
                        Log.e("ComposePostDialog", "Failed to coerce author to User");
                        errResult = new ClassCastException("Failed to coerce author to User");
                    } else {
                        User author = (User) parseObject;
                        type.item += "Replying to "
                                + (author.equals(User.getCurrentUser())
                                    ? "yourself"
                                    : author.getUsername());
                        wroteReply.item = true;
                    }
                    cb.call(errResult);
            }));
        } else {
            type.item += "Posting ";
        }

        if (inGroup != null) {
            asyncActions.add((i, cb) ->
                inGroup.fetchIfNeededInBackground((parseObject, e) -> {
                    Throwable errResult = e;
                    if (e != null) {
                        Log.e("ComposePostDialog", "Failed to get in group", e);
                    } else if (!(parseObject instanceof Group)) {
                        Log.e("ComposePostDialog", "Failed to coerce in group to Group");
                        errResult = new ClassCastException("Failed to coerce in group to Group");
                    } else {
                        Group group = (Group) parseObject;
                        type.item += " in " + group.getName();
                        wroteGroup.item = true;
                    }
                    cb.call(errResult);
            }));
        } else if (postConfig.postToReply == null) {
            type.item += (postConfig.isPersonal) ? "to yourself" : "to friends";
        }

        AsyncUtils.waterfall(asyncActions, (e) -> {
            if (e != null) {
                Log.e("ComposePostDialog", "Failed to generate text", e);
                holder.llTypeIndicator.setVisibility(View.GONE);
            } else {
                holder.llTypeIndicator.setVisibility(View.VISIBLE);
                holder.tvTypeIndicator.setText(type.item);

                Drawable icon;
                if (wroteReply.item) {
                    icon = res.getDrawable(R.drawable.ic_round_reply_24px);
                } else if (wroteGroup.item) {
                    icon = res.getDrawable(R.drawable.ic_highfive);
                } else if (postConfig.isPersonal) {
                    icon = res.getDrawable(R.drawable.ic_round_person_24px);
                } else {
                    icon = res.getDrawable(R.drawable.ic_round_people_24px);
                }
                holder.ivTypeIndicator.setImageDrawable(icon);
            }
        });
    }

    private void displayMedia() {
        // FIXME: Should media be fetched in background if needed?
        Runnable hide = () -> {
            holder.llAddMedia.setVisibility(View.VISIBLE);
            holder.clMedia.clear();
        };

        if (postConfig.media == null) {
            hide.run();
        } else {
            postConfig.media.makeComponent(getActivity().getResources(), (mediaComponent, e) -> {
                if (e != null) {
                    Toast.makeText(getActivity(), "Failed to load media", Toast.LENGTH_LONG).show();
                    hide.run();
                    return;
                }
                if (mediaComponent == null) {
                    hide.run();
                    return;
                }

                if (postConfig.media.getType() == Media.ContentType.Event) {
                    ((EventCardComponent) mediaComponent).setAllowDetailsClick(false);
                }

                holder.cvAddMedia.setVisibility(View.GONE);
                holder.clMedia.setMargin(getActivity().getResources().getDimensionPixelOffset(R.dimen.innerMargin));
                holder.clMedia.inflateComponent(getActivity(), mediaComponent);
                holder.clMedia.setVisibility(View.VISIBLE);
            });
        }
    }

    private void configureClickEvents() {
        holder.ivClose.setOnClickListener((view) -> onCancel());

        holder.efabPost.setOnClickListener((view) -> {
            Post post = makePost();
            if (post != null) {
                postConfig.post = post;
                onPost(postConfig);
            }
        });

        holder.cvAddMedia.setOnClickListener((view) -> {
            manager.swap(managerMediaKey);
        });
    }

    protected abstract void onCancel();
    protected abstract void onPost(PostConfig postConfig);

    private Post makePost() {
        String body = holder.etBody.getText().toString();

        if (body.equals("")) {
            Toast.makeText(getActivity(), "Write a message before posting!", Toast.LENGTH_SHORT).show();
            return null;
        }

        Post post = new Post();
        post.setBody(body);
        post.setAuthor(User.getCurrentUser());
        post.setIsPersonal(holder.psToggle.isPersonal());

        if (postConfig.displayMoodSelector) {
            Mood.Status mood = holder.moodSelector.getSelectedItem();
            if (mood != Mood.Status.Empty) {
                post.setMood(mood);
            }
        }

        ParseGeoPoint location = LocationUtils.getCurrentUserLocation();
        if (location != null) post.setLocation(location);

        if (postConfig.postToReply != null) post.setParent(postConfig.postToReply);
        if (inGroup != null)                post.setGroup(inGroup);

        return post;
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.flReplyContent)  public FrameLayout flReplyContent;
        @BindView(R.id.clPostToReply)   public ComponentLayout clPostToReply;

        @BindView(R.id.llTypeIndicator) public LinearLayout llTypeIndicator;
        @BindView(R.id.ivTypeIndicator) public ImageView ivTypeIndicator;
        @BindView(R.id.tvTypeIndicator) public TextView tvTypeIndicator;

        @BindView(R.id.cvAddMedia)      public CardView cvAddMedia;
        @BindView(R.id.clMedia)         public ComponentLayout clMedia;
        @BindView(R.id.llAddMedia)      public LinearLayout llAddMedia;

        @BindView(R.id.etBody)          public EditText etBody;
        @BindView(R.id.ivClose)         public ImageView ivClose;
        @BindView(R.id.efabPost)        public EdgeFloatingActionButton efabPost;
        @BindView(R.id.psToggle)        public PersonalSocialToggle psToggle;

        @BindView(R.id.llMood)          public LinearLayout llMood;
        @BindView(R.id.moodSelector)    public MoodSelectorLayout moodSelector;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {
        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_compose_post, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
