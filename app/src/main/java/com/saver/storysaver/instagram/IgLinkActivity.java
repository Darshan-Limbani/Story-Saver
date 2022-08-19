package com.saver.storysaver.instagram;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.saver.storysaver.R;
import com.saver.storysaver.setting.SettingActivity;
import com.saver.storysaver.utils.IGUtils;
import com.saver.storysaver.utils.Util;

import java.io.File;

public class IgLinkActivity extends AppCompatActivity {

    public static Uri uri;
    public static File file;
    public static Boolean isDp;
    public static IgLinkActivity instance;
    //    ImageView btnWhatsapp;
    String startDir, secondDir, finalDirPath;
    SharedPreferences preferences;
    SharedPreferences.Editor edit;
    ConstraintLayout btnDpDownload;
    ConstraintLayout btnMedia;
    //    ImageButton btnSave;
    EditText etLink;
    EditText etProfile;

    ImageView btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ig_link);

        initView();

        instance = IgLinkActivity.this;

        Util.requestPermission(IgLinkActivity.this);

/*        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                showDialog();
                if (!preferences.getBoolean(IS_PERMISSION, false)) {
                    getFilePath();
                } else if (Util.requestPermission(MainActivity.this)) {
                    startActivity(new Intent(MainActivity.this, WhatsAppStatusActivity.class).putExtra("type", "wa"));
                } else {
                    Util.requestPermission(MainActivity.this);
                }
            }
        });*/
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IgLinkActivity.this, SettingActivity.class));
            }
        });
        btnDpDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Util.isNetworkAvailable(IgLinkActivity.this)) {
                    showInternetDialog();
                    return;
                }

                if (etProfile.getText().toString().trim().isEmpty()) {

                    etProfile.setError("Username Required!");
                    Toast.makeText(IgLinkActivity.this, "Please Enter Username!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
//                new IGUtils(MainActivity.this,etLink.getText().toString().trim());

                isDp = true;
                new MyTask().execute();

            }
        });

        btnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Util.isNetworkAvailable(IgLinkActivity.this)) {
                    showInternetDialog();
                    return;
                }

                if (etLink.getText().toString().trim().isEmpty()) {
                    etLink.setError("Link Required!");
                    Toast.makeText(IgLinkActivity.this, "Please paste a Link First!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
//                new IGUtils(MainActivity.this,etLink.getText().toString().trim());
                isDp = false;
                new MyTask().execute();
            }
        });

    }

    private void showInternetDialog() {

        Dialog dialog = new Dialog(IgLinkActivity.this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.no_internet_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        Button btnretry = dialog.findViewById(R.id.btnretry);

        btnretry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isNetworkAvailable(IgLinkActivity.this)) {
                    dialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    }, 300);
                } else {
                    dialog.dismiss();
//                    loadJsonData();
                }
            }
        });
        dialog.show();
    }


    private void initView() {

//        btnWhatsapp = findViewById(R.id.btnWhatsapp);
//      btnSave = findViewById(R.id.btnSearch);
        etLink = findViewById(R.id.etLink);
        etProfile = findViewById(R.id.etProfile);
        btnDpDownload = findViewById(R.id.btnDpDownload);
        btnMedia = findViewById(R.id.btnMedia);
//      etUname = findViewById(R.id.etUname);
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        edit = preferences.edit();
        btnSettings = findViewById(R.id.btnSettings);
    }


    class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

//            Log.d("INSTA_LOG", "------ FINAL POST URL ------ doInBackGround()  called" + etUname.getText().toString().trim());

            if (isDp) {
                Log.d("INSTA_LOG", "------ FINAL POST URL ------ doInBackGround()  called" + etProfile.getText().toString().trim());
                new IGUtils(IgLinkActivity.this, etProfile.getText().toString().trim());

            } else {
                new IGUtils(IgLinkActivity.this, etLink.getText().toString().trim());
            }

            return "";

            //etUname.getText().toString().trim();
        }
//        MyTask()
//        {
//            Log.d("INSTA_LOG", "------ FINAL POST URL ------ onPreExecute()  called");
//        }

    }
}
