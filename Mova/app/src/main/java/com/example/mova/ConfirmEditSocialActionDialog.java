package com.example.mova;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.utils.GoalUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmEditSocialActionDialog  extends DialogFragment {

    private static final String TAG = "confirmEditSocialAction";

    private Action action;
    private boolean isAuthor;
    private String new_task;

    @BindView(R.id.tvOriginalAction)        protected TextView oldTask;
    @BindView(R.id.tvNewAction)             protected TextView newTask;
    @BindView(R.id.tvEffect)                protected TextView tvEffect;
    @BindView(R.id.btConfirm)               protected Button btConfirm;
    @BindView(R.id.btCancel)                protected Button btCancel;

    public ConfirmEditSocialActionDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ConfirmEditSocialActionDialog newInstance(Action action, boolean isAuthor, String new_task) {
        ConfirmEditSocialActionDialog frag = new ConfirmEditSocialActionDialog();
        Bundle args = new Bundle();
        args.putParcelable("action", action);
        args.putBoolean("isAuthor", isAuthor);
        args.putString("new_task", new_task);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_edit_social_action, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        action = getArguments().getParcelable("action");
        isAuthor = getArguments().getBoolean("isAuthor", false);
        new_task = getArguments().getString("new_task", "No new task");

        oldTask.setText(action.getTask());
        newTask.setText(new_task);

        String messageEffect = (isAuthor) ? "If you change this action, we will let the participating users choose whether or not they adopt this change." :
                "If you change this action, it will no longer be connected to the Social Goal parent.";
        tvEffect.setText(messageEffect);

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // casework: if author, then save

                if (isAuthor) {
                    // fixme -- should be same as isPersonal (haven't checked)
                    GoalUtils.saveSharedAndAction(action, new_task, (item) -> {
                        Toast.makeText(getActivity(), "Updated action", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    // additional step: needs to set action's isConnectedToParentSharedAction as false
                    GoalUtils.saveSharedAndAction(action, new_task, (item) -> {
                        action.setIsConnectedToParent(false)
                                .saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getActivity(), "Updated action", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Log.e(TAG, "updating action failed", e);
                                        }
                                    }
                                });
                    });
                }

                ConfirmEditSocialActionDialog.this.dismiss();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fixme -- should automatically change action component to view (haven't checked)
                ConfirmEditSocialActionDialog.this.dismiss();
            }
        });
    }
}
