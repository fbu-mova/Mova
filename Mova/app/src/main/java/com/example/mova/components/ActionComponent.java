package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
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
    private ViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ActionViewComponent viewComponent;
    private ActionEditComponent editComponent;
    private ComponentManager componentManager;

    public ActionComponent(Action item) {
        super();
        this.item = item;
    }

    // overrides bc type of viewHolder is different from type of holder in checklistItemComp
    @Override
    public Component.ViewHolder getViewHolder() {
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
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
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {
        setManager(new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                viewHolder.component.clear();
                viewHolder.component.inflateComponent(activity, toComponent, new Inflater());

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

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.viewHolder = (ViewHolder) holder;

        viewHolder.component.inflateComponent(activity, viewComponent, new Inflater());

        viewHolder.component.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentManager.swap("ActionEditComponent");
            }
        });

        // todo -- set icons later
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.component)       protected ComponentLayout component;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
