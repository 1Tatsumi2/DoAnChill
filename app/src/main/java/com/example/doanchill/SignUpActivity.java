package com.example.doanchill;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    EditText Username, Email, Password, Confirmpass;
    Button signup;
    TextView signin;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
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
                progressBar.setVisibility(View.VISIBLE);
                String user = Username.getText().toString();
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String confirm = Confirmpass.getText().toString().trim();

                if (user.isEmpty()) {
                    Username.setError("Username cannot be empty");
                }
                if (email.isEmpty()) {
                    Email.setError("Email cannot be empty");
                }
                if (password.isEmpty()) {
                    Password.setError("Password cannot be empty");
                }
                if (confirm.isEmpty()) {
                    Confirmpass.setError("Confirm pass cannot be empty");
                } else {

                                mAuth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
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