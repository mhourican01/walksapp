package com.example.walksapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class constructs Section objects
 */
public class Section implements Parcelable {

    // Instance variables
    private String id;
    private String name;
    private String blurb;
    private String filename;
    private String sectionLat;
    private String sectionLng;
    private String routeId;

    /**
     * Default constructor
     */
    public Section() {

    }

    /**
     * Constructor with arguments
     * @param id
     * @param name
     * @param blurb
     * @param filename
     * @param sectionLat
     * @param sectionLng
     * @param routeId
     */
    public Section(String id, String name, String blurb, String filename, String sectionLat, String sectionLng, String routeId) {
        this.id = id;
        this.name = name;
        this.blurb = blurb;
        this.filename = filename;
        this.sectionLat = sectionLat;
        this.sectionLng = sectionLng;
        this.routeId = routeId;
    }

    /**
     * Converts Section object to Parcelable
     * @param in
     */
    protected Section(Parcel in) {
        id = in.readString();
        name = in.readString();
        blurb = in.readString();
        filename = in.readString();
        sectionLat = in.readString();
        sectionLng = in.readString();
        routeId = in.readString();
    }

    /**
     * Converts Section object to Parcelable
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(blurb);
        dest.writeString(filename);
        dest.writeString(sectionLat);
        dest.writeString(sectionLng);
        dest.writeString(routeId);
    }

    /**
     * Converts Section object to Parcelable
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Converts Section object to Parcelable
     */
    public static final Creator<Section> CREATOR = new Creator<Section>() {
        @Override
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        @Override
        public Section[] newArray(int size) {
            return new Section[size];
        }
    };

    /**
     * Gets id of Section object
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id of Section object
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets name of Section object
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of Section object
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets description of Section object
     * @return
     */
    public String getBlurb() {
        return blurb;
    }

    /**
     * Sets description of Section object
     * @param blurb
     */
    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    /**
     * Gets filename of image of Section
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets filename of image of Section
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets latitude of Section object
     * @return
     */
    public String getSectionLat() {
        return sectionLat;
    }

    /**
     * Sets latitude of Section object
     * @param section_lat
     */
    public void setSectionLat(String section_lat) {
        this.sectionLat = section_lat;
    }

    /**
     * Gets longitude of Section object
     * @return
     */
    public String getSectionLng() {
        return sectionLng;
    }

    /**
     * Sets longitude of Section object
     * @param section_lng
     */
    public void setSectionLng(String section_lng) {
        this.sectionLng = section_lng;
    }

    /**
     * Gets id of Route object to which Section object belongs
     * @return
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * Sets id to Route object to which Section object belongs
     * @param routeId
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
