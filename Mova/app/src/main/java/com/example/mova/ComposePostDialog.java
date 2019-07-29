package com.example.mova;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.components.Component;
import com.example.mova.components.ComponentLayout;
import com.example.mova.components.ComponentManager;
import com.example.mova.components.ComposeMediaComponent;
import com.example.mova.components.ComposePostComponent;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;

import java.util.List;

public abstract class ComposePostDialog {
    private DelegatedResultActivity activity;
    private ComponentManager manager;

    private View dialogView;
    private ComponentLayout container;
    private AlertDialog dialog;

    private ComposePostComponent composeComponent;
    private ComposeMediaComponent mediaComponent;
    private Media lastGeneratedMedia;

    // TODO: Add all necessary constructor signatures

    public ComposePostDialog(DelegatedResultActivity activity) {
        this.activity = activity;
        this.lastGeneratedMedia = null;
        buildDialog();

        this.manager = new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                container.clear();
                container.inflateComponent(activity, toComponent);

                // Pass media to compose component if any media exists
                if (toKey.equals(composeComponent.getName()) && lastGeneratedMedia != null) {
                    composeComponent.setMedia(lastGeneratedMedia);
                }
            }
        };
    }

    private void buildDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.layout_component_layout, null);
        container = dialogView.findViewById(R.id.componentLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(dialogView);
        dialog = builder.create();
    }

    public void show() {
        mediaComponent = new ComposeMediaComponent() {
            @Override
            public void onSelectMedia(Media media) {
                lastGeneratedMedia = media;
                manager.swap(composeComponent.getName());
            }

            @Override
            public void onBack() {
                manager.swap(composeComponent.getName());
            }

            @Override
            public void onCancel() {
                dialog.cancel();
                ComposePostDialog.this.onCancel();
            }
        };

        mediaComponent.setManager(manager);
        manager.launch(mediaComponent.getName(), mediaComponent);

        composeComponent = new ComposePostComponent(mediaComponent.getName()) {
            @Override
            protected void onCancel() {
                dialog.cancel();
                ComposePostDialog.this.onCancel();
            }

            @Override
            protected void onPost(Post post, List<Tag> tags, Media media, Post postToReply) {
                dialog.dismiss();
                ComposePostDialog.this.onPost(post, tags, media, postToReply);
            }
        };

        composeComponent.setManager(manager);
        manager.launch(composeComponent.getName(), composeComponent);

        container.inflateComponent(activity, composeComponent);

        dialog.show();
    }

    protected abstract void onCancel();
    protected abstract void onPost(Post post, List<Tag> tags, Media media, Post postToReply);
}
