package com.example.mova.components;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Post;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressGridMoodComponent extends Component<Post>{

    private static final String TAG = "ProgressMoodComponent";
    private static final int viewLayoutRes = R.layout.item_grid_mood;

    private View view;
    private ProgressGridViewHolder viewHolder;
    private Activity activity;

    public ProgressGridMoodComponent(Post item){
        super(item);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new ProgressGridViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void render() {
        if(viewHolder == null){
            Log.e(TAG, "Not inflating views in render");
            return;
        }

        Post post = getItem();
        Mood.Status status = post.getMood();
        viewHolder.ivMood.setColorFilter(Mood.getColor(status));
    }

    public static class ProgressGridViewHolder extends Component.ViewHolder{

        @BindView(R.id.ivMood)
        ImageView ivMood;

        public ProgressGridViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
