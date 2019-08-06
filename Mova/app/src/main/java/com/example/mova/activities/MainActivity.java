package com.example.mova.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mova.R;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.fragments.SocialFragment;
import com.example.mova.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseACL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends DelegatedResultActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };

    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        User currUser = User.getCurrentUser();
        ParseACL acl = new ParseACL(currUser);
        acl.setPublicReadAccess(true);
        currUser.setACL(acl);

        updateRecurrences();

        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
    }

    private void initFragments() {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.action_personal:
                        //Toast.makeText(MainActivity.this, "Switched to Personal", Toast.LENGTH_SHORT).show();
                        fragment = new PersonalFragment();
                        break;
                    case R.id.action_social:
                        //Toast.makeText(MainActivity.this, "Switched to Social", Toast.LENGTH_SHORT).show();
                        fragment = new SocialFragment();
                        break;
                    default: return true;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_personal);
    }

    private void updateRecurrences() {
        // TODO: Potentially launch this on a new thread.
        User.getCurrentUser().updateRecurrences();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (allPermissionsGranted()) {
                initFragments();
            } else {
                finish();
                // TODO: Create a better fallback for permissions not being granted
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            boolean granted = ContextCompat.checkSelfPermission(getBaseContext(), permission) == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                return false;
            }
        }
        return true;
    }
}
