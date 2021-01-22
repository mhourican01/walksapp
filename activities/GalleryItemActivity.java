package com.example.walksapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.walksapp.R;
import com.example.walksapp.Section;
import com.example.walksapp.adapters.GalleryItemAdapter;

import java.util.ArrayList;

/**
 * Manages display of item in gallery
 */
public class GalleryItemActivity extends Activity {

    private ViewPager viewPager;
    private ArrayList<Section> sectionList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item);

        sectionList = new ArrayList<>();

        Intent intent = getIntent();
        sectionList = intent.getParcelableArrayListExtra("section_list");
        position = intent.getIntExtra("position", 0);

        viewPager = findViewById(R.id.viewPager);

        GalleryItemAdapter adapter = new GalleryItemAdapter(this, sectionList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, true);
    }
}
