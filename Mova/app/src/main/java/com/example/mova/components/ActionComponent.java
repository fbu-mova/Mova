package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
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
        setManager(new ComponentManager() {
            @Override
            public void onSwap(String fromKey, Component fromComponent, String toKey, Component toComponent) {
                viewHolder.component.clear();
                viewHolder.component.inflateComponent(activity, toComponent);
            }
        });
        this.activity = activity;
        viewComponent = new ActionViewComponent(item, componentManager);
        editComponent = new ActionEditComponent(item, componentManager);
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
        return "ActionComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
//
//        viewHolder.tvAction.setText(item.getTask());
//        viewHolder.tvAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        // TODO -- add component layout, hook clicklistener to there, change inflated layout inside on click to be edit instead of text

        viewHolder.component.inflateComponent(activity, viewComponent);

        viewHolder.component.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentManager.swap("ActionEditComponent");
            }
        });

        viewHolder.ibDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo -- what happens when done or not done, need to keep track
            }
        });

        // todo -- set icons later
    }

    public static class ActionViewHolder extends Component.ViewHolder {

        @BindView(R.id.ibDone)          protected ImageButton ibDone;
        @BindView(R.id.component)       protected ComponentLayout component;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
