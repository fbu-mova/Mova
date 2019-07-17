package com.example.mova.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mova.R;
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.fragments.SocialFragment;
import com.example.mova.model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.action_personal:
                        Toast.makeText(MainActivity.this, "Switched to Personal", Toast.LENGTH_SHORT).show();
                        fragment = new PersonalFragment();
                        break;
                    case R.id.action_social:
                        Toast.makeText(MainActivity.this, "Switched to Social", Toast.LENGTH_SHORT).show();
                        fragment = new SocialFragment();
                        break;
                    default: return true;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_personal);

        // Log in temporary user
        // TODO: Move this to a login activity with real user details
        try {
            ParseUser.logIn("temp", "temp");
            Log.d("MainActivity", "Logged in temp user");
        } catch (ParseException e) {
            Log.e("MainActivity", "Could not log in user", e);
            Toast.makeText(this, "Could not log in user", Toast.LENGTH_LONG).show();
        }
    }
}
