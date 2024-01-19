package com.example.doanchill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    EditText Username, Email, Password, Confirmpass;
    Button signup;
    TextView signin;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Username = findViewById(R.id.username);
        Email = findViewById(R.id.emailid);
        Password = findViewById(R.id.passwordps);
        Confirmpass = findViewById(R.id.confirmpassword);
        signup = findViewById(R.id.signup);
        signin = findViewById(R.id.signin);
        progressBar = findViewById(R.id.progressBar);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Username.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String confirmpass = Confirmpass.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                if(!username.isEmpty())
                {
                    if(!email.isEmpty())
                    {
                        if(!password.isEmpty() && password.length() >= 6)
                        {
                            if(!confirmpass.isEmpty())
                            {
                                signup.setEnabled(true);
                                signup.setTextColor(getResources().getColor(R.color.white));
                            }
                            else {
                                signup.setEnabled(false);
                                signup.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                        else {
                            signup.setEnabled(false);
                            signup.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                    else {
                        signup.setEnabled(false);
                        signup.setTextColor(getResources().getColor(R.color.white));
                    }
                }
                else {
                    signup.setEnabled(false);
                    signup.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}