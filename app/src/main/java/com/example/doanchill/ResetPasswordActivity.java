package com.example.doanchill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    TextView back;
    EditText email;
    Button resetPassword;
    FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        back = findViewById(R.id.back);
        email = findViewById(R.id.rsEmail);
        resetPassword = findViewById(R.id.resetPassword);
        mauth = FirebaseAuth.getInstance();


       resetPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String Email = email.getText().toString().trim();


               mauth.sendPasswordResetEmail(Email)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(ResetPasswordActivity.this, "Reset Password link has been sent to your registered Email.", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(ResetPasswordActivity.this,SignInActivity.class);
                               startActivity(intent);
                               finish();
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(ResetPasswordActivity.this, "There is an issue in Email! Please try again.", Toast.LENGTH_SHORT).show();

                           }
                       });


           }
       });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}