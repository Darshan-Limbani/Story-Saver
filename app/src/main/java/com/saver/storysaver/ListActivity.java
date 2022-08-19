package com.saver.storysaver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.saver.storysaver.instagram.IgLinkActivity;
import com.saver.storysaver.setting.SettingActivity;
import com.saver.storysaver.splash.ExploreActivity;
import com.saver.storysaver.utils.Util;
import com.saver.storysaver.whatsapp.WhatsAppStatusActivity;

import java.io.File;

public class ListActivity extends AppCompatActivity {

    public static Uri uri;
    public static File file;
    CardView btnInsta;
    CardView btnWhatsapp;
    CardView btnSettings;
    //    SharedPreferences preferences;
//    SharedPreferences.Editor edit;
//    String IS_PERMISSION = "isPermission";
    String startDir, secondDir, finalDirPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initView();

        btnInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.requestPermission(ListActivity.this)) {
                    Util.createAppFolder();

                    SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Util.IS_PERMISSION, true);
                    editor.apply();

                    if (!Util.IsLogin()) {
                        startActivity(new Intent(ListActivity.this, ExploreActivity.class));
                    } else {
                        startActivity(new Intent(ListActivity.this, IgLinkActivity.class));
                    }
                }
            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                showDialog();
                if (!Util.ISURIPermission()) {
                    getFilePath();
                } else if (Util.requestPermission(ListActivity.this)) {
                    startActivity(new Intent(ListActivity.this, WhatsAppStatusActivity.class).putExtra("type", "wa"));
                } else {
                    Util.requestPermission(ListActivity.this);
                }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListActivity.this, SettingActivity.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
//         data.get

            uri = data.getData();
            Log.d("TAG_URI", "--------- MY Final URI ------- :  " + uri);

            if (uri.toString().contains("Statuses")) {
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Util.setURIPermission(true);
//                        edit.putBoolean(IS_PERMISSION, true);
                Util.setUri(uri.toString());

          /*      edit.putBoolean(IS_PERMISSION, true);
                edit.putString("uri", uri.toString());
                edit.commit();*/
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getFolderPermission() {

        String path = Environment.getExternalStorageDirectory() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        File file = new File(path);
        if (file.exists()) {
            startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        }
        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();

        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString();

        Log.d("TAG_URI", "INITIAL_URI scheme: " + scheme);

        scheme = scheme.replace("/root/", "/document/");

        finalDirPath = scheme + "%3A" + startDir;

        uri = Uri.parse(finalDirPath);

        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        Log.d("TAG_URI", "uri: " + uri.toString());

        try {
            startActivityForResult(intent, 6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialog() {

        new AlertDialog.Builder(ListActivity.this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Allow Permission")
                .setMessage("Please allow access to the Whatsapp")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            getFolderPermission();
                        }
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }

    private void getFilePath() {
        if (!Util.ISURIPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showDialog();

            } else {
                String path = Environment.getExternalStorageDirectory() + "/WhatsApp/Media/.Statuses";
                file = new File(path);
                if (file.exists()) {
                    {
                        Log.d("TAG_URI", "------ File Path: " + file.toString());

                        Util.setURIPermission(true);
//                        edit.putBoolean(IS_PERMISSION, true);
                        Util.setUri(file.toString());
                    }
                }
            }
        }
    }

    private void initView() {
        btnInsta = findViewById(R.id.btnInsta);
        btnWhatsapp = findViewById(R.id.btnWhatsapp);
        btnSettings = findViewById(R.id.btnSettings);

    }
}