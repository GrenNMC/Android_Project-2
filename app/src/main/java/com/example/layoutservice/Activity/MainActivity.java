package com.example.layoutservice.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.layoutservice.Fragment.Fragment_Tim_Kiem;
import com.example.layoutservice.Fragment.Fragment_Trang_Chu;
import com.example.layoutservice.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        bindingView();
//        init();
//    }

//    private void init() {
//        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), 1);
//        mainViewPagerAdapter.addFragment(new Fragment_Trang_Chu(), "Trang Chu");
//        mainViewPagerAdapter.addFragment(new Fragment_Tim_Kiem(), "Tim Kiem");
//        viewPager.setAdapter(mainViewPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
//    }
//
//    private void bindingView() {
//        tabLayout = findViewById(R.id.myTabLayout);
//        viewPager = findViewById(R.id.myViewPager);
//    }

}

