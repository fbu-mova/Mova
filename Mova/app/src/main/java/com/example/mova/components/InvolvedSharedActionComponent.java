package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.model.SharedAction;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.views.ActionView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvolvedSharedActionComponent extends ChecklistItemComponent<SharedAction.Data> {

    private static final String TAG = "inv. shared action comp";

    protected SharedAction sharedAction;
    protected ColorUtils.Hue hue;
    protected static int viewLayoutRes = R.layout.item_involved_shared_action;
    protected ViewHolder holder;

    private int complete;
    private int total;

    public InvolvedSharedActionComponent(SharedAction.Data data, ColorUtils.Hue hue) {
        super(data,
            (thing) -> thing.sharedAction.getTask(),
            (thing) -> {
                return data.isUserDone;
            });

        this.sharedAction = data.sharedAction;
        this.hue = hue;
    }

    @Override
    protected void onLaunch() {
        super.onLaunch();
        setColors(ActionView.ColorConfig.defaultFromHue(getActivity().getResources(), hue));
    }

    @Override
    protected void onRender(Component.ViewHolder holder) {

        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        complete = sharedAction.getUsersDone();

        sharedAction.relChildActions.getSize((total) -> {
            this.total = total;
            updateNumDone(complete, this.total);
        });

//        this.holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (allowCheckedEvent) {
//                onCheckedChanged(isChecked);
//            }
//        });
//        this.holder.cbItem.setTextColor(uncheckedColor);

        this.allowCheckedEvent = false;
        this.holder.avItem.setComplete(getDone.call(item));
        this.allowCheckedEvent = true;
    }

    private void updateNumDone(int complete, int total) {
        this.holder.tvNumDone.setVisibility(View.VISIBLE);
        this.holder.tvNumDone.setText(complete + "/" + total + " done!");
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        // finds the action of the sharedAction corresponding to the user, updates isDone boolean

        GoalUtils.findUsersAction(sharedAction, (action) -> {
            GoalUtils.toggleDone(action, (e) -> {
                if (e == null) {
                    Log.d(TAG, "toggled action done");
                }
                else {
                    Log.e(TAG, "toggled action failed", e);
                    Toast.makeText(getActivity(), "Toggling action failed", Toast.LENGTH_LONG).show();
                }
            });
        });

        if (isChecked) {
            complete++;
        }
        else {
            complete--;
        }
        updateNumDone(complete, total); // updates UI without calling database
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
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
