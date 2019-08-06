package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.mova.ConfirmEditSocialActionDialog;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionComponent extends Component {

    private static final String TAG = "action comp";
    protected static final int viewLayoutRes = R.layout.item_action;

    private Action item;
    protected ViewHolder viewHolder;

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
                viewHolder.component.inflateComponent(getActivity(), toComponent);

                if (toKey.equals(editComponent.getName())) {
                    ((ActionEditComponent.ActionEditViewHolder) editComponent.getViewHolder()).etAction
                            .setText(item.getTask());
                }
            }
        });

        viewComponent = new ActionViewComponent(item, componentManager);
        editComponent = new ActionEditComponent(item, componentManager, new GoalUtils.onActionEditSaveListener() {
            @Override
            public void call(Action action, Action.Wrapper wrapper, ComponentManager manager) {
                // editing a social goal - two cases (if author, if not author)

                // update this action with new text
                String new_action = wrapper.getMessage();

                if (!action.getParentGoal().getIsPersonal()) { // fixme -- getParentGoal doesn't return whole goal

                    confirmEdit((User.getCurrentUser() == action.getParentGoal().getAuthor()), new_action); // fixme, getParentGoal again
                    // includes case where editing personal version of a social goal,
                    //  so action saved, connected to SharedAction set to false, sharedAction not changed

                }
                else {
                    // saving logic for isPersonal
                    GoalUtils.saveSharedAndAction(action, new_action, (item) -> {
                        Toast.makeText(getActivity(), "Updated action", Toast.LENGTH_SHORT).show();
                    });
                }

                manager.swap("ActionViewComponent");
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

        // todo -- set icons later
    }

    @Override
    protected void onDestroy() {

    }

    private void confirmEdit(boolean isAuthor, String new_task) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ConfirmEditSocialActionDialog confirmEditSocialActionDialog = ConfirmEditSocialActionDialog.newInstance(item, isAuthor, new_task);
        confirmEditSocialActionDialog.show(fm, "showingConfirmEditSocialActionDialog");
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
