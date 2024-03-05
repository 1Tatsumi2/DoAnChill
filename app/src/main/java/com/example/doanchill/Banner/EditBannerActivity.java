package com.example.doanchill.Banner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.SettingAcc.EditProfileActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Models.SliderModel;
import com.example.doanchill.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class EditBannerActivity extends AppCompatActivity {
    ImageView image;
    Button save;
    boolean isImageUpdated = false;
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    Uri uriImage;
    String oldImageUrl,imageUrl,key;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_banner);
        save=findViewById(R.id.saveBanner);
        image=findViewById(R.id.EditBannerImage);
        Intent intent=getIntent();
        Bundle extraData=intent.getExtras();
        key=extraData.getString("key");
        oldImageUrl=extraData.getString("image");
        imageUrl=oldImageUrl;
        Glide.with(EditBannerActivity.this).load(imageUrl).into(image);
        activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK)
                        {
                            Intent data=o.getData();
                            uriImage=data.getData();
                            image.setImageURI(uriImage);
                        }
                        else {
                            Toast.makeText(EditBannerActivity.this,"No Image selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImageUpdated=true;
                Intent i=new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                activityResultLauncher.launch(i);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(EditBannerActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        android.app.AlertDialog dialog= builder.create();
        dialog.show();
        if (isImageUpdated)
        {
            StorageReference storageReferenceImg = FirebaseStorage.getInstance().getReference().child("Banner Images")
                    .child(uriImage.getLastPathSegment());
            storageReferenceImg.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();
                            UpdateData();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        else
        {
            UpdateData();
        }
    }

    private void UpdateData() {
        SliderModel sliderModel=new SliderModel(imageUrl);
        DocumentReference ref=fStore.collection("banner").document(key);
        ref.set(sliderModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(!Objects.equals(imageUrl, oldImageUrl))
                {
                    FirebaseStorage storage=FirebaseStorage.getInstance();
                    StorageReference imageReference = storage.getReferenceFromUrl(oldImageUrl);
                    imageReference.delete();
                }
                Toast.makeText(EditBannerActivity.this, "Update success", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}