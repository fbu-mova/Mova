package com.example.mova.components;

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

import com.example.mova.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Group;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.Wrapper;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseGeoPoint;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ComposePostComponent extends Component {

    // TODO: Handle tags

    private PostConfig postConfig;
    private Group inGroup;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;

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
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_compose_post, parent, attachToRoot);
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
        return "ComposePostDialog";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        displayToReplyTo();
        displayPostType();
        displayMedia();
        configureClickEvents();
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
            PostComponent postComponent = new PostComponent(postConfig.postToReply);
            holder.flReplyContent.setVisibility(View.VISIBLE);
            holder.clPostToReply.setMargin(32);
            holder.clPostToReply.inflateComponent(activity, postComponent);
            postComponent.hideButtons();
        }
    }

    private void displayPostType() {
        // TODO: Set correct icons
        Wrapper<String> type = new Wrapper<>();
        type.item = "";

        List<AsyncUtils.ExecuteManyCallback> asyncActions = new ArrayList<>();

        if (postConfig.postToReply != null) {
            asyncActions.add((i, cb) ->
                postConfig.postToReply.getAuthor().fetchIfNeededInBackground((parseObject, e) -> {
                    if (e != null) {
                        Log.e("ComposePostDialog", "Failed to get reply to author", e);
                    } else if (!(parseObject instanceof User)) {
                        Log.e("ComposePostDialog", "Failed to coerce author to User");
                    } else {
                        User author = (User) parseObject;
                        type.item += "Replying to " + author.getUsername();
                    }
            }));
        } else {
            type.item += "Posting ";
        }

        if (inGroup != null) {
            asyncActions.add((i, cb) ->
                inGroup.fetchIfNeededInBackground((parseObject, e) -> {
                    if (e != null) {
                        Log.e("ComposePostDialog", "Failed to get in group", e);
                    } else if (!(parseObject instanceof Group)) {
                        Log.e("ComposePostDialog", "Failed to coerce in group to Group");
                    } else {
                        Group group = (Group) parseObject;
                        type.item += " in " + group.getName();
                    }
            }));
        } else if (postConfig.postToReply == null) {
            type.item += "to friends";
        }

        AsyncUtils.waterfall(asyncActions, (e) -> {
            if (e != null) {
                Log.e("ComposePostDialog", "Failed to generate text");
                holder.llTypeIndicator.setVisibility(View.GONE);
            } else {
                holder.llTypeIndicator.setVisibility(View.VISIBLE);
                holder.tvTypeIndicator.setText(type.item);
                // TODO: Set image
            }
        });
    }

    private void displayMedia() {
        // FIXME: Should media be fetched in background if needed?
        Component mediaComponent = (postConfig.media == null) ? null : postConfig.media.makeComponent();
        if (mediaComponent == null) {
            holder.llAddMedia.setVisibility(View.VISIBLE);
            holder.clMedia.clear();
        } else {
            holder.llAddMedia.setVisibility(View.GONE);
            holder.clMedia.inflateComponent(activity, mediaComponent);
        }
    }

    private void configureClickEvents() {
        holder.ivClose.setOnClickListener((view) -> onCancel());

        holder.fabPost.setOnClickListener((view) -> {
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
            Toast.makeText(activity, "Write a message before posting!", Toast.LENGTH_SHORT).show();
            return null;
        }

        Post post = new Post();
        post.setBody(body);
        post.setAuthor(User.getCurrentUser());
        post.setIsPersonal(postConfig.isPersonal);

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
        @BindView(R.id.fabPost)         public FloatingActionButton fabPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
