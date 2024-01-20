package com.example.doanchill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    EditText Username, Email, Password, Confirmpass;
    Button signup;
    TextView signin;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
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
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Username.getText().toString();
                String email = Email.getText().toString();
                String password = Password.getText().toString();
                String confirmpass = Confirmpass.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                signUpWithFirebase();
                signup.setEnabled(false);
                signup.setTextColor(getResources().getColor(R.color.white));

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Confirmpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckInputs();
            }
        });

    }

    private void signUpWithFirebase()
    {
        if(Email.getText().toString().matches("[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+"))
        {
            if(Password.getText().toString().equals(Confirmpass.getText().toString()))
            {
                mAuth.createUserWithEmailAndPassword(Email.getText().toString(), Password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Confirmpass.setError("Password doesn't match.");
                signup.setEnabled(true);
                signup.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            Email.setError("Invalid Email Pattern!");
            signup.setEnabled(true);
            signup.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void CheckInputs()
    {
        if(!Username.getText().toString().isEmpty())
        {
            if(!Email.getText().toString().isEmpty())
            {
                if(!Password.getText().toString().isEmpty() && Password.length() >= 6)
                {
                    if(!Confirmpass.getText().toString().isEmpty())
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
    }
}