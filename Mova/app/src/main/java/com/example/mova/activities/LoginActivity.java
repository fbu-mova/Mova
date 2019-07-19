package com.example.mova.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mova.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends DelegatedResultActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.etUsername)  protected EditText etUsername;
    @BindView(R.id.etPassword)  protected EditText etPassword;
    @BindView(R.id.btLogIn)     protected Button btLogIn;
    @BindView(R.id.btSignUp)    protected Button btSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Keep user logged in
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Fill feild
        Intent intent = getIntent();
        etUsername.setText(intent.getStringExtra("username"));

        // Set OnClickListeners

        btLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                logIn(username, password);
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Launch an intent to go to SignupActivity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.putExtra("username", etUsername.getText().toString());
                intent.putExtra("password", etPassword.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void logIn(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Logged in successfully!");

                    // Launch an intent to go to main Personal page
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Log.e(TAG, "Log in failed", e);
                    Toast.makeText(LoginActivity.this, "Log in failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
