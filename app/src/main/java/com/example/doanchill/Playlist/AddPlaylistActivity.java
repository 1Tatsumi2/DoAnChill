package com.example.doanchill.Playlist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.MusicManager.PlaylistDetailActivity;
import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Playlist;
import com.example.doanchill.Class.Song;
import com.example.doanchill.R;
import com.example.doanchill.UploadActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPlaylistActivity extends AppCompatActivity {

    EditText name, desc;
    Switch publicSwitch;
    CircleImageView uploadImage;
    String imageUrl;
    Uri uriImage;
    Button addPlaylist;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    CollectionReference ref=db.collection("Playlist");
    String UserID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);
        name=findViewById(R.id.CreatePlaylistName);
        desc=findViewById(R.id.CreatePlaylistDesc);
        publicSwitch=findViewById(R.id.isPublic);
        addPlaylist=findViewById(R.id.createPlaylist);
        uploadImage=findViewById(R.id.uploadImagePlaylist);
        fAuth=FirebaseAuth.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        fStore=FirebaseFirestore.getInstance();
        activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK)
                        {
                            Intent data=o.getData();
                            uriImage=data.getData();
                            uploadImage.setImageURI(uriImage);
                        }
                        else {
                            Toast.makeText(AddPlaylistActivity.this,"No Image selected",Toast.LENGTH_SHORT).show();
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
                        uploadImage.setImageURI(uriImage);
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

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
                imagePickDialog();
            }
        });
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveData();
            }
        });
    }
    private void imagePickDialog() {
        //option to display in dialog
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
    private void saveData() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(AddPlaylistActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        android.app.AlertDialog dialog= builder.create();
        dialog.show();
        StorageReference storageReferenceImg = FirebaseStorage.getInstance().getReference().child("Playlist Images")
                .child(uriImage.getLastPathSegment());
                storageReferenceImg.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uria) {
                        imageUrl = uria.toString();
                        dialog.dismiss();
                        UploadData();
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

    private void UploadData() {
        String names=name.getText().toString();
        String descs=desc.getText().toString();
        DocumentReference documentReference=fStore.collection("users").document(UserID);
        Playlist playlist=new Playlist(names,descs,publicSwitch.isChecked(),documentReference,imageUrl);
        ref.add(playlist).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddPlaylistActivity.this,"Playlist Created",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(AddPlaylistActivity.this, PlaylistDetailActivity.class);
                playlist.setKey(documentReference.getId());
                i.putExtra("key",playlist.getKey());
                startActivity(i);
                finish();
            }
        });
    }
}