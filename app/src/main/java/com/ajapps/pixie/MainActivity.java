package com.ajapps.pixie;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import com.ajapps.pixie.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1024;
    private Uri imageUri;
    private static final int REQUEST_PICK_IMAGE = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        if(!MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            findViewById(R.id.cameraButton).setVisibility(View.GONE);

        final ImageButton getImage = findViewById(R.id.editButton);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                final Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
                startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
            }
        });

        final ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        imageUri = FileProvider.getUriForFile(MainActivity.this, "com.ajapps.pixie.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == REQUEST_PICK_IMAGE && data != null) {
            System.out.println(data.getData());
            Intent pixie = new Intent(MainActivity.this, EditImageActivity.class);
            pixie.setData(data.getData());
            startActivity(pixie);
        }
        if(requestCode == REQUEST_IMAGE_CAPTURE && imageUri != null){
            Intent pixie = new Intent(MainActivity.this, EditImageActivity.class);
            pixie.setData(imageUri);
            startActivity(pixie);
        }
    }
}