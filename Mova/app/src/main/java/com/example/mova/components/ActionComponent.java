package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionComponent extends Component {

    private static final String TAG = "action comp";
    private static final int viewLayoutRes = R.layout.item_action;

    private Action item;
    private View view;
    private ActionViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ActionViewComponent viewComponent;
    private ActionEditComponent editComponent;
    private ComponentManager componentManager;

    public ActionComponent(Action item) {
        super();
        this.item = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        viewHolder = new ActionViewHolder(view);
        this.activity = activity;

        // for ActionComponent, makeViewHolder should only be called once so it's not redundant
        setManager(new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                viewHolder.component.clear();
                viewHolder.component.inflateComponent(activity, toComponent);

                if (toKey.equals(editComponent.getName())) {
                    ((ActionEditComponent.ActionEditViewHolder) editComponent.getViewHolder()).etAction
                            .setText(item.getTask());
                }
            }
        });

        viewComponent = new ActionViewComponent(item, componentManager);
        editComponent = new ActionEditComponent(item, componentManager);

        componentManager.launch(viewComponent.getName(), viewComponent);
        componentManager.launch(editComponent.getName(), editComponent);
    }

    // overrides bc type of viewHolder is different from type of holder in checklistItemComp
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
        return "ActionComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {

        viewHolder.component.inflateComponent(activity, viewComponent);

        viewHolder.component.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentManager.swap("ActionEditComponent");
            }
        });

        // todo -- set icons later
    }

    public static class ActionViewHolder extends Component.ViewHolder {

        @BindView(R.id.component)       protected ComponentLayout component;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
