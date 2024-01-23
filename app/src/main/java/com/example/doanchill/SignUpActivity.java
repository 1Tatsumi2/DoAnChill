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
                String user=Email.getText().toString();
                String pass=Password.getText().toString();
                String comfirm=Confirmpass.getText().toString();
                if(user.isEmpty())
                {
                    Email.setError("Email cannot be empty");
                }
                if(pass.isEmpty())
                {
                    Password.setError("Password cannot be empty");
                }
                if(comfirm.isEmpty())
                {
                    Password.setError("Confirm password cannot be empty");
                }
                if(!comfirm.equals(pass))
                {
                    Confirmpass.setError("Password and Confirm password must equal");
                }
                else {
                    mAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(SignUpActivity.this,"SignUp Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                            }
                            else {
                                Toast.makeText(SignUpActivity.this,"SignUp Failed" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });
    }
}