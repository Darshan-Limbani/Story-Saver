package com.saver.storysaver.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

import java.io.File;
import java.io.FileOutputStream;

public class ViewImageActivity extends AppCompatActivity {


    ImageView imgDisplay;
    ImageView btnShare, btnDownload, btnBack;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        initView();
        Log.d("URI_LOG_ADAPTER", "---------- Uri in ImageActivity ----------" + getIntent().getStringExtra("URI"));


        uri = Uri.parse(getIntent().getStringExtra("URI"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (uri != null) {
            Glide.with(ViewImageActivity.this).load(uri).into(imgDisplay);
//            imgDisplay.setImageURI(uri);
        }


        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareImage();

            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadImage();

            }
        });


    }


        private void downloadImage () {

            if (Util.createAppFolder()) {

                Bitmap bitmap = ((BitmapDrawable) imgDisplay.getDrawable()).getBitmap();
                try {

                    File img = new File(Util.WA_IMG, "IMG_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream out = new FileOutputStream(img);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    Toast.makeText(this, "Image Saved to : Downloads/" + getString(R.string.app_name) + "/Images", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        private void shareImage () {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Share Image Via :"));

        }

        private void initView () {

            imgDisplay = findViewById(R.id.imgDisplay);
            btnDownload = findViewById(R.id.imgDownload);
            btnShare = findViewById(R.id.imgShare);
            btnBack = findViewById(R.id.imgBack);

        }
    }