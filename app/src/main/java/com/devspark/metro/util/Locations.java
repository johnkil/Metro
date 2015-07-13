package com.devspark.metro.util;

import android.location.Location;

import com.devspark.metro.City;

import java.util.List;

public final class Locations {
    private static final float CITY_RADIUS = 30000f;

    public static Location moscowLocation() {
        return createLocation(55.749792, 37.632495);
    }

    public static Location spbLocation() {
        return createLocation(59.9174455, 30.3250575);
    }

    public static Location createLocation(double lat, double lng) {
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }

    public static City nearestCity(Location location, List<City> cities, City defaultCity) {
        City nearestCity = null;
        float minDistance = 0;
        for (City city : cities) {
            float distance = location.distanceTo(city.location);
            if (nearestCity == null || distance < minDistance) {
                nearestCity = city;
                minDistance = distance;
            }
        }
        if (minDistance > CITY_RADIUS) {
            nearestCity = defaultCity;
        }
        return nearestCity;
    }

    private Locations() {
        throw new AssertionError("No instances.");
    }
}