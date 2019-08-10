package com.example.mova.components;

import android.app.Activity;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

import com.example.mova.component.Component;
import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.model.Post;
import com.example.mova.utils.PostConfig;

public abstract class ComposableComponent extends Component {
    protected View eventView;
    protected PostConfig config;
    protected boolean allowCompose;

    public ComposableComponent() {
        this.config = new PostConfig();
    }

    public ComposableComponent(PostConfig config) {
        this.config = config;
        allowCompose = true;
    }

    public void setComposeEventView(View eventView) {
        this.eventView = eventView;
        addListeners();
    }

    public void allowCompose(boolean allow) {
        this.allowCompose = allow;
    }

    private void addListeners() {
        eventView.setOnLongClickListener((v) -> {
            if (allowCompose) {
//                vibrate();
                ComposePostDialog dialog = new ComposePostDialog(getActivity(), config) {
                    @Override
                    protected void onCancel() {
                        onCancelCompose();
                    }

                    @Override
                    protected void onPost(PostConfig config) {
                        config.savePost((post) -> onSavePost(post));
                    }
                };
                dialog.show();
            }
            return false;
        });
    }

    private void vibrate() {
        Vibrator vib = (Vibrator) getActivity().getSystemService(Activity.VIBRATOR_SERVICE);
        vib.vibrate(50);
    }

    protected abstract void onSavePost(Post post);

    protected abstract void onCancelCompose();
}
