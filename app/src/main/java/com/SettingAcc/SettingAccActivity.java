package com.SettingAcc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doanchill.R;
import com.example.doanchill.SignUpActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SettingAccActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button verifySend,editBtn;
    TextView verfiyNofi;
    String userID,name,email,imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_acc);
        verfiyNofi=findViewById(R.id.verifyNofi);
        verifySend=findViewById(R.id.verify);
        editBtn=findViewById(R.id.editProfile);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID=fAuth.getCurrentUser().getUid();
        FirebaseUser user=fAuth.getCurrentUser();
        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name=(value.getString("fName"));
                email=(value.getString("email"));
                imageUrl=value.getString("image");
            }
        });
        if(user.isEmailVerified())
        {
           verfiyNofi.setVisibility(View.INVISIBLE);
           verifySend.setVisibility(View.INVISIBLE);
        }
        else {
            verifySend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SettingAccActivity.this, "Verification mail has been send to your email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(SettingAccActivity.this, EditProfileActivity.class);
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("image",imageUrl);
                startActivity(i);
            }
        });
    }
}