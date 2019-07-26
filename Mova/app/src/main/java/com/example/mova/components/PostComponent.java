package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Group;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostComponent extends Component {

    private Post post;
    private String subheader;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager componentManager;

    public PostComponent(Post post) {
        this.post = post;
        this.subheader = null;
    }

    public PostComponent(Post post, String subheader) {
        this.post = post;
        this.subheader = subheader;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_post, parent, attachToRoot);
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
        return "Post_" + post.getObjectId();
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
        // Basic info
        holder.tvDate.setText(TimeUtils.toShortDateString(post.getCreatedAt()));
        holder.tvBody.setText(post.getBody());
        displayUser();
        displayMedia();
        displayGroup();
        displaySubheader();
    }

    private void displayUser() {
        User author = post.getAuthor();
        author.fetchIfNeededInBackground((parseObject, e) -> {
            if (e != null) {
                Log.e("PostComponent", "Failed to load author", e);
            } else if (!(parseObject instanceof User)) {
                Log.e("PostComponent", "Failed to coerce author");
            } else {
                // FIXME: Will group now be the object, or will it be parseObject?
                User loaded = (User) parseObject;
                holder.tvUsername.setText(loaded.getUsername());
                // TODO: Profile picture
                // TODO: Go to profile on click
            }
        });
    }

    private void displayMedia() {
        Media media = post.getMedia();
        Component mediaComponent = (media == null) ? null : media.makeComponent();
        if (mediaComponent == null) {
            holder.clMedia.setVisibility(View.GONE);
        } else {
            holder.clMedia.setVisibility(View.VISIBLE);
            holder.clMedia.inflateComponent(activity, mediaComponent);
        }
    }

    private void displayGroup() {
        Group group = post.getGroup();
        if (group == null) {
            holder.llGroup.setVisibility(View.GONE);
        } else {
            holder.llGroup.setVisibility(View.VISIBLE);
            group.fetchIfNeededInBackground((parseObject, e) -> {
                if (e != null) {
                    Log.e("PostComponent", "Failed to load group", e);
                } else if (!(parseObject instanceof Group)) {
                    Log.e("PostComponent", "Failed to coerce group");
                } else {
                    // FIXME: Will group now be the object, or will it be parseObject?
                    Group loaded = (Group) parseObject;
                    holder.tvGroupName.setText(loaded.getName());
                    // TODO: Group image
                    // TODO: Go to group on click
                }
            });
        }
    }

    private void displaySubheader() {
        // TODO: Maybe a more extensive check for whether subheader is all whitespace?
        if (subheader == null || subheader.equals("")) {
            holder.tvSubheader.setVisibility(View.GONE);
        } else {
            holder.tvSubheader.setVisibility(View.VISIBLE);
            holder.tvSubheader.setText(subheader);
        }
    }

    public static class ViewHolder extends Component.ViewHolder {

        // TODO: Find a way to display date without displaying username, etc.
        // TODO: Create settings for different views (group from, etc.)

        @BindView(R.id.tvSubheader)    public TextView tvSubheader;

        @BindView(R.id.llGroup)        public LinearLayout llGroup;
        @BindView(R.id.cvGroupImage)   public CardView cvGroupImage;
        @BindView(R.id.ivGroupImage)   public ImageView ivGroupImage;
        @BindView(R.id.tvGroupName)    public TextView tvGroupName;

        @BindView(R.id.card)           public CardView card;
        @BindView(R.id.tvBody)         public TextView tvBody;
        @BindView(R.id.clMedia)        public ComponentLayout clMedia;

        @BindView(R.id.llDetails)      public LinearLayout llDetails;
        @BindView(R.id.cvProfileImage) public CardView cvProfileImage;
        @BindView(R.id.ivProfileImage) public ImageView ivProfileImage;
        @BindView(R.id.tvUsername)     public TextView tvUsername;
        @BindView(R.id.tvDate)         public TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
