package com.example.mova.components;

import android.content.res.Resources;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mova.containers.GestureLayout;
import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.icons.Icons;
import com.example.mova.utils.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.fragments.Social.PostDetailsFragment;
import com.example.mova.model.Group;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseQuery;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostComponent extends Component {

    private Post post;
    private Config config;

    private ViewHolder holder;
    private ComponentManager componentManager;

    public PostComponent(Post post) {
        this(post, new Config());
    }

    public PostComponent(Post post, Config config) {
        this.post = post;
        this.config = config;
    }

    protected static PostConfig makePostConfig(Post post) {
        PostConfig postConfig = new PostConfig();
        postConfig.postToReply = post;
        postConfig.isPersonal = true; // TODO: Determine this based on current tab
        return postConfig;
    }

    public Config getConfig() {
        return config;
    }

    /**
     * Updates the component's configuration.
     * Does not force render.
     * @param config The new config to use.
     */
    public void setConfig(Config config) {
        this.config = config;
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
        return "Post_" + post.getObjectId();
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

        // Basic info
        this.holder.tvDate.setText(TimeUtils.toShortDateString(post.getCreatedAt()));
        if(post.getBody().equals("") || post.getBody() == null){
            this.holder.tvBody.setVisibility(View.GONE);
        }
        this.holder.tvBody.setText(post.getBody());

        displayUser();
        configureButtons();
        configureEvents();
        displayMedia();
        displayGroup();
        displaySubheader();
    }

    @Override
    protected void onDestroy() {

    }

    private void displayUser() {
        User author = post.getAuthor();
        author.fetchIfNeededInBackground((parseObject, e) -> {
            if (e != null) {
                Log.e("PostComponent", "Failed to load author", e);
            } else if (!(parseObject instanceof User)) {
                Log.e("PostComponent", "Failed to coerce author");
            } else {
                User loaded = (User) parseObject;
                holder.tvUsername.setText(loaded.getUsername());
                holder.ivProfileImage.setOnClickListener((view) -> {
                    // TODO: Go to profile page
                });
                Icons.from(getActivity()).displayIdenticon(loaded, holder.cvProfileImage, holder.ivProfileImage);
            }
        });
    }

    private void displayMedia() {
        Runnable hide = () -> holder.clMedia.setVisibility(View.GONE);

        if (!config.showMedia) {
            hide.run();
            return;
        }

        Media media = post.getMedia();
        if (media == null) {
            hide.run();
            return;
        }

        media.fetchIfNeededInBackground((fetchedMedia, e) -> {
            if (e != null) {
                Log.e("PostComponent", "Failed to load media", e);
                Toast.makeText(getActivity(), "Failed to load media", Toast.LENGTH_LONG).show();
                return;
            }

            Media item = (Media) fetchedMedia;
            media.fetchContentIfNeededInBackground((fetchedContent, e1) -> {
                if (e1 != null) {
                    hide.run();
                    return;
                }

                // Update content with loaded instance
                item.setContent(fetchedContent);

                media.makeComponent(getActivity().getResources(), (mediaComponent, e2) -> {
                    if (e2 != null) {
                        Toast.makeText(getActivity(), "Failed to load media", Toast.LENGTH_LONG).show();
                        hide.run();
                        return;
                    }
                    if (mediaComponent == null) {
                        hide.run();
                        return;
                    }

                    int elementMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.elementMargin);
                    holder.clMedia.setMarginTop(elementMargin);
                    holder.clMedia.setMarginBottom(elementMargin);
                    holder.clMedia.inflateComponent(getActivity(), mediaComponent);
                    holder.clMedia.setVisibility(View.VISIBLE);
                });
            });
        });
    }

    private void displayGroup() {
        Group group = post.getGroup();
        if (!config.showGroup || group == null) {
            holder.llGroup.setVisibility(View.GONE);
        } else {
            holder.llGroup.setVisibility(View.VISIBLE);
            group.fetchIfNeededInBackground((parseObject, e) -> {
                if (e != null) {
                    Log.e("PostComponent", "Failed to load group", e);
                } else if (!(parseObject instanceof Group)) {
                    Log.e("PostComponent", "Failed to coerce group");
                } else {
                    Group loaded = (Group) parseObject;
                    holder.tvGroupName.setText(loaded.getName());
                    // TODO: Group image
                    holder.ivGroupImage.setOnClickListener((view) -> {
                        // TODO: Go to group page
                    });
                    Icons.from(getActivity()).displayNounIcon(loaded, holder.cvGroupImage, holder.ivGroupImage);
                }
            });
        }
    }

    private void displaySubheader() {
        // TODO: Maybe a more extensive check for whether subheader is all whitespace?
        if (config.subheader == null || config.subheader.equals("")) {
            holder.llSubheader.setVisibility(View.GONE);
        } else {
            holder.llSubheader.setVisibility(View.VISIBLE);
            holder.tvSubheader.setText(config.subheader);
        }
    }

    private void showButtons() {
        holder.llButtons.setVisibility(View.VISIBLE);
    }

    private void hideButtons() {
        holder.llButtons.setVisibility(View.GONE);
    }

    private void configureButtons() {
        if (config.showButtons) {
            showButtons();

            holder.ivRepost.setOnClickListener((view) -> {
                PostConfig config = new PostConfig();
                config.media = new Media(post);

                ComposePostDialog dialog = new ComposePostDialog(getActivity(), config) {
                    @Override
                    protected void onCancel() {

                    }

                    @Override
                    protected void onPost(PostConfig config) {
                        config.savePost((savedPost) -> {
                            PostComponent.this.config.onRepost.call(savedPost);
                        });
                    }
                };

                dialog.show();
            });

            holder.ivReply.setOnClickListener((view) -> {
                PostConfig config = new PostConfig();
                config.postToReply = post;

                ComposePostDialog dialog = new ComposePostDialog(getActivity(), config) {
                    @Override
                    protected void onCancel() {

                    }

                    @Override
                    protected void onPost(PostConfig config) {
                        config.savePost((savedPost) -> {
                            PostComponent.this.config.onReply.call(savedPost);
                        });
                    }
                };

                dialog.show();
            });

            holder.ivSave.setOnClickListener((view) -> {
                isSavedToScrapbook((saved) -> {
                    if (!saved) {
                        User.getCurrentUser().relScrapbook.add(post, (savedPost) -> {
                            Toast.makeText(getActivity(), "Saved to scrapbook!", Toast.LENGTH_SHORT).show();
                            toggleIcon(holder.ivSave, true);
                        });
                    } else {
                        User.getCurrentUser().relScrapbook.remove(post, () -> {
                            Toast.makeText(getActivity(), "Removed from scrapbook.", Toast.LENGTH_SHORT).show();
                            toggleIcon(holder.ivSave, false);
                        });
                    }
                });
            });

            isSavedToScrapbook((saved) -> toggleIcon(holder.ivSave, saved));
        } else {
            hideButtons();
        }
    }

    private void isSavedToScrapbook(AsyncUtils.ItemCallback<Boolean> callback) {
        ParseQuery<Post> query = User.getCurrentUser().relScrapbook.getQuery();
        query.whereEqualTo(Post.KEY_ID, post.getObjectId());
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e("PostComponent", "Failed to load scrapbook entries for toggle", e);
                Toast.makeText(getActivity(), "Failed to save to scrapbook", Toast.LENGTH_LONG).show();
            }
            callback.call(posts.size() > 0);
        });
    }

    private void configureEvents() {
//        holder.card.setOnClickListener((view) -> {
//            if (config.allowDetailsClick) {
//                PostDetailsFragment frag = PostDetailsFragment.newInstance(post);
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                FragmentTransaction ft = manager.beginTransaction();
//                ft.add(R.id.flSocialContainer, frag);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//            config.onClick.call(post);
//        });

        GestureDetector detector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (config.allowDetailsClick) {
                    PostDetailsFragment frag = PostDetailsFragment.newInstance(post);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.add(R.id.flSocialContainer, frag);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                config.onClick.call(post);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (config.allowCompose) {
                    new ComposePostDialog.Builder(getActivity())
                            .setConfig(makePostConfig(post))
                            .setAllowCompose(true)
                            .setOnPost((toPost) -> {
                                Toast.makeText(getActivity(), "Posted!", Toast.LENGTH_SHORT).show();
                                // TODO
                            })
                            .show(holder.glCompose);
                }
            }
        });
        holder.glCompose.setGestureDetector(detector);
    }

    private void toggleIcon(ImageView ivIcon, boolean active) {
        if (ivIcon == null) return;
        // TODO: Perhaps choose more colorful tints based on additional context.
        Resources res = getActivity().getResources();
        int id = (active) ? R.color.buttonActive : R.color.buttonInactive;
        int tintColor = res.getColor(id);
        ivIcon.setColorFilter(tintColor);
    }

    public static class ViewHolder extends Component.ViewHolder {

        // TODO: Find a way to display date without displaying username, etc.

        @BindView(R.id.llSubheader)    public LinearLayout llSubheader;
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

        @BindView(R.id.llButtons)      public LinearLayout llButtons;
        @BindView(R.id.ivRepost)       public ImageView ivRepost;
        @BindView(R.id.ivReply)        public ImageView ivReply;
        @BindView(R.id.ivSave)         public ImageView ivSave;

        @BindView(R.id.glCompose)      public GestureLayout glCompose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_post, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }

    public static class Config {
        public String subheader = null;
        public boolean showGroup = true;
        public boolean showMedia = true;
        public boolean showButtons = true;
        public boolean allowCompose = true;
        public boolean allowDetailsClick = true;

        public AsyncUtils.ItemCallback<Post> onReply = (post) -> {};
        public AsyncUtils.ItemCallback<Post> onRepost = (post) -> {};
        public AsyncUtils.ItemCallback<Post> onClick = (post) -> {};

        public Config() { }

        public Config(String subheader, boolean showGroup, boolean showButtons, boolean allowDetailsClick) {
            this.subheader = subheader;
            this.showButtons = showButtons;
            this.showMedia = true;
            this.allowDetailsClick = allowDetailsClick;
            this.showGroup = showGroup;
        }
    }
}
