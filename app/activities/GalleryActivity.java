package com.example.walksapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.walksapp.R;
import com.example.walksapp.Section;
import com.example.walksapp.adapters.GalleryImageAdapter;
import com.example.walksapp.interfaces.IRecyclerViewClickListener;

import java.util.ArrayList;

/**
 * Manages gallery view of images from route
 */
public class GalleryActivity extends AppCompatActivity {

    // Instance variables
    private RecyclerView galleryRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Section> sectionList;
    private Toolbar mToolbar;
    private String routeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Intent intent = getIntent();
        routeName = intent.getStringExtra("route_name");

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Images from " + routeName);

        galleryRecyclerView = findViewById(R.id.galleryRecyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        galleryRecyclerView.setHasFixedSize(true);
        galleryRecyclerView.setLayoutManager(layoutManager);

        sectionList = new ArrayList<>();
        sectionList = intent.getParcelableArrayListExtra("section_list");

        final IRecyclerViewClickListener listener = new IRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                // Opens image
                Intent intent = new Intent(getApplicationContext(), GalleryItemActivity.class);
                intent.putExtra("section_list", sectionList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        };

        GalleryImageAdapter adapter = new GalleryImageAdapter(this, sectionList, listener);
        galleryRecyclerView.setAdapter(adapter);
    }
}
