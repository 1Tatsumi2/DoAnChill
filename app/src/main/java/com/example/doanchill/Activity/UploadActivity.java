package com.example.doanchill.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanchill.MainActivity;
import com.example.doanchill.R;
import com.example.doanchill.ROOM.Music;
import com.example.doanchill.ROOM.MusicDatabase;
import com.example.doanchill.SignInActivity;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 99;

    EditText uploadName, uploadArtist, uploadAlbum, uploadSinger;
    TextView file;

    Button saveBtn,uploadFile;
    CircleImageView uploadImage;
    String imageUrl, audioUrl;
    Uri uriImage,uriAu;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    MusicDatabase musicDatabase;
    MediaPlayer mediaPlayer;
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

        RoomDatabase.Callback callback=new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }


            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        };
        musicDatabase = Room.databaseBuilder(getApplicationContext(),MusicDatabase.class,"MusicDB").addCallback(callback).build();

        //check permission
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES},REQUEST_PERMISSION);
            return;
        } else {

        }

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
                            file.setText("b");
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
                String name=uploadName.getText().toString();
                String artist=uploadArtist.getText().toString();
                String album=uploadArtist.getText().toString();
                String singer=uploadSinger.getText().toString();
                imageUrl=uriImage.toString();
                audioUrl=uriAu.toString();
                int duration=0;
                    mediaPlayer=MediaPlayer.create(UploadActivity.this,uriAu);
                    duration = mediaPlayer.getDuration();
                    Log.d("durationMuzik", String.valueOf(duration));
                Music music=new Music(name,artist,singer,duration,audioUrl,album,imageUrl);
                addMusic(music);
            }
        });
    }

    //add music
    public void addMusic(Music music)
    {
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Handler handler=new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                //bg task
                musicDatabase.getMusicDAO().addMusic(music);
                //on finishing task
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadActivity.this, "Added to database", Toast.LENGTH_SHORT).show();
                    }
                });
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