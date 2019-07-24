package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Action;
import com.parse.ParseException;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionEditComponent extends Component {

    private static final int viewLayoutRes = R.layout.item_action_edit;
    private static final String TAG = "action edit comp";

    private Action action;
    private View view;
    private ActionEditViewHolder viewHolder;
    private DelegatedResultActivity activity;

    public ActionEditComponent(Action action) {
        super();
        this.action = action;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        viewHolder = new ActionEditViewHolder(view);
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
    public void render() {

        // todo -- implement icons (need to update in action model)

        viewHolder.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update this action with new text
                String new_action = viewHolder.etAction.getText().toString();
                saveAction(action, new_action);

                // TODO -- needs to set component layout back to old...
            }
        });
    }

    private void saveAction(Action action, String new_action) {
        action.setTask(new_action);

        action.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "action saved successfully");
                    Toast.makeText(activity, "Updated action", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e(TAG, "action failed saving", e);
                }
            }
        });
    }

    public static class ActionEditViewHolder extends Component.ViewHolder {

        @BindView(R.id.etAction)        protected EditText etAction;
        @BindView(R.id.ivIcon1)         protected ImageView ivIcon1; // fixme -- in future, image buttons
        @BindView(R.id.ivIcon2)         protected ImageView ivIcon2;
        @BindView(R.id.ivIcon3)         protected ImageView ivIcon3;
        @BindView(R.id.btSave)        protected Button btSave;

        public ActionEditViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
