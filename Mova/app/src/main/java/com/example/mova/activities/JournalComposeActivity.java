package com.example.mova.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mova.Mood;
import com.example.mova.R;
import com.example.mova.components.Component;
import com.example.mova.components.ComponentLayout;
import com.example.mova.model.Media;
import com.example.mova.model.Tag;
import com.example.mova.utils.TextUtils;
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

public class JournalComposeActivity extends DelegatedResultActivity {

    // Incoming intent keys
    /** The key for the entry's mood if one already exists, passed as the string value of the Mood.Status. */
    public static final String KEY_MOOD = "mood";
    /** The key for the body of the entry if one already exists. */
    public static final String KEY_BODY = "body";
    /** The key for the embedded media of the entry if it exists, passed as a ParseObject. */
    public static final String KEY_MEDIA = "media";

    // Outgoing intent keys
    public static final String KEY_COMPOSED_POST = "post";
    public static final String KEY_COMPOSED_POST_TAGS = "tags";

    public static final int COMPOSE_REQUEST_CODE = 30;

    List<String> tags;

    @BindView(R.id.tvTime)       protected TextView tvTime;
    @BindView(R.id.tvLocation)   protected TextView tvLocation;
    @BindView(R.id.etBody)       protected EditText etBody;
    @BindView(R.id.bSave)        protected Button bSave;

    @BindView(R.id.etTag)        protected EditText etTag;
    @BindView(R.id.tvTags)       protected TextView tvTags;
    @BindView(R.id.bAddTag)      protected Button bAddTag;

    @BindView(R.id.moodSelector) protected Mood.SelectorLayout moodSelector;
    @BindView(R.id.clMedia)      protected ComponentLayout clMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_compose);
        ButterKnife.bind(this);

        tags = new ArrayList<>();
        loadIncomingExtras();

        // Store all immediate information relevant to post
        Date startDate = new Date();
        final double lat = 0, lon = 0; // TODO: Get location

        tvTime.setText(TimeUtils.toTimeString(startDate));
        tvLocation.setText("Seattle, WA, USA"); // TODO: Get location
        // TODO: Build tag removal

        bAddTag.setOnClickListener((view) -> {
            String tagName = etTag.getText().toString();
            if (tagName.equals("")) {
                Toast.makeText(JournalComposeActivity.this, "Write a tag first!", Toast.LENGTH_LONG).show();
            } else {
                updateTags(tagName, true);
                etTag.setText("");
            }
        });

        bSave.setOnClickListener((view) -> {
            String body = etBody.getText().toString();
            User user = (User) ParseUser.getCurrentUser();
            Date endDate = new Date();
            // FIXME: Maybe calculate the location on postJournalEntry to keep this running quickly?
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
        });

        // If embedded media exists, load it into its container
        Media media = getIntent().getParcelableExtra(KEY_MEDIA);
        Component mediaComponent = (media == null) ? null : media.makeComponent();
        if (mediaComponent != null) {
            clMedia.inflateComponent(this, mediaComponent);
        }
    }

    private void updateTags(String tag, boolean shouldKeep) {
        if (shouldKeep && !tags.contains(tag)) {
            tags.add(tag);
        } else {
            tags.remove(tag);
        }
        TextUtils.writeCommaSeparated(tags, "No tags", tvTags, (str) -> str);
    }

    private void loadIncomingExtras() {
        Intent data = getIntent();
        String moodName = data.getStringExtra(KEY_MOOD);
        if (moodName != null && !moodName.equals("")) {
            moodSelector.setItem(Mood.Status.valueOf(moodName));
        }
        String body = data.getStringExtra(KEY_BODY);
        if (body != null && !body.equals("")) {
            etBody.setText(body);
        }
    }
}
