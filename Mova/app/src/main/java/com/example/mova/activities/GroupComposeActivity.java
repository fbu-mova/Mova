package com.example.mova.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mova.R;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.TextUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupComposeActivity extends AppCompatActivity {

    User user;
    Group group;
    private List<String> tags;

    @BindView(R.id.ibGroupImage)
    ImageButton ibGroupImage;

    @BindView(R.id.etGroupName)
    EditText etGroupName;

    @BindView(R.id.etGroupDescription)
    EditText etGroupDesctiption;

    @BindView(R.id.etAddTag2)
    EditText etAddtag;

    @BindView(R.id.btnAddTag2)
    Button btnAddTag;

    @BindView(R.id.tvTags2)
    TextView tvTags;

    @BindView(R.id.btnSave)
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_compose);
        ButterKnife.bind(this);

        user = User.getCurrentUser();
        group = new Group();
        tags = new ArrayList<>();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set name
                group.setName(etGroupName.getText().toString());

                //Set description
                group.setDescription(etGroupDesctiption.getText().toString());

                //Change profile pic

                //Set admin
                group.relAdmins.add(user);
                group.relUsers.add(user);
                user.relGroups.add(group);

                group.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //Add tags
                        for(String tagString: tags){
                            Tag tag = new Tag(tagString);
                            group.relTags.add(tag);
                            tag.relGroups.add(group);
                            tag.saveInBackground();
                        }

                        user.saveInBackground();
                        group.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(GroupComposeActivity.this, "Group has been created", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });

            }
        });

        ibGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo - allow user to add image
            }
        });


        btnAddTag.setOnClickListener((view) -> {
            String tagName = etAddtag.getText().toString();
            if (tagName.equals("")) {
                Toast.makeText(GroupComposeActivity.this, "Write a tag first!", Toast.LENGTH_LONG).show();
            } else {
                updateTags(tagName, true);
                etAddtag.setText("");
            }
        });
    }

    private void updateTags(String tag, boolean shouldKeep) {
        if (shouldKeep && !tags.contains(tag)) {
            tags.add(tag);
        } else {
            tags.remove(tag);
        }
        TextUtils.writeCommaSeparated(tags, "No tags", tvTags, (str) -> str);
    }
}
