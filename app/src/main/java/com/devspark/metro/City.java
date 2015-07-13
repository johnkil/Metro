package com.devspark.metro;

import android.location.Location;

import com.davemorrissey.labs.subscaleview.ImageSource;

public class City {
    public static final int ID_MSK = 0;
    public static final int ID_SPB = 1;

    public final int id;
    public final String name;
    public final ImageSource imageSource;
    public final ImageSource previewImageSource;
    public final Location location;

    public City(int id, String name, ImageSource imageSource,
                ImageSource previewImageSource, Location location) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.previewImageSource = previewImageSource;
        this.location = location;
    }
}