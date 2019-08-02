package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalComposeActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.utils.GoalUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateActionComponent extends Component {

    private static final String TAG = "createAction comp";
    private static final int viewLayoutRes = R.layout.item_action; // consists only of a component layout

    private CreateActionViewComponent viewComponent;
    private ActionEditComponent editComponent;
    private ComponentManager componentManager;

    private ViewHolder viewHolder;

    private Action uncreatedAction;
    private GoalComposeActivity.HandleCreateAction handler;

    public CreateActionComponent(GoalComposeActivity.HandleCreateAction handler) {
        super();
        this.uncreatedAction = new Action();
        this.handler = handler;
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
    public String getName() {
        return "CreateActionComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {
        // set up componentManager

        setManager(new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                viewHolder.component.clear();
                viewHolder.component.inflateComponent(getActivity(), toComponent);
            }
        });

        viewComponent = new CreateActionViewComponent(uncreatedAction, componentManager);
        editComponent = new ActionEditComponent(uncreatedAction, componentManager, new GoalUtils.onActionEditSaveListener() {
            @Override
            public void call(String task, ComponentManager manager) {
                // todo -- want to save the string task and the updated icons logic to the unsavedAction

                Log.i(TAG, "editComp save button pressed");

                uncreatedAction.setTask(task)
                        .setIsConnectedToParent(true)
                        .setIsDone(true); // fixme -- add icon logic setting

                uncreatedAction.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // want to pass uncreatedAction back to GoalComposeActivity for its recyclerview
                            handler.call(uncreatedAction);
                            manager.swap("CreateActionViewComponent");
                        }
                        else {
                            Log.d(TAG, "could not save action", e);
                        }
                    }
                });

            }
        });

        componentManager.launch(viewComponent.getName(), viewComponent);
        componentManager.launch(editComponent.getName(), editComponent);
    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.viewHolder = (ViewHolder) holder;

        viewHolder.component.inflateComponent(getActivity(), viewComponent);

        viewHolder.component.setOnClickListener(new View.OnClickListener() {
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
            return new CreateActionComponent.ViewHolder(view);
        }
    }
}
