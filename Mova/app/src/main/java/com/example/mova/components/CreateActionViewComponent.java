package com.example.mova.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateActionViewComponent extends Component {

    private static final String TAG = "createActionView comp";
    private static final int viewLayoutRes = R.layout.item_action_view;

    private ComponentManager componentManager;

    private ViewHolder viewHolder;
    private Action uncreatedAction;

    public CreateActionViewComponent(Action action, ComponentManager componentManager) {
        this.uncreatedAction = action;
        setManager(componentManager);
    }

    @Override
    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    @Override
    public String getName() {
        return "CreateActionViewComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.componentManager = manager;
    }

    @Override
    public Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.viewHolder = (ViewHolder) holder;

        viewHolder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentManager.swap("ActionEditComponent");
            }
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.clLayout)       protected ConstraintLayout clLayout;
        @BindView(R.id.ivAdd)          protected ImageView ivAdd;

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
            return new CreateActionViewComponent.ViewHolder(view);
        }
    }
}
