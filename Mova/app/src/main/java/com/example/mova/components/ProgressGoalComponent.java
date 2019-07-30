package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressGoalComponent extends Component {

    private static final String TAG = "ProgressGoalComponent";
    private static final int viewLayoutRes = R.layout.item_progress_goal;

    private Goal goal;
    private View view;
    private ProgressGoalViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ComponentManager componentManager;

    public ProgressGoalComponent(Goal item){
        super();
        this.goal = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
        this.activity = activity;
    }



    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new ProgressGoalViewHolder(view);
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
    public String getName() {
        return "ProgressGoalComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
        if(viewHolder == null){
            Log.e(TAG, "Not inflating views in render");
            return;
        }

        if(goal.getColor() != null){
            viewHolder.ivGoalColor.setColorFilter(Color.parseColor(goal.getColor()));
        }
        viewHolder.tvGoalTitle.setText(goal.getTitle());
    }

    public static class ProgressGoalViewHolder extends Component.ViewHolder{

        @BindView(R.id.tvGoalTitle) protected TextView tvGoalTitle;
        @BindView(R.id.ivGoalColor) protected ImageView ivGoalColor;

        public ProgressGoalViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
