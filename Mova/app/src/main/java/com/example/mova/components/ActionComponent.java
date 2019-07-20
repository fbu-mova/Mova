package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Action;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionComponent extends Component<Action> {

    private static final String TAG = "action comp";
    private static final int viewLayoutRes = R.layout.item_action;

    private View view;
    private ActionViewHolder viewHolder;
    private DelegatedResultActivity activity;

    public ActionComponent(Action item) {
        super(item);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new ActionViewHolder(view);
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
    public void render() {

        Action action = getItem();

        viewHolder.rbTask.setText(action.getTask());

        // todo -- set icons later
    }

    public static class ActionViewHolder extends Component.ViewHolder {

        @BindView(R.id.rbTask)      protected RadioButton rbTask;
        @BindView(R.id.ivIcon)      protected ImageView ivIcon; // might need to be ImageButton

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
