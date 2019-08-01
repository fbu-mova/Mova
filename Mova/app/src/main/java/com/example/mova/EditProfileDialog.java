package com.example.mova;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mova.model.User;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileDialog extends DialogFragment {

    private User user = User.getCurrentUser();
    private Boolean changedPassword = false;

    @BindView(R.id.ivEditProfilePic)
    ImageView ivEditProfilePic;

    @BindView(R.id.etEditUsername)
    EditText etEditUsername;

    @BindView(R.id.etEditEmail)
    EditText etEditEmail;

    @BindView(R.id.etEditPassword)
    EditText etEditPassword;

    @BindView(R.id.etConfirmNewPassword)
    EditText etConfirmNewPassword;

    public EditProfileDialog(){}

    public static EditProfileDialog newInstance(){
        EditProfileDialog frag = new EditProfileDialog();
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.dialog_edit_profile, null);
        ButterKnife.bind(this,view);

        etEditUsername.setText(user.getUsername());
        etEditEmail.setText(user.getEmail());
        ParseFile file = user.getProfilePic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .transform(new CenterCrop(), new RoundedCorners(150))
                    .error(R.color.colorAccent) // todo - replace to be better image, add rounded corners
                    .placeholder(R.color.colorAccent)
                    .into(ivEditProfilePic);
        }

        etEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedPassword = true;
            }
        });

        ivEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - change profile pic
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit profile")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setUsername(etEditUsername.getText().toString());
                        user.setEmail(etEditEmail.getText().toString());
                        if(changedPassword){
                            if(etEditPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())) {
                                user.setPassword(etEditPassword.getText().toString());
                            }else{
                                Toast.makeText(getContext(), "Confirm Password does not match password entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                        user.saveInBackground();
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
