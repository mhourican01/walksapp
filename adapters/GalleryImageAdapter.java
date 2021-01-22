package com.example.walksapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.walksapp.R;
import com.example.walksapp.Section;
import com.example.walksapp.interfaces.IRecyclerViewClickListener;

import java.io.File;
import java.util.ArrayList;


/**
 * Manages display of gallery of images from route
 */
public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ImageViewHolder> {

    private static final String IMAGE_PATH = "";

    private Context mContext;
    private ArrayList<Section> sectionList = new ArrayList<>();
    private IRecyclerViewClickListener listener;

    public GalleryImageAdapter(Context mContext, ArrayList<Section> sectionList, IRecyclerViewClickListener listener) {

        this.mContext = mContext;
        this.sectionList = sectionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_recyclerview, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {

        String currentImage = IMAGE_PATH + File.separator + sectionList.get(i).getFilename();
        ImageView imageView = imageViewHolder.imageView;
        final ProgressBar progressBar = imageViewHolder.progressBar;

        Glide.with(mContext).load(currentImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

        imageViewHolder.imageView.setRotation(90);
    }

    @Override
    public int getItemCount() {

        return sectionList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        ProgressBar progressBar;

        public ImageViewHolder(@NonNull View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            progressBar = itemView.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            listener.onClick(v, getAdapterPosition());
        }
    }
}
