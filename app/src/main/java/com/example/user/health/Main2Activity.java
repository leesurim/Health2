package com.example.user.health;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class Main2Activity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TabHost tabHost = getTabHost();
        Intent intent;

        intent = new Intent().setClass(this, FirstTab.class);
        TabHost.TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("HOME",getResources().getDrawable(R.drawable.home));
        tabSpecTab1.setContent(intent);
        Intent intent2 = getIntent();

        String name;
        name = intent2.getExtras().getString("name");

        tabHost.addTab(tabSpecTab1);

        intent = new Intent().setClass(this, Main3Activity.class);
        Log.d("Main","21132");
        TabHost.TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("START",getResources().getDrawable(R.drawable.chart));
        tabSpecTab2.setContent(intent);
        Log.d("Main","21132333");
        tabHost.addTab(tabSpecTab2);

        intent = new Intent().setClass(this, MapsActivity.class);
        TabHost.TabSpec tabSpecTab3 = tabHost.newTabSpec("TAB3").setIndicator("MAPS",getResources().getDrawable(R.drawable.map2));
        tabSpecTab3.setContent(intent);
        tabHost.addTab(tabSpecTab3);

        tabHost.setCurrentTab(0);
    }
}