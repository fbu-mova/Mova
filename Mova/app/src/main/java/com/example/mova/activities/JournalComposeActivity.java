package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.model.Tag;
import com.example.mova.utils.TimeUtils;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalComposeActivity extends AppCompatActivity {

    public static final String KEY_COMPOSED_POST = "post";
    public static final String KEY_COMPOSED_POST_TAGS = "tags";
    public static final int COMPOSE_REQUEST_CODE = 30;

    // TODO: Perhaps change this to List<Tag>?
    ArrayList<String> tags;

    @BindView(R.id.tvTime)       protected TextView tvTime;
    @BindView(R.id.tvLocation)   protected TextView tvLocation;
    @BindView(R.id.etBody)       protected EditText etBody;
    @BindView(R.id.bSave)        protected Button bSave;

    @BindView(R.id.etTag)        protected EditText etTag;
    @BindView(R.id.tvTags)       protected TextView tvTags;
    @BindView(R.id.bAddTag)      protected Button bAddTag;

    @BindView(R.id.moodSelector) protected Mood.SelectorLayout moodSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_compose);
        ButterKnife.bind(this);

        tags = new ArrayList<>();

        Date startDate = new Date();
        final double lat = 0, lon = 0; // TODO: Get location

        tvTime.setText(TimeUtils.toTimeString(startDate));
        tvLocation.setText("Seattle, WA, USA"); // TODO: Get location
        // TODO: Build and handle mood selection
        // TODO: Build tag removal

        bAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagName = etTag.getText().toString();
                if (tagName.equals("")) {
                    Toast.makeText(JournalComposeActivity.this, "Write a tag first!", Toast.LENGTH_LONG).show();
                } else {
                    updateTags(tagName, true);
                    etTag.setText("");
                }
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etBody.getText().toString();
                User user = (User) ParseUser.getCurrentUser();
                Date endDate = new Date();
                ParseGeoPoint location = new ParseGeoPoint();
                location.setLatitude(lat);
                location.setLongitude(lon);
                Mood.Status mood = moodSelector.getSelectedItem();

                ArrayList<Tag> tagObjects = new ArrayList<>();
                for (String s : tags) {
                    tagObjects.add(new Tag(s));
                }

                // TODO: Handle media

                if (body.equals("")) {
                    // TODO: Move all Toast strings to a strings.xml file for cleaner code
                    Toast.makeText(JournalComposeActivity.this, "Write an entry first!", Toast.LENGTH_LONG).show();
                } else {
                    Post post = new Post()
                            .setIsPersonal(true)
                            .setAuthor(user)
                            .setBody(body)
                            .setLocation(location)
                            .setMood(mood);

                    getIntent().putExtra(KEY_COMPOSED_POST, post);
                    getIntent().putExtra(KEY_COMPOSED_POST_TAGS, tagObjects);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }
        });
    }

    private void updateTags(String tag, boolean shouldKeep) {
        if (shouldKeep && !tags.contains(tag)) {
            tags.add(tag);
        } else {
            tags.remove(tag);
        }
        writeTags(tags, tvTags);
    }

    public static void writeTags(ArrayList<String> tags, TextView tvTags) {
        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            tagsBuilder.append(tags.get(i));
            if (i < tags.size() - 1) tagsBuilder.append(", ");
        }
        tvTags.setText(tagsBuilder.toString());
    }

    // FIXME: List vs. ArrayList is extremely hacky, must be a better way to pass that in
    public static void writeTags(List<Tag> tags, TextView tvTags) {
        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            tagsBuilder.append(tags.get(i).getName());
            if (i < tags.size() - 1) tagsBuilder.append(", ");
        }
        tvTags.setText(tagsBuilder.toString());
    }
}
