package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalResponseComponent extends Component {

    private Post post;

    private ViewHolder holder;
    private ComponentManager componentManager;

    public JournalResponseComponent(Post post) {
        this.post = post;
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
        return "JournalResponse_" + post.getObjectId();
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

        this.holder.tvDate.setText(TimeUtils.toShortDateString(post.getCreatedAt()));
        this.holder.tvBody.setText(post.getBody());
        displayMedia();
    }

    @Override
    protected void onDestroy() {

    }

    private void displayMedia() {
        Runnable hide = () -> holder.clMedia.setVisibility(View.GONE);

        Media media = post.getMedia();
        if (media == null) {
            hide.run();
        } else {
            media.makeComponent(getActivity().getResources(), (mediaComponent, e) -> {
                if (e != null) {
                    Toast.makeText(getActivity(), "Failed to load media", Toast.LENGTH_LONG).show();
                    hide.run();
                    return;
                }
                if (mediaComponent == null) {
                    hide.run();
                    return;
                }
                holder.clMedia.setVisibility(View.VISIBLE);
                holder.clMedia.inflateComponent(getActivity(), mediaComponent);
            });
        }
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvDate)         public TextView tvDate;
        @BindView(R.id.tvBody)         public TextView tvBody;
        @BindView(R.id.clMedia)        public ComponentLayout clMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_journal_response, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
