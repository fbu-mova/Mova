package com.example.mova;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.components.Component;
import com.example.mova.components.ComponentLayout;
import com.example.mova.components.ComponentManager;
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
    // TODO: Store ComposeMediaComponent

    // TODO: Add all necessary constructor signatures

    public ComposePostDialog(DelegatedResultActivity activity) {
        this.activity = activity;
        buildDialog();

        this.manager = new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                container.clear();
                container.inflateComponent(activity, toComponent);

                if (toKey.equals(composeComponent.getName())) {
                    // TODO: Get media from result
                    composeComponent.setMedia(null);
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
        // TODO: Create ComposeMediaComponent
        composeComponent = new ComposePostComponent("TODO") {
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
