package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Media;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaTextComponent extends Component {

    private Media media;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager manager;

    public MediaTextComponent(Media media) {
        if (media.getType() != Media.ContentType.Text) {
            throw new IllegalArgumentException("MediaTextComponent requires media of type text.");
        }
        this.media = media;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_media_text, parent, attachToRoot);
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
        // Made unique to prevent future issues if multiple MediaTextComponents are managed
        return media.getObjectId();
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.manager = manager;
    }

    @Override
    public void render() {
        holder.tvText.setText(media.getContentText());
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvText) public TextView tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
