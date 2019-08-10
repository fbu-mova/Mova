package com.example.mova.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mova.PersonalSocialToggle;
import com.example.mova.R;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.fragments.SocialFragment;
import com.example.mova.model.User;
import com.parse.ParseACL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends DelegatedResultActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };

//    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.bottom_navigation) protected PersonalSocialToggle bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        User currUser = User.getCurrentUser();
        ParseACL acl = new ParseACL(currUser);
        acl.setPublicReadAccess(true);
        currUser.setACL(acl);

        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
    }

    private void initFragments() {
        bottomNavigationView.setOnToggle((toPersonal) -> setFragment(toPersonal));
        setFragment(true);
    }

    protected void setFragment(boolean toPersonal) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment;
        if (toPersonal) {
            fragment = new PersonalFragment();
        } else {
            fragment = new SocialFragment();
        }
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
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
