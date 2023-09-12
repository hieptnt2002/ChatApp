package com.realtime.message;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.realtime.message.databinding.ActivityMainBinding;
import com.realtime.message.view.adapter.MainViewPager2Adapter;
import com.realtime.message.viewmodel.MainViewModel;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MainViewModel viewModel = new MainViewModel(this);
        mainBinding.setMainViewModel(viewModel);

        mToolbar = mainBinding.toolbar;
        mainBinding.viewPager2.setAdapter(new MainViewPager2Adapter(this));
//        viewPager2.setUserInputEnabled(false);
        mainBinding.viewPager2.setUserInputEnabled(false);
        mainBinding.viewPager2.setCurrentItem(0, false);
        mainBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_msg)
                    mainBinding.viewPager2.setCurrentItem(0);
                else if (item.getItemId() == R.id.menu_gr)
                    mainBinding.viewPager2.setCurrentItem(1);
                else if (item.getItemId() == R.id.menu_dir)
                    mainBinding.viewPager2.setCurrentItem(2);
                else if (item.getItemId() == R.id.menu_usr)
                    mainBinding.viewPager2.setCurrentItem(3);

                return false;
            }
        });

        mainBinding.getMainViewModel().getUnreadLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
              if(mainBinding.viewPager2.getCurrentItem() == 0){
                  mToolbar.setTitle("Đoạn chat" + "(" + integer + ")");
              }
                mainBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        switch (position) {
                            case 0:
                                mainBinding.bottomNavigationView.getMenu().findItem(R.id.menu_msg).setChecked(true);
                                mToolbar.setTitle("Đoạn chat" + "(" + integer + ")");
                                mToolbar.setNavigationIcon(R.drawable.option);
                                mainBinding.buttonImgGroup.setVisibility(View.VISIBLE);
                                break;

                            case 1:
                                mainBinding.bottomNavigationView.getMenu().findItem(R.id.menu_gr).setChecked(true);
                                mToolbar.setTitle("Danh bạ");
                                mToolbar.setNavigationIcon(R.drawable.option);
                                mainBinding.buttonImgGroup.setVisibility(View.GONE);
                                break;
                            case 2:
                                mainBinding.bottomNavigationView.getMenu().findItem(R.id.menu_dir).setChecked(true);
                                mToolbar.setTitle("Nhật ký");
                                mToolbar.setNavigationIcon(null);
                                mainBinding.buttonImgGroup.setVisibility(View.GONE);
                                break;
                            case 3:
                                mainBinding.bottomNavigationView.getMenu().findItem(R.id.menu_usr).setChecked(true);
                                mToolbar.setTitle("Cá nhân");
                                mToolbar.setNavigationIcon(null);
                                mainBinding.buttonImgGroup.setVisibility(View.GONE);
                                break;

                        }
                    }
                });

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBinding.getMainViewModel().status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        mainBinding.getMainViewModel().status("offline");
    }
}