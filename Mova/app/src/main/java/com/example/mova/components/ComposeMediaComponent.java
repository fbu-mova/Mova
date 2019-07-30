package com.example.mova.components;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.scrolling.EndlessScrollRefreshLayout;
import com.example.mova.scrolling.ScrollLoadHandler;
import com.example.mova.utils.ImageUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ComposeMediaComponent extends Component {

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    private DataComponentAdapter<Post> scrapbookAdapter;
    private List<Post> scrapbookPosts;
    private Preview preview;

    private boolean imagesOnly;

    public ComposeMediaComponent() {
        this.imagesOnly = false;
    }

    public ComposeMediaComponent(boolean imagesOnly) {
        this.imagesOnly = imagesOnly;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_compose_media, parent, attachToRoot);
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
        return "ComposeMedia";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        holder.ivBack.setOnClickListener((view) -> {
            endCamera();
            onBack();
        });

        holder.ivClose.setOnClickListener((view) -> {
            endCamera();
            onCancel();
        });

        holder.cvGallery.setOnClickListener((view) -> {
            ImageUtils.chooseFromGallery(activity, (int requestCode, int resultCode, Intent data) -> {
                if (requestCode == ImageUtils.PICK_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
                    Uri photoUri = data.getData();
                    try {
                        Bitmap fromGallery = ImageUtils.uriToBitmapFromGallery(activity, photoUri);
                        fromGallery = ImageUtils.resizeImage(fromGallery, 300);
                        Media.fromImage(fromGallery, (media, e) -> returnMedia(media));
                    } catch (IOException e) {
                        Log.e("ComposeMediaComponent", "Failed to retrieve image from library", e);
                        Toast.makeText(activity, "Failed to retrieve image from library", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        holder.cvCamera.setOnClickListener((view) -> {
            ImageUtils.launchCamera(activity, (int requestCode, int resultCode, Intent data) -> {
                if (requestCode == ImageUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    Bitmap fromCamera = ImageUtils.getStoredBitmap(activity);
                    fromCamera = ImageUtils.resizeImage(fromCamera, 300);
                    Media.fromImage(fromCamera, (media, e) -> returnMedia(media));
                }
            });
        });

        scrapbookPosts = new ArrayList<>();
        scrapbookAdapter = new DataComponentAdapter<Post>(activity, scrapbookPosts) {
            @Override
            public Component makeComponent(Post item) {
                return new PostComponent(item);
            }
        };

        holder.llScrapbook.setVisibility(View.GONE);
        if (!imagesOnly) {
            holder.esrlScrapbook.init(new ScrollLoadHandler<Component.ViewHolder>() {
                @Override
                public void load() {
                    loadScrapbookPosts();
                }

                @Override
                public void loadMore() {
                    loadMoreScrapbookPosts();
                }

                @Override
                public RecyclerView.Adapter<Component.ViewHolder> getAdapter() {
                    return scrapbookAdapter;
                }

                @Override
                public RecyclerView.LayoutManager getLayoutManager() {
                    return new LinearLayoutManager(activity);
                }

                @Override
                public int[] getColorScheme() {
                    return EndlessScrollRefreshLayout.getDefaultColorScheme();
                }
            });
        }

        launchEmbeddedCamera();
    }

    private void loadScrapbookPosts() {
        scrapbookPosts.clear();
        loadMoreScrapbookPosts();
    }

    private void loadMoreScrapbookPosts() {
        User user = (User) ParseUser.getCurrentUser();
        ParseQuery<Post> query = user.relScrapbook.getQuery();
        query.setLimit(20);
        query.orderByDescending(Post.KEY_CREATED_AT);
        if (scrapbookPosts.size() > 0) {
            // Assume last item is oldest item because always queried in descending order
            query.whereLessThan(Post.KEY_CREATED_AT, scrapbookPosts.get(scrapbookPosts.size() - 1));
        }
        query.findInBackground((posts, e) -> {
            if (e != null) {
                Log.e("ComposeMediaComponent", "Failed to load scrapbook entries", e);
            } else if (posts.size() > 0) {
                holder.llScrapbook.setVisibility(View.VISIBLE);
                int endPos = scrapbookPosts.size();
                scrapbookPosts.addAll(posts);
                scrapbookAdapter.notifyItemRangeInserted(endPos, posts.size());
            }
        });
    }

    private void launchEmbeddedCamera() {
        // pull the metrics from our TextureView
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        // define the screen size
        Size screenSize = new Size(metrics.widthPixels, metrics.heightPixels);
        Rational screenAspectRatio = new Rational(metrics.widthPixels, metrics.heightPixels);

        PreviewConfig config = new PreviewConfig.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setLensFacing(CameraX.LensFacing.BACK)
                .setTargetResolution(screenSize)
                .build();
        preview = new Preview(config);

        preview.setOnPreviewOutputUpdateListener((Preview.PreviewOutput output) -> {
            // Remove and re-add texture to update it
            ViewGroup parent = (ViewGroup) holder.txvCamera.getParent();
            parent.removeView(holder.txvCamera);
            parent.addView(holder.txvCamera, 0);

            holder.txvCamera.setSurfaceTexture(output.getSurfaceTexture());
            updateCameraTransform();
        });

        CameraX.bindToLifecycle(activity, preview);
    }

    /** @source https://medium.com/simform-engineering/camerax-getting-started-guide-8fda21a750f7 */
    private void updateCameraTransform() {
        Matrix matrix = new Matrix();

        // Compute the center of the view finder
        float centerX = ((float) holder.txvCamera.getWidth()) / 2f;
        float centerY = ((float) holder.txvCamera.getHeight()) / 2f;

        // Correct preview output to account for display rotation
        float rotationDegree;
        switch (holder.txvCamera.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegree = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegree = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegree = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegree = 270;
                break;
            default:
                return;
        }
        matrix.postRotate(-rotationDegree, centerX, centerY);

        // Finally, apply transformations to our TextureView
        holder.txvCamera.setTransform(matrix);
    }

    private void returnMedia(Media media) {
        endCamera();
        onSelectMedia(media);
    }

    private void endCamera() {
        preview.removePreviewOutputListener();
        CameraX.unbind(preview);
    }

    public abstract void onSelectMedia(Media media);
    public abstract void onBack();
    public abstract void onCancel();

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.llButtons)     public LinearLayout llButtons;
        @BindView(R.id.ivBack)        public ImageView ivBack;
        @BindView(R.id.ivClose)       public ImageView ivClose;

        @BindView(R.id.llPhotos)      public LinearLayout llPhotos;
        @BindView(R.id.cvCamera)      public CardView cvCamera;
        @BindView(R.id.txvCamera)     public TextureView txvCamera;
        @BindView(R.id.cvGallery)     public CardView cvGallery;

        @BindView(R.id.llScrapbook)   public LinearLayout llScrapbook;
        @BindView(R.id.esrlScrapbook) public EndlessScrollRefreshLayout<Component.ViewHolder> esrlScrapbook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}