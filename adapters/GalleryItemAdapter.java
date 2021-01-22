package com.example.walksapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.walksapp.R;
import com.example.walksapp.Section;

import java.io.File;
import java.util.ArrayList;

/**
 * Managers display of item in gallery
 */
public class GalleryItemAdapter extends PagerAdapter {

    private static final String IMAGE_PATH = "";

    private Context mContext;
    private ArrayList<Section> sectionList = new ArrayList<>();
    LayoutInflater inflater;

    public GalleryItemAdapter(Context mContext, ArrayList<Section> sectionList) {

        this.mContext = mContext;
        this.sectionList = sectionList;
    }

    @Override
    public int getCount() {
        return sectionList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.gallery_item, null);

        String currentImage = IMAGE_PATH + File.separator + sectionList.get(position).getFilename();

        ImageView imageView = v.findViewById(R.id.imageView);
        Glide.with(mContext).load(currentImage)
                .apply(new RequestOptions()
                        .centerInside())
                .into(imageView);

        imageView.setRotation(90);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager viewPager = (ViewPager) container;
        View v = (View) object;
        viewPager.removeView(v);
    }
}
