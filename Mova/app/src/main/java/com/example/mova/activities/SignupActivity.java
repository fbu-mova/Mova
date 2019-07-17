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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    @BindView(R.id.etUsername)  protected EditText etUsername;
    @BindView(R.id.etPassword)  protected EditText etPassword;
    @BindView(R.id.etEmail)     protected EditText etEmail;
    @BindView(R.id.btSignUp)    protected Button btSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        etUsername.setText(intent.getStringExtra("username"));
        etPassword.setText(intent.getStringExtra("password"));

        // Set OnClickListener
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();

                signUp(username, password, email);
            }
        });
    }

    private void signUp(String username, String password, String email) {
        // Create ParseUser, sign up in background

        ParseUser user = new ParseUser();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Signed up successfully!");

                    // Launch an intent to go to main Personal screen
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Log.e(TAG, "Signing up failed", e);
                    Toast.makeText(SignupActivity.this, "Signing up failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
