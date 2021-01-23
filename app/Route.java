package com.example.walksapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class constructs Route objects
 */
public class Route implements Parcelable {

    // Instance variables
    private String id;
    private String name;
    private String startLat;
    private String startLng;
    private String endLat;
    private String endLng;
    private String distance;
    private String userId;

    /**
     * Default constructor
     */
    public Route() {

    }

    public Route(String id, String name, String startLat, String startLng, String endLat, String endLng,
                 String distance,
                 String userId) {
        this.id = id;
        this.name = name;
        this.startLat = startLat;
        this.startLng = startLng;
        this.endLat = endLat;
        this.endLng = endLng;
        this.distance = distance;
        this.userId = userId;
    }


    protected Route(Parcel in) {
        id = in.readString();
        name = in.readString();
        startLat = in.readString();
        startLng = in.readString();
        endLat = in.readString();
        endLng = in.readString();
        distance = in.readString();
        userId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startLat);
        dest.writeString(startLng);
        dest.writeString(endLat);
        dest.writeString(endLng);
        dest.writeString(distance);
        dest.writeString(userId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    /**
     * Gets id of Route object
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id of Route object
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets name of Route object
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of Route object
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets latitude at start of Route
     * @return
     */
    public String getStartLat() {
        return startLat;
    }

    /**
     * Sets latitude at start of Route
     * @param startLat
     */
    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    /**
     * Gets longitude at start of Route
     * @return
     */
    public String getStartLng() {
        return startLng;
    }

    /**
     * Sets longitude at start of Route
     * @param startLng
     */
    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    /**
     * Gets latitude at end of Route
     * @return
     */
    public String getEndLat() {
        return endLat;
    }

    /**
     * Sets latitude at end of Route
     * @param endLat
     */
    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    /**
     * Gets longitude at end of Route
     * @return
     */
    public String getEndLng() {
        return endLng;
    }

    /**
     * Sets longitude at end of Route
     * @param endLng
     */
    public void setEndLng(String endLng) {
        this.endLng = endLng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}