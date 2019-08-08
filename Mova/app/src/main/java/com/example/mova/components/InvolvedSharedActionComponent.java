package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.model.Action.KEY_PARENT_USER;

public class InvolvedSharedActionComponent extends ChecklistItemComponent<SharedAction.Data> {

    private static final String TAG = "inv. shared action comp";

    protected SharedAction sharedAction;
    protected static int viewLayoutRes = R.layout.item_involved_shared_action;
    protected InvolvedViewHolder holder;

    private int complete;
    private int total;

    private Goal.HandleUpdatedProgress progressHandler;

    public InvolvedSharedActionComponent(SharedAction.Data data, Goal.HandleUpdatedProgress progressHandler) {
        super(data, Color.parseColor("#999999"), Color.parseColor("#222222"),
                false, (thing) -> thing.sharedAction.getTask(),
                (thing) -> {return data.isUserDone;});

        this.sharedAction = data.sharedAction;
        this.progressHandler = progressHandler;

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {

        checkViewHolderClass(holder, InvolvedViewHolder.class);
        this.holder = (InvolvedViewHolder) holder;

        this.holder.cbItem.setText(sharedAction.getTask());

        complete = sharedAction.getUsersDone();

        sharedAction.relChildActions.getSize((total) -> {
            this.total = total;
            updateNumDone(complete, this.total);
        });

        this.holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (allowCheckedEvent) {
                onCheckedChanged(buttonView, isChecked);
            }
        });
        this.holder.cbItem.setTextColor(uncheckedColor);
        this.allowCheckedEvent = false;
        this.holder.cbItem.setChecked(getDone.call(item));
        this.allowCheckedEvent = true;
    }

    private void updateNumDone(int complete, int total) {
        this.holder.tvNumDone.setVisibility(View.VISIBLE);
        this.holder.tvNumDone.setText(complete + "/" + total + " done!");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // finds the action of the sharedAction corresponding to the user, updates isDone boolean

        GoalUtils.findUsersAction(sharedAction, (action) -> {
            GoalUtils.toggleDone(action, (portionDone) -> {
                // update goalProgressBar - need to make call/handler/event listener in GoalCardComp/Details
                progressHandler.call(portionDone);
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

    public static class InvolvedViewHolder extends Component.ViewHolder {

        @BindView(R.id.cbItem)      protected CheckBox cbItem;
        @BindView(R.id.tvNumDone)   protected TextView tvNumDone;

        public InvolvedViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new InvolvedViewHolder(view);
        }
    }
}
