package com.example.photoeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.photoeditor.databinding.ActivityHomeBinding;

import java.io.ByteArrayOutputStream;

public class Home extends AppCompatActivity
{
    ActivityHomeBinding binding;
    int IMAGE_REQUEST_CODE =45;
    int CAMERA_REQUEST_CODE =14;
    int INTENT_CODE =200;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        binding.gallerybutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        binding.camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Home.this,
                        Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.CAMERA},32);
                }else
                    {
                        Intent cameraIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE)
        {
            if (data.getData() != null)
            {
                Uri filepath = data.getData();
                Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                dsPhotoEditorIntent.setData(filepath);
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Photo Editor");
                startActivityForResult(dsPhotoEditorIntent, INTENT_CODE);
            }
        }
        if (requestCode == INTENT_CODE)
        {
            Intent intent = new Intent(Home.this, ResultActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }
        if (requestCode== CAMERA_REQUEST_CODE)
        {
            Bitmap photo= (Bitmap) data.getExtras().get("data");
            Uri uri=getImageUri(photo);
            Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);

            dsPhotoEditorIntent.setData(uri);
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Photo Editor");

            startActivityForResult(dsPhotoEditorIntent, INTENT_CODE);
        }
    }
    public  Uri getImageUri(Bitmap bitmap)
    {
        ByteArrayOutputStream arrayOutputStream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,arrayOutputStream);
        String path =MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"Title",null);
        return Uri.parse(path);
    }


}
