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
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.SharedAction;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UninvolvedSharedActionComponent extends Component {

    private static final String TAG = "UNinv. shared action comp";

    protected SharedAction sharedAction;
    protected boolean isUserDone;
    protected static int viewLayoutRes = R.layout.item_uninvolved_shared_action;

    protected UninvolvedViewHolder holder;

    protected ComponentManager componentManager;

    public UninvolvedSharedActionComponent(SharedAction.Data data) {
        super();
        this.sharedAction = data.sharedAction;
        this.isUserDone = data.isUserDone;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
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
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {

        // todo -- have a better icon for ivIcon
        // todo -- should GoalCardComp add "saveSocialGoal" button if in this case?

        checkViewHolderClass(holder, UninvolvedViewHolder.class);
        this.holder = (UninvolvedViewHolder) holder;

        int complete = sharedAction.getUsersDone();
        sharedAction.relChildActions.getSize((total) -> {
            this.holder.tvNumDone.setText(complete + "/" + total + " done!");
        });

        this.holder.tvTask.setText(sharedAction.getTask());

    }

    @Override
    protected void onDestroy() {

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

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new UninvolvedViewHolder(view);
        }
    }
}
