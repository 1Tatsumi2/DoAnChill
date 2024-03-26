package com.SettingAcc;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ManageUser.ManageUserActivity;
import com.ManageUser.UserDetailActivity;
import com.MusicManager.UpdateActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Fragments.SettingsFragment;
import com.example.doanchill.R;
import com.example.doanchill.SignInActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView editImage;
    Button saveBtn;
    String name, email, imageUrl,oldEmail,oldImage;
    EditText editName,editEmail;
    Uri uriImage;
    boolean isImageUpdated = false,premium;
    String role;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        saveBtn=findViewById(R.id.saveProfile);
        Intent data=getIntent();
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            name=bundle.getString("name");
            email=bundle.getString("email");
            oldEmail=bundle.getString("email");
            oldImage=bundle.getString("image");
            role=bundle.getString("role");
            premium=bundle.getBoolean("premium");
        }
        imageUrl=oldImage;
        editEmail=findViewById(R.id.editUserEmail);
        editName=findViewById(R.id.editUsername);
        editImage=findViewById(R.id.editUserImage);
        editEmail.setText(email);
        editName.setText(name);
        Glide.with(EditProfileActivity.this).load(imageUrl).into(editImage);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        user=fAuth.getCurrentUser();
        activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK)
                        {
                            Intent data=o.getData();
                            uriImage=data.getData();
                            editImage.setImageURI(uriImage);
                        }
                        else {
                            Toast.makeText(EditProfileActivity.this,"No Image selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // The image capture intent was successful, start the cropping activity
                        UCrop.of(uriImage, uriImage)
                                .withAspectRatio(1, 1)
                                .start(this);
                        editImage.setImageURI(uriImage);
                    }
                }
        );
        cropImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // The cropping activity was successful, get the resulting Uri
                        Uri croppedImageUri = UCrop.getOutput(result.getData());
                        // Use croppedImageUri
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        Throwable cropError = UCrop.getError(result.getData());
                        // Handle possible errors here
                    }
                }
        );

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImageUpdated=true;
                imagePickDialog();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        email=editEmail.getText().toString();
        name=editName.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            editName.setError("Name cannot be empty");
            return;
        }
        if(TextUtils.isEmpty(email))
        {
            editEmail.setError("Email cannot be empty");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editEmail.setError("Please enter the valid email");
            return;
        }
        if(uriImage==null)
        {
            imageUrl=oldImage;
        }
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(name))
        {
            android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(EditProfileActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            android.app.AlertDialog dialog= builder.create();
            dialog.show();
            if (isImageUpdated && uriImage!=null)
            {
                StorageReference storageReferenceImg = FirebaseStorage.getInstance().getReference().child("Android Images")
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
    }

    private void UpdateData() {
        email=editEmail.getText().toString();
        name=editName.getText().toString();
        if(uriImage==null)
        {
            imageUrl=oldImage;
        }
        if(Objects.equals(email, oldEmail))
        {
            DocumentReference documentReference=fStore.collection("users").document(user.getUid());
            Map<String,Object> edited=new HashMap<>();
            edited.put("email",email);
            edited.put("fName",name);
            edited.put("image",imageUrl);
            edited.put("role",role);
            edited.put("premium",premium);
            documentReference.set(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditProfileActivity.this, "Update success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfileActivity.this, SettingAccActivity.class));
                    finish();
                }
            });
        }
        if(!Objects.equals(email, oldEmail))
        {
            user.verifyBeforeUpdateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(EditProfileActivity.this, "Verification mail has been sent, you should log out", Toast.LENGTH_SHORT).show();
                    DocumentReference documentReference=fStore.collection("users").document(user.getUid());
                    Map<String,Object> edited=new HashMap<>();
                    edited.put("email",email);
                    edited.put("fName",name);
                    edited.put("image",imageUrl);
                    edited.put("role",role);
                    edited.put("premium",premium);
                    documentReference.set(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            fAuth.signOut();
                            finish();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditProfileActivity.this, SettingAccActivity.class));
        finish();
    }

    private void imagePickDialog() {
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        //set items/options
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle click
                if(which==0)
                {
                    //camera clicked
                    pickFromCamera();
                }
                else if(which==1)
                {
                    pickFromGallery();
                }
            }
        });
        //create/show dialog
        builder.create().show();
    }
    private void pickFromGallery() {
        //intent to pick image from gallary, the image will be rerurn in onActivityResult method
        Intent i=new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        activityResultLauncher.launch(i);
    }

    private void pickFromCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Image_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image_DESCRIPTION");
        //put image uri
        uriImage=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to open camera
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uriImage);
        cameraLauncher.launch(intent);
    }
}