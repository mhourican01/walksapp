package com.example.walksapp.adapters;

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
import com.example.walksapp.DistanceCalculator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages display of route in list
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    // Constant variables
    private static final String IMAGE_PATH = "";

    // Instance variables
    private List<Route> routeList;
    private List<Section> sectionList;
    private OnNoteListener mOnNoteListener;
    private Route routeIndex;

    public RouteAdapter(List<Route> routeList, List<Section> sectionList, OnNoteListener onNoteListener) {
        this.routeList = routeList;
        this.sectionList = sectionList;
        this.mOnNoteListener = onNoteListener;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_recyclerview_detail, viewGroup, false);
        return new RouteViewHolder(view, mOnNoteListener);
    }

    private ArrayList<Section> getRouteSections() {

        ArrayList<Section> routeSections = new ArrayList<>();

        for (Section section : sectionList) {
            if (section.getRouteId().equals(routeIndex.getId())) {
                routeSections.add(section);
            }
        }

        return routeSections;
    }

    @Override
    public void onBindViewHolder(RouteViewHolder routeViewHolder, int i) {

        routeIndex = routeList.get(i);

        routeViewHolder.nameLayout.setText(routeIndex.getName());

        ArrayList<Section> routeSections = getRouteSections();

        routeViewHolder.descLayout.setText(routeSections.size() + " sections");

        double distanceAsDouble = Double.parseDouble(routeIndex.getDistance());

        if (distanceAsDouble >= 1000) {
            routeViewHolder.distLayout.setText(String.format("%.2fkm", (distanceAsDouble / 1000)));
        } else {

            routeViewHolder.distLayout.setText(String.format("%.2fm", distanceAsDouble));
        }

        if (!routeSections.isEmpty()) {
                Glide.with(routeViewHolder.imageLayout.getContext())
                        .load(IMAGE_PATH + File.separator + routeSections.get(0).getFilename())
                        .apply(RequestOptions.circleCropTransform())
                        .into(routeViewHolder.imageLayout);
        }

        routeViewHolder.imageLayout.setRotation(90);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameLayout, descLayout, distLayout;
        ImageView imageLayout;
        OnNoteListener onNoteListener;

        public RouteViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            nameLayout = itemView.findViewById(R.id.sNameLayout);
            descLayout = itemView.findViewById(R.id.sDescLayout);
            distLayout = itemView.findViewById(R.id.sDistLayout);
            imageLayout = itemView.findViewById(R.id.imageLayout);

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