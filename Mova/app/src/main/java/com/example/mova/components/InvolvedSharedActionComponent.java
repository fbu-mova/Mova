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

    public InvolvedSharedActionComponent(SharedAction.Data data) {
        super(data, Color.parseColor("#999999"), Color.parseColor("#222222"),
                false, (thing) -> thing.sharedAction.getTask(),
                (thing) -> {
                    return data.isUserDone;
                });

        this.sharedAction = data.sharedAction;

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {

        checkViewHolderClass(holder, InvolvedViewHolder.class);
        this.holder = (InvolvedViewHolder) holder;

        this.holder.cbItem.setText(sharedAction.getTask());

        int complete = sharedAction.getUsersDone();

        sharedAction.relChildActions.getSize((total) -> {
            this.holder.tvNumDone.setText(complete + "/" + total + " done!");
        });

        this.holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) ->
                onCheckedChanged(buttonView, isChecked));
        this.holder.cbItem.setTextColor(uncheckedColor);
        this.holder.cbItem.setChecked(getDone.call(item));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // finds the action of the sharedAction corresponding to the user, updates isDone boolean

        findUsersAction(sharedAction, (action) -> {
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

    }

    private static void findUsersAction(SharedAction sharedAction, AsyncUtils.ItemCallback<Action> callback) {
        sharedAction.relChildActions.getQuery()
                .whereEqualTo(KEY_PARENT_USER, User.getCurrentUser())
                .findInBackground(new FindCallback<Action>() {
                    @Override
                    public void done(List<Action> objects, ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "found action");
                            if (objects.size() == 1) {
                                callback.call(objects.get(0));
                                return;
                            }
                            Log.e(TAG, "found either none or too many actions");
                        }
                        else {
                            Log.e(TAG, "failed finding action", e);
                        }
                    }
                });
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
