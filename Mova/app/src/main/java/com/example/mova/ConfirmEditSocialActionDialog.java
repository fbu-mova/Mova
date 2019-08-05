package com.example.mova;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mova.model.Action;
import com.example.mova.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmEditSocialActionDialog  extends DialogFragment {

    private static final String TAG = "confirmEditSocialAction";

    private Action action;
    private boolean isAuthor;

    @BindView(R.id.tvOriginalAction)        protected TextView oldTask;
    @BindView(R.id.tvNewAction)             protected TextView newTask;
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
    }
}
