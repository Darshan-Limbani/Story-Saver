package com.saver.storysaver.whatsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.saver.storysaver.R;
import com.saver.storysaver.view.ImageFragment;
import com.saver.storysaver.view.VideoFragment;
import com.saver.storysaver.whatsapp.adapter.PagerAdapter;

import java.util.ArrayList;

public class WhatsAppStatusActivity extends AppCompatActivity {


    TabLayout tab;
    ViewPager2 pager;

    ArrayList<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_status);

        initView();

//        tab.addTab(tab.newTab().setText("Images"));
//        tab.addTab(tab.newTab().setTag("Videos"));

        if (getIntent().getStringExtra("type").equals("ig")) {
            if (getIntent().getBooleanExtra("isImage", true)) {
                fragmentList.add(new ImageFragment());
            }
            if (getIntent().getBooleanExtra("isVideo", true)) {
                fragmentList.add(new VideoFragment());
            }
        } else {
            fragmentList.add(new ImageFragment());
            fragmentList.add(new VideoFragment());

        }


        pager.setAdapter(new PagerAdapter(this, fragmentList));

        pager.setPageTransformer(new MarginPageTransformer(5));

//        pager.

//        tab.setupWithViewPager(pager);
        new TabLayoutMediator(tab, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

//                if (position == 0) {
//                    tab.setText("Images");
//                }

                for (int i = 0; i < fragmentList.size(); i++) {
                    if (fragmentList.get(position) instanceof ImageFragment)
                        tab.setText("Images");
                    if (fragmentList.get(position) instanceof VideoFragment) {
                        tab.setText("Videos");
                    }
                }

                /*else if (position == 1) {
                    tab.setText("Videos");
                }*/

            }
        }).attach();

//        pager.setPageTransformer(new MarginPageTransformer(50));

    }

    private void initView() {

        tab = findViewById(R.id.tab);
        pager = findViewById(R.id.pager);

    }
}