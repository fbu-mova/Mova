package com.example.mova.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.model.Post;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.PostConfig;

public class ComposableContainer extends FrameLayout {

    protected PostConfig config;
    protected boolean allowCompose;
    protected AsyncUtils.ItemCallback<Post> onPost;
    protected AsyncUtils.EmptyCallback onCancel;
    protected GestureDetector detector;

    public ComposableContainer(@NonNull Context context) {
        super(context);
        init();
    }

    public ComposableContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ComposableContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        config = new PostConfig();
        allowCompose = true;
        onPost = (post) -> {};
        onCancel = () -> {};

        detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (allowCompose) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    ComposePostDialog dialog = new ComposePostDialog((DelegatedResultActivity) getContext(), config) {
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
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // FIXME: Will this allow click pass-through, or should I unconditionally return true?
        detector.onTouchEvent(ev);
        return true;
    }

    public ComposableContainer setConfig(PostConfig config) {
        this.config = config;
        return this;
    }

    public PostConfig getConfig() {
        return config;
    }

    public ComposableContainer setAllowCompose(boolean allowCompose) {
        this.allowCompose = allowCompose;
        return this;
    }

    public boolean getAllowCompose() {
        return allowCompose;
    }

    public ComposableContainer setOnPost(AsyncUtils.ItemCallback<Post> onPost) {
        this.onPost = onPost;
        return this;
    }

    public ComposableContainer setOnCancel(AsyncUtils.EmptyCallback onCancel) {
        this.onCancel = onCancel;
        return this;
    }
}
