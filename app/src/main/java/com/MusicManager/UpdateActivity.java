package com.MusicManager;

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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doanchill.Class.Song;
import com.example.doanchill.Fragments.SettingsFragment;
import com.example.doanchill.R;
import com.example.doanchill.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateActivity extends AppCompatActivity {
    CircleImageView updateImage;
    TextView file;
    Button saveButton, audioSave;
    EditText name,artist,singer,album;
    String key,oldImageUrl,oldAudioUrl,imageUrl,audioUrl;
    Uri uriImage,uriAu;
    MediaPlayer mediaPlayer;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    boolean isImageUpdated = false;
    boolean isAudioUpdated = false;
    int duration=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        updateImage=findViewById(R.id.updateImage);
        file=findViewById(R.id.fileUpdate);
        saveButton=findViewById(R.id.saveUpdateButton);
        audioSave=findViewById(R.id.btnUpdateFile);
        name=findViewById(R.id.updateName);
        artist=findViewById(R.id.updateArtist);
        singer=findViewById(R.id.updateSinger);
        album=findViewById(R.id.updateAlbum);
        activityResultLauncher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK)
                        {
                            Intent data=o.getData();
                            uriImage=data.getData();
                            updateImage.setImageURI(uriImage);
                        }
                        else {
                            Toast.makeText(UpdateActivity.this,"No Image selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
//
//        //audio
        ActivityResultLauncher<Intent> activityResultLauncherAu=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode()== Activity.RESULT_OK){
                            Intent data=o.getData();
                            uriAu=data.getData();
                            file.setText("File selected");
                        }
                        else {
                            Toast.makeText(UpdateActivity.this,"No File selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
//
//        //camera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // The image capture intent was successful, start the cropping activity
                        UCrop.of(uriImage, uriImage)
                                .withAspectRatio(1, 1)
                                .start(this);
                        updateImage.setImageURI(uriImage);
                    }
                }
        );
//
//
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
//
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            name.setText(bundle.getString("Title"));
            artist.setText(bundle.getString("Artist"));
            singer.setText(bundle.getString("Singer"));
            album.setText(bundle.getString("Album"));
            key=bundle.getString("Key");
            oldImageUrl=bundle.getString("Image");
            oldAudioUrl=bundle.getString("Audio");
            duration=bundle.getInt("Duration");
        }
        imageUrl=oldImageUrl;
        audioUrl=oldAudioUrl;
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImageUpdated=true;
                imagePickDialog();
            }
        });
        audioSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudioUpdated=true;
                Intent photopicker=new Intent(Intent.ACTION_GET_CONTENT);
                photopicker.setType("audio/*");
                activityResultLauncherAu.launch(photopicker);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void saveData() {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        android.app.AlertDialog dialog= builder.create();
        dialog.show();
        if(isAudioUpdated && isImageUpdated)
        {
            StorageReference storageReferenceImg = FirebaseStorage.getInstance().getReference().child("Android Images")
                    .child(uriImage.getLastPathSegment());
            StorageReference storageReferenceAu = FirebaseStorage.getInstance().getReference().child("Audio")
                    .child(uriAu.getLastPathSegment());

            UploadTask uploadTaskImg = storageReferenceImg.putFile(uriImage);
            UploadTask uploadTaskAu = storageReferenceAu.putFile(uriAu);

            uploadTaskImg.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReferenceImg.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageUrl = task.getResult().toString();

                    uploadTaskAu.continueWithTask(taskAu -> {
                        if (!taskAu.isSuccessful()) {
                            throw taskAu.getException();
                        }
                        return storageReferenceAu.getDownloadUrl();
                    }).addOnCompleteListener(taskAu -> {
                        if (taskAu.isSuccessful()) {
                            audioUrl = taskAu.getResult().toString();

                            // Call UploadData() only when both download URLs have been retrieved
                            dialog.dismiss();
                            UpdateData();
                        }
                    });
                }
            });
        }
        else if (isAudioUpdated) {
            StorageReference storageReferenceAu = FirebaseStorage.getInstance().getReference().child("Audio")
                    .child(uriAu.getLastPathSegment());
            storageReferenceAu.putFile(uriAu).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            audioUrl = uri.toString();
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
        else if (isImageUpdated)
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

    private void UpdateData() {
        String nameUpdate=name.getText().toString();
        String artistUpdate=artist.getText().toString();
        String albumUpdate=album.getText().toString();
        String singerUpdate=singer.getText().toString();
        if(isAudioUpdated)
        {
            mediaPlayer= MediaPlayer.create(UpdateActivity.this,uriAu);
            duration = mediaPlayer.getDuration();
        }
        Song song=new Song(nameUpdate,artistUpdate,audioUrl,duration,imageUrl,albumUpdate,singerUpdate);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance("https://chill-8ac86-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("music").child(key);
        databaseReference.setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UpdateActivity.this,"Update Success",Toast.LENGTH_SHORT).show();
                    finish();
                    Intent i=new Intent(UpdateActivity.this, MusicManagerActivity.class);
                    startActivity(i);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
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