package com.example.mova.components;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Media;
import com.example.mova.scrolling.EndlessScrollRefreshLayout;
import com.example.mova.utils.ImageUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ComposeMediaComponent extends Component {

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    public ComposeMediaComponent() {

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
        holder.ivBack.setOnClickListener((view) -> onBack());
        holder.ivClose.setOnClickListener((view) -> onCancel());

        holder.cvLibrary.setOnClickListener((view) -> {
            ImageUtils.chooseFromGallery(activity, (int requestCode, int resultCode, Intent data) -> {
                if (requestCode == ImageUtils.PICK_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
                    Uri photoUri = data.getData();
                    try {
                        Bitmap fromGallery = ImageUtils.uriToBitmapFromGallery(activity, photoUri);
                        Media media = Media.fromImage(fromGallery);
                        onSelectMedia(media);
                    } catch (IOException e) {
                        Log.e("ComposeMediaComponent", "Failed to retrieve image from library", e);
                        Toast.makeText(activity, "Failed to retrieve image from library", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        // TODO: Camera, scrapbook, embedded camera
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
        @BindView(R.id.ivCamera)      public ImageView ivCamera;
        @BindView(R.id.cvGallery)     public CardView cvLibrary;

        @BindView(R.id.llScrapbook)   public LinearLayout llScrapbook;
        @BindView(R.id.esrlScrapbook) public EndlessScrollRefreshLayout<Component.ViewHolder> esrlScrapbook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
