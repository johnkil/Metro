package com.devspark.metro;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.devspark.metro.util.Locations;

public class City implements Parcelable {
    public final int id;
    public final String name;
    public final Location location;
    public final int mapResId;
    public final int mapWidth;
    public final int mapHeight;
    public final int mapPreviewResId;

    private City(int id, String name, Location location,
                 int mapResId, int mapWidth, int mapHeight, int mapPreviewResId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.mapResId = mapResId;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapPreviewResId = mapPreviewResId;
    }

    /* MOSCOW */

    public static final int ID_MSK = 0;

    public static City createMoscow(Context context) {
        return new City(
                ID_MSK,
                context.getString(R.string.city_moscow),
                Locations.createLocation(55.749792, 37.632495),
                R.drawable.map_moscow, 4211, 5143,
                R.drawable.map_moscow_preview);
    }

    /* SAINT PETERSBURG */

    public static final int ID_SPB = 1;

    public static City createSpb(Context context) {
        return new City(
                ID_SPB,
                context.getString(R.string.city_spb),
                Locations.createLocation(59.9174455, 30.3250575),
                R.drawable.map_spb, 4233, 5200,
                R.drawable.map_spb_preview);
    }

    /* -------- PARCELABLE -------- */

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    private City(Parcel in) {
        id = in.readInt();
        name = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        mapResId = in.readInt();
        mapWidth = in.readInt();
        mapHeight = in.readInt();
        mapPreviewResId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeInt(mapResId);
        dest.writeInt(mapWidth);
        dest.writeInt(mapHeight);
        dest.writeInt(mapPreviewResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}