package com.example.mova.dialogs;

import android.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.example.mova.containers.GestureLayout;
import com.example.mova.model.Post;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.PostConfig;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.components.ComposeMediaComponent;
import com.example.mova.components.ComposePostComponent;
import com.example.mova.model.Media;

public abstract class ComposePostDialog {
    private DelegatedResultActivity activity;
    private ComponentManager manager;

    private View dialogView;
    private ComponentLayout container;
    private AlertDialog dialog;

    private ComposePostComponent composeComponent;
    private ComposeMediaComponent mediaComponent;

    private PostConfig postConfig;

    public ComposePostDialog(DelegatedResultActivity activity) {
        this(activity, new PostConfig());
    }

    public ComposePostDialog(DelegatedResultActivity activity, PostConfig postConfig) {
        this.activity = activity;
        this.postConfig = postConfig;
        buildDialog();
        makeManager();
    }

    private void buildDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.layout_component_layout, null);
        container = dialogView.findViewById(R.id.componentLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setView(dialogView);
        dialog = builder.create();
    }

    private void makeManager() {
        this.manager = new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                container.clear();
                container.inflateComponent(activity, toComponent);

                // Pass media to compose component if any media exists
                if (toKey.equals(composeComponent.getName()) && postConfig.media != null) {
                    composeComponent.setMedia(postConfig.media);
                }
            }
        };
    }

    public void show() {
        mediaComponent = new ComposeMediaComponent() {
            @Override
            public void onSelectMedia(Media media) {
                postConfig.media = media;
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

        composeComponent = new ComposePostComponent(mediaComponent.getName(), postConfig) {
            @Override
            protected void onCancel() {
                dialog.cancel();
                ComposePostDialog.this.onCancel();
            }

            @Override
            protected void onPost(PostConfig config) {
                dialog.dismiss();
                ComposePostDialog.this.onPost(config);
            }
        };

        composeComponent.setManager(manager);
        manager.launch(composeComponent.getName(), composeComponent);

        container.inflateComponent(activity, composeComponent);

        dialog.show();
    }

    protected abstract void onCancel();
    protected abstract void onPost(PostConfig config);

    public static class Builder {
        protected DelegatedResultActivity activity;
        protected GestureDetector detector;
        protected GestureLayout layout;

        protected PostConfig config;
        protected boolean allowCompose;
        protected AsyncUtils.ItemCallback<Post> onPost;
        protected AsyncUtils.EmptyCallback onCancel;

        public Builder(DelegatedResultActivity activity) {
            this.activity = activity;

            config = new PostConfig();
            allowCompose = true;
            onPost = (post) -> {};
            onCancel = () -> {};

            detector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    Log.d("ComposableContainer", "Press");
                    showDialog();
                }
            });
        }

        public Builder setConfig(PostConfig config) {
            this.config = config;
            return this;
        }

        public PostConfig getConfig() {
            return config;
        }

        public Builder setAllowCompose(boolean allowCompose) {
            this.allowCompose = allowCompose;
            return this;
        }

        public boolean getAllowCompose() {
            return allowCompose;
        }

        public Builder setOnPost(AsyncUtils.ItemCallback<Post> onPost) {
            this.onPost = onPost;
            return this;
        }

        public Builder setOnCancel(AsyncUtils.EmptyCallback onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public Builder setGestureLayout(GestureLayout layout) {
            this.layout = layout;
            layout.addGestureDetector(detector);
            return this;
        }

        public void show() {
            showDialog();
        }

        private void showDialog() {
            if (allowCompose) {
                layout.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                ComposePostDialog dialog = new ComposePostDialog(activity, config) {
                    @Override
                    protected void onCancel() {
                        onCancel.call();
                    }

                    @Override
                    protected void onPost(PostConfig config) {
                        config.savePost((post) -> onPost.call(post));
                    }
                };
                dialog.show();
            }
        }
    }
}
