package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalResponseComponent extends Component {

    private Post post;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentManager componentManager;

    public JournalResponseComponent(Post post) {
        this.post = post;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_journal_response, parent, attachToRoot);
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
        return "JournalResponse_" + post.getObjectId();
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
        holder.tvDate.setText(TimeUtils.toDateString(post.getCreatedAt(), false));
        holder.tvBody.setText(post.getBody());

        Media media = post.getMedia();
        Component mediaComponent = (media == null) ? null : media.makeComponent();
        if (mediaComponent == null) {
            hideMedia();
        } else {
            showMedia();
            holder.clMedia.inflateComponent(activity, mediaComponent);
        }
    }

    private void hideMedia() {
        holder.clMedia.setVisibility(View.GONE);
    }

    private void showMedia() {
        holder.clMedia.setVisibility(View.VISIBLE);
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
}
