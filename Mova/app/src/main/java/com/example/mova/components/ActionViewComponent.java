package com.example.mova.components;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.views.ActionView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionViewComponent extends ChecklistItemComponent<Action> {
        // needs a separate name for getName component manager, and to implement icons...
        // fixme -- needs diff viewLayoutRes to implement icons

    private static final String TAG = "action view comp";
    private static final int viewLayoutRes = R.layout.item_checklist_action_view;

    private Action action;
    private ColorUtils.Hue hue;
    private AsyncUtils.EmptyCallback onTextClickListener;
    private AsyncUtils.ItemCallback<Boolean> onSuccessfullyToggled;

    private ViewHolder viewHolder;

    private ComponentManager componentManager;

    public ActionViewComponent(Action action, ColorUtils.Hue hue, ComponentManager componentManager) {
        super(action, (item) -> item.getTask(), (item) -> (item.getIsDone()));
        onSuccessfullyToggled = completed -> {};
        this.action = action;
        this.hue = hue;
        setManager(componentManager);
    }

    @Override
    public ViewHolder getViewHolder() {
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public String getName() {
        return "ActionViewComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onLaunch() {
        super.onLaunch();
        setColors(ActionView.ColorConfig.defaultFromHue(getActivity().getResources(), hue));
    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        super.onRender(holder);
        checkViewHolderClass(holder, ViewHolder.class);

        this.viewHolder = (ViewHolder) holder;
        this.viewHolder.avItem.setOnTextClickListener(() -> {
            onTextClickListener.call();
        });

        this.viewHolder.ivPriority.setVisibility(View.GONE);
        if (action.getIsPriority()) {
            // set icon next to checklist item
            Resources res = getActivity().getResources();
            this.viewHolder.ivPriority.setColorFilter(ColorUtils.getColor(res, hue, ColorUtils.Lightness.Mid));
            this.viewHolder.ivPriority.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {
        GoalUtils.toggleDone(item, (e) -> {
            if (e == null) {
                Log.d(TAG, "toggled action done");
                onSuccessfullyToggled.call(isChecked);
            }
            else {
                Log.e(TAG, "toggled action failed", e);
                Toast.makeText(getActivity(), "Toggling action failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setOnSuccessfullyToggled(AsyncUtils.ItemCallback<Boolean> listener) {
        onSuccessfullyToggled = listener;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    public void setOnTextClickListener(AsyncUtils.EmptyCallback listener) {
        onTextClickListener = listener;
    }

    public class ViewHolder extends ChecklistItemComponent.ViewHolder {

        @BindView(R.id.ivPriority) protected ImageView ivPriority;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class Inflater extends ChecklistItemComponent.Inflater {

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
