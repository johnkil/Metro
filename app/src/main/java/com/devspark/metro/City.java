package com.devspark.metro;

import android.content.Context;
import android.location.Location;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.devspark.metro.util.Locations;

public class City {
    public final int id;
    public final String name;
    public final ImageSource imageSource;
    public final ImageSource previewImageSource;
    public final Location location;

    private City(int id, String name, ImageSource imageSource,
                 ImageSource previewImageSource, Location location) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.previewImageSource = previewImageSource;
        this.location = location;
    }

    /* MOSCOW */

    public static final int ID_MSK = 0;

    public static City createMoscow(Context context) {
        return new City(
                ID_MSK,
                context.getString(R.string.city_moscow),
                ImageSource.resource(R.drawable.map_moscow).dimensions(4211, 5143),
                ImageSource.resource(R.drawable.map_moscow_preview),
                Locations.createLocation(55.749792, 37.632495));
    }

    /* SAINT PETERSBURG */

    public static final int ID_SPB = 1;

    public static City createSpb(Context context) {
        return new City(
                ID_SPB,
                context.getString(R.string.city_spb),
                ImageSource.resource(R.drawable.map_spb).dimensions(4233, 5200),
                ImageSource.resource(R.drawable.map_spb_preview),
                Locations.createLocation(59.9174455, 30.3250575));
    }
}