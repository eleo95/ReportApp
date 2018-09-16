package com.eleo95.reportapp.model;

public class Upload {
    private String mTitle;
    private String mImageUrl;
    private String mDescription;
    private String mLocation;
    private String mKey;

    public Upload() {
        //constructor vacío necesario para adapter
    }

    public Upload(String title, String imageUrl, String description, String location, String key) {
        if (title.trim().equals("")) {
            title = "No Title";
        }
        if (description.trim().equals("")) {
            description = "No descrpt";
        }
        if (location.trim().equals("")) {
            location = "No location";
        }
        this.mTitle = title;
        this.mImageUrl = imageUrl;
        this.mDescription = description;
        this.mLocation = location;
        this.mKey = key;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
