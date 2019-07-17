package com.example.mova.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mova.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JournalComposeActivity extends AppCompatActivity {

    public static final String KEY_COMPOSED_POST = "post";

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

        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm aa", Locale.US);

        tvTime.setText(timeFmt.format(startDate));
        tvLocation.setText("Seattle, WA, USA"); // TODO: Get location
        // TODO: Build and handle mood selection

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etBody.getText().toString();
//                Media media = null; // TODO: Get embedded media
                ParseUser user = ParseUser.getCurrentUser();
                Date endDate = new Date();
                ParseGeoPoint location = new ParseGeoPoint();
                location.setLatitude(lat);
                location.setLongitude(lon);

                // TODO: Create journal post, add to intent
            }
        });
    }
}
