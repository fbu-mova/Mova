package com.example.mova;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileDialog extends DialogFragment {

    @BindView(R.id.ivEditProfilePic)
    ImageView ivEditProfilePic;

    @BindView(R.id.etEditUsername)
    EditText etEditUsername;

    @BindView(R.id.etEditPassword)
    EditText etEditPassword;

    @BindView(R.id.etConfirmNewPassword)
    EditText etConfirmNewPassword;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.dialog_edit_profile, null);
        ButterKnife.bind(this,view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit profile")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Save to user
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
