package com.example.mova;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // testing to make sure parse sdk android studio setup is correct

    EditText etUsername;
    EditText etPassword;
    Button btLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btLogIn = findViewById(R.id.btLogIn);

        btLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "failede", Toast.LENGTH_LONG).show();
                            Log.e("Log in", "failed", e);
                        }
                    }
                });
            }
        });

    }

//    public void logIn(Button button) {
//        String username = etUsername.getText().toString();
//        String password = etPassword.getText().toString();
//
//        ParseUser.logInInBackground(username, password, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (e == null) {
//                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "failede", Toast.LENGTH_LONG).show();
//                    Log.e("Log in", "failed", e);
//                }
//            }
//        });
//    }
}
