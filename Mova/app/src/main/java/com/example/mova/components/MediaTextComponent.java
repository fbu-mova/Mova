package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Media;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaTextComponent extends Component {

    private Media media;

    private ViewHolder holder;
    private ComponentManager manager;

    public MediaTextComponent(Media media) {
        if (media.getType() != Media.ContentType.Text) {
            throw new IllegalArgumentException("MediaTextComponent requires media of type text.");
        }
        this.media = media;
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
        // Made unique to prevent future issues if multiple MediaTextComponents are managed
        return media.getObjectId();
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        Log.d("MediaTextComponent", "Text: " + media.getContentText());
        this.holder.tvText.setText(media.getContentText());
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvText) public TextView tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_media_text, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
