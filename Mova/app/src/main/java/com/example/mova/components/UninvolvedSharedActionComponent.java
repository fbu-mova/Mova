package com.example.mova.components;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.SharedAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UninvolvedSharedActionComponent extends Component {

    private static final String TAG = "UNinv. shared action comp";

    protected SharedAction sharedAction;
    protected boolean isUserDone;
    protected static int viewLayoutRes = R.layout.item_uninvolved_shared_action;

    protected UninvolvedViewHolder holder;
    protected View view;
    protected DelegatedResultActivity activity;

    protected ComponentManager componentManager;

    public UninvolvedSharedActionComponent(SharedAction.Data data) {
        super();
        this.sharedAction = data.sharedAction;
        this.isUserDone = data.isUserDone;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
        holder = new UninvolvedViewHolder(view);
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
        return "UninvolvedSharedActionComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {

        // todo -- have a better icon for ivIcon
        // todo -- should GoalCardComp add "saveSocialGoal" button if in this case?

        int complete = sharedAction.getUsersDone();
        sharedAction.relChildActions.getSize((total) -> {
            holder.tvNumDone.setText(complete + "/" + total + " done!");
        });

        holder.tvTask.setText(sharedAction.getTask());
    }

    public static class UninvolvedViewHolder extends Component.ViewHolder {

        @BindView(R.id.ivIcon)      protected ImageView ivIcon;
        @BindView(R.id.tvNumDone)   protected TextView tvNumDone;
        @BindView(R.id.tvTask)      protected TextView tvTask;

        public UninvolvedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
