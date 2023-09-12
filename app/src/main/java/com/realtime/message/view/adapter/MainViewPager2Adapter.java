package com.realtime.message.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.realtime.message.view.fragment.main.DiaryFragment;
import com.realtime.message.view.fragment.main.DirectoryFragment;
import com.realtime.message.view.fragment.main.IndividualFragment;
import com.realtime.message.view.fragment.main.MsgFragment;

public class MainViewPager2Adapter extends FragmentStateAdapter {
    public MainViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MsgFragment();
            case 1:
                return new DirectoryFragment();
            case 2:
                return new DiaryFragment();
            case 3:
                return new IndividualFragment();
            default:
                return new MsgFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}


