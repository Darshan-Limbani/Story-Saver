package com.saver.storysaver.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.saver.storysaver.ListActivity;
import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

public class GettingStartedActivity extends AppCompatActivity {


    ConstraintLayout btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);

        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.requestPermission(GettingStartedActivity.this)) {
                    Util.createAppFolder();

                    startActivity(new Intent(GettingStartedActivity.this, ListActivity.class));

                    /*SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
                    editor.putBoolean("IS_FILE_PERMISSION", true);
                    editor.apply();
                    startActivity(new Intent(GettingStartedActivity.this, ExploreActivity.class));
*/                }
            }
        });
    }
}