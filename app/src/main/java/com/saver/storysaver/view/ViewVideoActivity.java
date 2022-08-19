package com.saver.storysaver.view;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class ViewVideoActivity extends AppCompatActivity {

    VideoView vdView;
    ImageView btnDownload;

    Boolean isFile = false;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        initView();

        MediaController controller = new MediaController(ViewVideoActivity.this);
        controller.setAnchorView(vdView);
        vdView.setMediaController(controller);


        isFile = getIntent().getBooleanExtra("isFile", false);

        if (isFile) {
            uri = getIntent().getStringExtra("URI");
            if (Util.file != null) {
                vdView.setVideoURI(Uri.parse(Util.file.getUri().toString()));
//            vdView.setVideoURI(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"));
            } else {
                Toast.makeText(this, "Video can not be Played!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            uri = getIntent().getStringExtra(Util.URL);
            vdView.setVideoURI(Uri.parse(uri));
        }


        vdView.requestFocus();
        vdView.start();


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isFile) {
                    downloadWhatsappVideo(Util.file);
                } else {
                    downloadInstaVideo(uri);
                }


            }
        });

    }

    private void downloadWhatsappVideo(DocumentFile srcFile) {


        ContentResolver resolver = getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, srcFile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, srcFile.getType());

//        if (Build.VERSION.SDK_INT >=)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Util.IMG_FOLDER);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS
                    + File.separator + getString(R.string.app_name)
                    + File.separator + "WhatsApp"
                    + File.separator + "Videos");
        }
        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

        try {
            InputStream in = resolver.openInputStream(srcFile.getUri());

            OutputStream out = resolver.openOutputStream(uri);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);
            }
            in.close();
            out.close();


            Toast.makeText(this, "Video Saved Successfully to Downloads/" + getString(R.string.app_name) + "/WhatsApp/Videos", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void downloadInstaVideo(String uriString) {
        try {


            String fName = "VID_" + System.currentTimeMillis() + ".mp4";
            File file = new File(Util.IG_VID, fName);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uriString));
//        request.setDescription("Some descrition");
            request.setTitle(fName);
// in order for this if to run, you must use the android 3.2 to compile your app
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

//        if (isInsta)
//            request.setDestinationInExternalPublicDir(Util.IG_POST, "IMG_" + System.currentTimeMillis() + ".jpg");
            request.setDestinationUri(Uri.fromFile(file));
//        else
            request.allowScanningByMediaScanner();
//            request.setDestinationInExternalFilesDir(getContext(), Util.IMG_FOLDER, "IMG_" + System.currentTimeMillis() + ".jpg");
// get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);

            Toast.makeText(ViewVideoActivity.this, "Download Started to to Downloads/" + getString(R.string.app_name) + "/Instagram/Videos", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {

        vdView = findViewById(R.id.vdView);
        btnDownload = findViewById(R.id.btnDownload);

    }
}