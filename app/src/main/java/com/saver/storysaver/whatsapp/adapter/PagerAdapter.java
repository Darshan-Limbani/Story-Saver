package com.saver.storysaver.whatsapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStateAdapter {


    ArrayList<Fragment> list;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragmentList) {
        super(fragmentActivity);

        this.list = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
//        if (position == 1) {
//            return new VideoFragment();
//        }
        return list.get(position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
