package com.saver.storysaver.splash;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.saver.storysaver.instagram.IgLink;
import com.saver.storysaver.instagram.IgLoginActivity;
import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

public class ExploreActivity extends AppCompatActivity {


    ConstraintLayout btnExplore;
    LinearLayoutCompat btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        initView();

        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ExploreActivity.this, IgLink.class));

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isNetworkAvailable(ExploreActivity.this)) {
                    goToActivity();
                } else {
                    showInternetDialog();
                }

            }
        });


    }

    private void goToActivity() {
        startActivity(new Intent(ExploreActivity.this, IgLoginActivity.class));
    }

    private void showInternetDialog() {

        Dialog dialog = new Dialog(ExploreActivity.this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        Button btnretry = dialog.findViewById(R.id.btnretry);

        btnretry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isNetworkAvailable(ExploreActivity.this)) {
                    dialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    }, 300);
                } else {
                    dialog.dismiss();

                    goToActivity();
//                    loadJsonData();
                }
            }
        });
        dialog.show();
    }


    private void initView() {

        btnExplore = findViewById(R.id.btnExplore);
        btnLogin = findViewById(R.id.btnLogin);


    }
}