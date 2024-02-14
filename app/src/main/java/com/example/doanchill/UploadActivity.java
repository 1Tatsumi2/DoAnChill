package com.example.doanchill;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanchill.Class.Song;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadActivity extends AppCompatActivity {

    EditText uploadName, uploadArtist, uploadAlbum, uploadSinger;
    TextView file;

    Button saveBtn,uploadFile;
    CircleImageView uploadImage;
    String imageUrl, audioUrl;
    Uri uriImage,uriAu;
    MediaPlayer mediaPlayer;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        uploadAlbum=findViewById(R.id.uploadAlbum);
        uploadArtist=findViewById(R.id.uploadArtist);
        uploadName=findViewById(R.id.uploadName);
        uploadSinger=findViewById(R.id.uploadSinger);
        file=findViewById(R.id.file);
        saveBtn=findViewById(R.id.saveButton);
        uploadFile=findViewById(R.id.btnUploadFile);
        uploadImage=findViewById(R.id.uploadImage);
        //image
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
                            Toast.makeText(UploadActivity.this,"No Image selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        //audio
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
                            Toast.makeText(UploadActivity.this,"No File selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        //camera
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

        //audio on click
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopicker=new Intent(Intent.ACTION_GET_CONTENT);
                photopicker.setType("audio/*");
                activityResultLauncherAu.launch(photopicker);
            }
        });
        //Click image view to show image pick dialog
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick dialog
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
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        android.app.AlertDialog dialog= builder.create();
        dialog.show();
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
                        UploadData();
                    }
                });
            }
        });
    }

    private void UploadData() {
        String name=uploadName.getText().toString();
        String artist=uploadArtist.getText().toString();
        String album=uploadAlbum.getText().toString();
        String singer=uploadSinger.getText().toString();
        int duration=0;
        mediaPlayer= MediaPlayer.create(UploadActivity.this,uriAu);
        duration = mediaPlayer.getDuration();
        Song song=new Song(name,artist,audioUrl,duration,imageUrl,album,singer);
        LocalDateTime now=LocalDateTime.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss");
        String dateTime=now.format(formatter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance("https://chill-8ac86-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("music");
        databaseReference.child(dateTime).setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UploadActivity.this,"Saved",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
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


}