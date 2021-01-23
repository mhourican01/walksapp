package com.example.walksapp.adapters;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.walksapp.R;
import com.example.walksapp.Route;
import com.example.walksapp.Section;

import java.io.File;
import java.util.List;

/**
 * Manages display of section in list
 */
public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    //Constant variables
    private static final String IMAGE_PATH = "";

    // Instance variables
    private List<Section> sectionList;
    private Route route;
    private OnNoteListener mOnNoteListener;
    private double distance;


    public SectionAdapter(Route route, List<Section> sectionList, OnNoteListener onNoteListener) {
        this.route = route;
        this.sectionList = sectionList;
        this.mOnNoteListener = onNoteListener;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.section_recyclerview, viewGroup, false);
        return new SectionViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder sectionViewHolder, int i) {

        Section currentSection = sectionList.get(i);

        sectionViewHolder.sNameLayout.setText("Leg " + (i + 1) + ": " + currentSection.getName());
        sectionViewHolder.sDescLayout.setText(currentSection.getBlurb());

        Glide.with(sectionViewHolder.sImageLayout.getContext())
                .load(IMAGE_PATH + File.separator + currentSection.getFilename())
                .apply(RequestOptions.circleCropTransform())
                .into(sectionViewHolder.sImageLayout);

        sectionViewHolder.sImageLayout.setRotation(90);

        Location currentLatLng = new Location("");
        currentLatLng.setLatitude(Double.parseDouble(currentSection.getSectionLat()));
        currentLatLng.setLongitude(Double.parseDouble(currentSection.getSectionLng()));

        if (currentSection.equals(sectionList.get(0))) {

            Location start = new Location("");
            start.setLatitude(Double.parseDouble(route.getStartLat()));
            start.setLongitude(Double.parseDouble(route.getStartLng()));

            distance = start.distanceTo(currentLatLng);

        } else {
            if (sectionList.indexOf(currentSection) < (sectionList.size())) {

                Section previousSection = sectionList.get(i - 1);

                Location previousLatLng = new Location("");
                previousLatLng.setLatitude(Double.parseDouble(previousSection.getSectionLat()));
                previousLatLng.setLongitude(Double.parseDouble(previousSection.getSectionLng()));

                distance = currentLatLng.distanceTo(previousLatLng);

            }
        }

        if (distance < 1000) {
            sectionViewHolder.sDistLayout.setText(String.format("%.0f", distance) + "m");
        } else {
            double distanceInKm = convertToKm(distance);
            sectionViewHolder.sDistLayout.setText(String.format("%.2f", distanceInKm) + "km");
        }
    }

    private double convertToKm(double distance) {

        double distanceInKm = distance / 1000;
        return distanceInKm;
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView sNameLayout, sDescLayout, sDistLayout;
        ImageView sImageLayout;
        OnNoteListener onNoteListener;

        public SectionViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            sNameLayout = itemView.findViewById(R.id.sNameLayout);
            sDescLayout = itemView.findViewById(R.id.sDescLayout);
            sDistLayout = itemView.findViewById(R.id.sDistLayout);
            sImageLayout = itemView.findViewById(R.id.sImageView);

            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}