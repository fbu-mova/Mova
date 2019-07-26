package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Action;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionViewComponent extends Component {

    private static final int viewLayoutRes = R.layout.item_action_view;
    private static final String TAG = "action view comp";

    private Action action;
    private View view;
    private ActionViewViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ComponentManager componentManager;

    public ActionViewComponent(Action action, ComponentManager componentManager) {
        super();
        this.action = action;
        setManager(componentManager);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        viewHolder = new ActionViewViewHolder(view);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public View getView() {
        return view;
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
    public void render() {
        viewHolder.tvAction.setText(action.getTask());

        // todo -- implement icons
    }

    public static class ActionViewViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvAction)        protected TextView tvAction;
        @BindView(R.id.ivIcon)          protected ImageView ivIcon;

        public ActionViewViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}