package com.example.doanchill;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanchill.Fragments.TrangChuFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    TextView signup,resetpass;
    EditText Username, Password;
    Button signin;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
     signup = findViewById(R.id.signupps);
     resetpass = findViewById(R.id.rspass);
     Username = findViewById(R.id.email);
     Password = findViewById(R.id.pass);
     signin = findViewById(R.id.entersignin);
     progressBar = findViewById(R.id.progressBar2);
     auth=FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Username.getText().toString();
                String pass = Password.getText().toString();
//                progressBar.setVisibility(View.VISIBLE);

                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    if(!pass.isEmpty())
                    {
                        auth.signInWithEmailAndPassword(email,pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(SignInActivity.this,"Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignInActivity.this, TrangChuActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignInActivity.this,"Email or Password is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        Password.setError("Password cannot be empty");
                    }
                }
                else if(email.isEmpty()) {
                    Username.setError("Email cannot be empty");
                }
                else {
                    Username.setError("Please enter valid email");
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}