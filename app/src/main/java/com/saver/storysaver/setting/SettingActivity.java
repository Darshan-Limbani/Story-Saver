package com.saver.storysaver.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

public class SettingActivity extends AppCompatActivity {


    ConstraintLayout btnShare;
    ConstraintLayout btnRate;
    ConstraintLayout btnContact;
    ConstraintLayout btnPrivacy;
    ConstraintLayout btnAbout;
    ImageView btnBack;

    String PRIVACY_URL = "https://sites.google.com/view/crazyappsdev";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        initView();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendEmail("crazyappsolutions@gmail.com");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openCustomTab(SettingActivity.this, PRIVACY_URL);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
            }
        });


    }

    private void rate() {

        String str = "market://details?id=" + getPackageName();
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }

    private void share() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);

        String share = ("https://app.mi.com/details?id=" + getPackageName());
//
//        String share = ("market://details??id=" + getPackageName());
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, Here I am using the best Social Media Downloading Application." +
                "\n\n--------------------------------------\n\n " + share);
        startActivity(Intent.createChooser(intent, "Share via : "));
    }

    public void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

        startActivity(emailIntent);

//        startActivity(Intent.createChooser(emailIntent, "Chooser Title"));

    }

    private void initView() {

        btnShare = findViewById(R.id.btnShare);
        btnRate = findViewById(R.id.btnRate);
        btnContact = findViewById(R.id.btnContact);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnAbout = findViewById(R.id.btnAbout);
        btnBack = findViewById(R.id.btnBack);

    }
}