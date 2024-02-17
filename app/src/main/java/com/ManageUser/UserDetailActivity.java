package com.ManageUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SettingAcc.EditProfileActivity;
import com.SettingAcc.SettingAccActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Users;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserDetailActivity extends AppCompatActivity {

    Button changeRole;
    TextView detailName, detailEmail, detailRole;
    ImageView detailImage;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        detailEmail=findViewById(R.id.detailEmail);
        detailName=findViewById(R.id.DetailName);
        detailRole=findViewById(R.id.detailRole);
        detailImage=findViewById(R.id.detailUserImage);
        changeRole=findViewById(R.id.changeRole);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        Bundle extraData=intent.getExtras();
        Users users1=(Users) extraData.getSerializable("user");
        ID=extraData.getString("ID");
        detailName.setText(users1.getName());
        detailEmail.setText(users1.getEmail());
        detailRole.setText(users1.getRole());
        Glide.with(this).load(users1.getImage()).into(detailImage);
        changeRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(users1.getRole(), "Moderator"))
                {
                    DocumentReference documentReference=fStore.collection("users").document(ID);
                    Map<String,Object> edited=new HashMap<>();
                    edited.put("email",users1.getEmail());
                    edited.put("fName",users1.getName());
                    edited.put("image",users1.getImage());
                    edited.put("role","User");
                    documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UserDetailActivity.this, "Set User", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserDetailActivity.this, ManageUserActivity.class));
                            finish();
                        }
                    });
                }
                if(Objects.equals(users1.getRole(), "User")) {
                    DocumentReference documentReference=fStore.collection("users").document(ID);
                    Map<String,Object> edited=new HashMap<>();
                    edited.put("email",users1.getEmail());
                    edited.put("fName",users1.getName());
                    edited.put("image",users1.getImage());
                    edited.put("role","Moderator");
                    documentReference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UserDetailActivity.this, "Set Moderator", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UserDetailActivity.this, ManageUserActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }
}