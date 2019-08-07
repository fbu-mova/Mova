package com.example.mova.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.mova.R;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.ImageComponent;
import com.example.mova.fragments.SocialFragment;
import com.example.mova.icons.Icons;
import com.example.mova.icons.NounProjectClient;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.GroupUtils;
import com.example.mova.utils.TextUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupComposeActivity extends DelegatedResultActivity {

    private User user;
    private Group group;
    private List<String> tags;
    private List<Tag> listtags;

    @BindView(R.id.ibGroupImage)       protected ImageButton ibGroupImage;
    @BindView(R.id.cvIcon)             protected CardView cvIcon;
    @BindView(R.id.ivIcon)             protected ImageView ivIcon;
    @BindView(R.id.etGroupName)        protected EditText etGroupName;
    @BindView(R.id.etGroupDescription) protected EditText etGroupDescription;
    @BindView(R.id.etAddTag2)          protected EditText etAddtag;
    @BindView(R.id.btnAddTag2)         protected Button btnAddTag;
    @BindView(R.id.tvTags2)            protected TextView tvTags;
    @BindView(R.id.btnSave)            protected Button btnSave;
    @BindView(R.id.btnDeleteGroup)     protected Button btnDeleteGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_compose);
        ButterKnife.bind(this);

        user = User.getCurrentUser();

        tags = new ArrayList<>();
        listtags = new ArrayList<>();

        Intent intent = getIntent();

        btnDeleteGroup.setVisibility(View.GONE);

        if (intent.getParcelableExtra("group") == null) {
            group = new Group();
        } else {
            group = intent.getParcelableExtra("group");

            //Show the delete button
            btnDeleteGroup.setVisibility(View.VISIBLE);

            //Allow the group to be deleted
            btnDeleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupComposeActivity.this);
                    builder.setMessage("Are you sure you want to delete this group?")
                            .setTitle("Confirm")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    group.deleteInBackground();
                                    user.relGroups.remove(group, () -> {});
                                    Intent intent1 = new Intent(GroupComposeActivity.this, SocialFragment.class);
                                    startActivity(intent1);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            //Fill in the image
            ParseFile file = group.getGroupPic();
            if (file != null) {
                String imageUrl = file.getUrl();
                Glide.with(GroupComposeActivity.this)
                        .load(imageUrl)
                        .into(ibGroupImage);
            }

            //Fill in the text feilds
            etGroupName.setText(group.getName());
            etGroupDescription.setText(group.getDescription());


            //Fill in the tag feild
            GroupUtils.getTags(group, (oldTagList) -> {
                for (Tag tag: oldTagList) {
                    updateTags(tag.getName(), true);
                }
            });
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set name
                group.setName(etGroupName.getText().toString());

                //Set description
                group.setDescription(etGroupDescription.getText().toString());

                //Change profile pic

                //Set admin
                group.relAdmins.add(user);
                group.relUsers.add(user);
                user.relGroups.add(group);

                group.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        for(int i = 0; i < tags.size() ; i++){
                            listtags.add(new Tag(tags.get(i)));
                        }
                        Tag.saveTags(listtags, (tag, cb) -> {
                            group.relTags.add(tag);
                            tag.relGroups.add(group);
                            tag.saveInBackground();
                        }, (error) -> {});
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

        configureIconClick();
    }

    private void configureIconClick() {
        cvIcon.setOnClickListener((v) -> {
            String term = etGroupName.getText().toString().toLowerCase();
            if (term.equals("")) {
                Toast.makeText(this, "Give your group a name first!", Toast.LENGTH_SHORT).show();
                return;
            }

            View view = getLayoutInflater().inflate(R.layout.layout_recycler_view, null);
            RecyclerView rv = view.findViewById(R.id.rv);

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Choose an icon")
                    .setView(view)
                    .setNegativeButton("Cancel", (dialog, which) -> {})
                    .create();

            List<NounProjectClient.Icon> icons = new ArrayList<>();
            DataComponentAdapter<NounProjectClient.Icon> adapter = new DataComponentAdapter<NounProjectClient.Icon>(this, icons) {
                @Override
                protected Component makeComponent(NounProjectClient.Icon item, Component.ViewHolder holder) {
                    ImageComponent component = new ImageComponent(Icons.highestResImage(item));
                    component.setOnClick(() -> {
                        alertDialog.dismiss();
                        group.setNounIcon(item);
                        Glide.with(GroupComposeActivity.this)
                             .load(Icons.lowestResImage(item))
                             .into(ivIcon);
                        cvIcon.setCardBackgroundColor(Icons.backgroundColor(term));

                        // TODO: Set color on group image, or find a good way to load SVGs for easier color setting

//                             .into(new CustomViewTarget<ImageView, Drawable>(ivIcon) {
//                                 @Override
//                                 protected void onResourceCleared(@Nullable Drawable placeholder) {
//                                    ivIcon.setImageDrawable(placeholder);
//                                 }
//
//                                 @Override
//                                 public void onLoadFailed(@Nullable Drawable errorDrawable) {
//
//                                 }
//
//                                 @Override
//                                 public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                     Bitmap bmp = ColorUtils.changeColorFromBlack(resource, Icons.color(term));
//                                     BitmapDrawable bmpDrawable = new BitmapDrawable(getResources(), bmp);
//                                     TransitionDrawable finalDrawable = new TransitionDrawable(new Drawable[] { resource, bmpDrawable });
//                                     ivIcon.setImageDrawable(finalDrawable);
//                                     finalDrawable.startTransition(200);
//                                     cvIcon.setBackgroundColor(Icons.backgroundColor(term));
//                                 }
//                             });
                    });
                    return component;
                }

                @Override
                protected Component.Inflater makeInflater(NounProjectClient.Icon item) {
                    return new ImageComponent.Inflater();
                }
            };

            rv.setLayoutManager(new GridLayoutManager(this, 4));
            rv.setAdapter(adapter);
            // TODO: Add padding

            Icons.nounIcons(term, 20, (suggestedIcons, e) -> {
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e("GroupComposeActivity", "Failed to load suggested group icons", e);
                        Toast.makeText(this, "Couldn't find any icons for \"" + term + "\"", Toast.LENGTH_LONG).show();
                        // TODO: Create friendlier UI for this
                        // TODO: Differentiate between network errors and no icons found
                        return;
                    }

                    Collections.addAll(icons, suggestedIcons);
                    adapter.notifyDataSetChanged();
                });
            });

            alertDialog.show();
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
