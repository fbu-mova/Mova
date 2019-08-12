package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class UninvolvedSharedActionComponent extends ChecklistItemComponent<SharedAction> {

    private static final String TAG = "UNinv. shared action comp";

    protected SharedAction sharedAction;
    protected boolean isUserDone;
    protected static int viewLayoutRes = R.layout.item_uninvolved_shared_action;

    protected ViewHolder holder;

    protected ComponentManager componentManager;

    public UninvolvedSharedActionComponent(SharedAction.Data data) {
        super(data.sharedAction, (item) -> item.getTask(), (item) -> false);
        this.sharedAction = data.sharedAction;
        this.isUserDone = data.isUserDone;
    }

    @Override
    public ChecklistItemComponent.ViewHolder getViewHolder() {
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
    protected void onRender(Component.ViewHolder holder) {

        // todo -- have a better icon for ivIcon
        // todo -- should GoalCardComp add "saveSocialGoal" button if in this case?

        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        int complete = sharedAction.getUsersDone();
        sharedAction.relChildActions.getSize((total) -> {
            this.holder.tvNumDone.setText(complete + "/" + total + " done!");
        });
    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public void onCheckedChanged(boolean isChecked) {

    }

    public static class ViewHolder extends ChecklistItemComponent.ViewHolder {

        @BindView(R.id.tvNumDone)   protected TextView tvNumDone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends ChecklistItemComponent.Inflater {

        public Inflater() {
            super(viewLayoutRes);
        }

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            Component.ViewHolder holder = super.inflate(activity, parent, attachToRoot);
            return new ViewHolder(holder.getView());
        }
    }
}
