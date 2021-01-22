package com.example.walksapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Constructs User objects
 */
public class User implements Parcelable {

    //Instance variables
    private String emailAddress;

    public User() {
    }

    public User(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    protected User(Parcel in) {
        emailAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(emailAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
