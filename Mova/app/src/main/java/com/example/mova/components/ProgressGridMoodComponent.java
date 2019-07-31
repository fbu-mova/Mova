package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressGridMoodComponent extends Component {

    private static final String TAG = "ProgressMoodComponent";
    private static final int viewLayoutRes = R.layout.item_grid_mood;

    private ProgressGridViewHolder viewHolder;
    private Mood.Status mood;
    private ComponentManager componentManager;

    public ProgressGridMoodComponent(Mood.Status item){
        super();
        if(item != null){
            this.mood = item;
        }else{
            this.mood = Mood.Status.Empty;
        }
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new ProgressGridViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    public String getName() {
        return "ProgressGridMoodComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        checkViewHolderClass(holder, ProgressGridMoodComponent.class);
        viewHolder = (ProgressGridViewHolder) holder;

//        if(viewHolder == null){
//            Log.e(TAG, "Not inflating views in render");
//            return;
//        }

        viewHolder.ivMood.setColorFilter(Mood.getColor(mood));
    }

    @Override
    protected void onDestroy() {

    }

    public static class ProgressGridViewHolder extends Component.ViewHolder{

        @BindView(R.id.ivMood)
        ImageView ivMood;

        public ProgressGridViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ProgressGridViewHolder(view);
        }
    }
}
