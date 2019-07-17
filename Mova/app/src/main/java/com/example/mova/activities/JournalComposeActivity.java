package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.TimeUtils;
import com.example.mova.model.Post;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalComposeActivity extends AppCompatActivity {

    public static final String KEY_COMPOSED_POST = "post";
    public static final int COMPOSE_REQUEST_CODE = 30;

    @BindView(R.id.tvTime)      protected TextView tvTime;
    @BindView(R.id.tvLocation)  protected TextView tvLocation;
    @BindView(R.id.etBody)      protected EditText etBody;
    @BindView(R.id.bSave)       protected Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_compose);
        ButterKnife.bind(this);

        Date startDate = new Date();
        final double lat = 0, lon = 0; // TODO: Get location

        tvTime.setText(TimeUtils.getTime(startDate));
        tvLocation.setText("Seattle, WA, USA"); // TODO: Get location
        // TODO: Build and handle mood selection

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etBody.getText().toString();
                ParseUser user = ParseUser.getCurrentUser();
                Date endDate = new Date();
                ParseGeoPoint location = new ParseGeoPoint();
                location.setLatitude(lat);
                location.setLongitude(lon);
                // TODO: Handle media

                if (body.equals("")) {
                    // TODO: Move all strings to a strings.xml file for cleaner code
                    Toast.makeText(JournalComposeActivity.this, "Write an entry first!", Toast.LENGTH_LONG).show();
                } else {
                    Post post = new Post()
                            .setIsPersonal(true)
                            .setAuthor(user)
                            .setBody(body)
//                            .setGroup(null) // FIXME: How to explicitly set this to none without crashing?
                            .setLocation(location);

                    getIntent().putExtra(KEY_COMPOSED_POST, post);
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }
        });
    }
}
