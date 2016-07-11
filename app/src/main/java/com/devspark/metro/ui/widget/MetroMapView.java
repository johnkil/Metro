package com.devspark.metro.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.devspark.metro.data.City;

public class MetroMapView extends SubsamplingScaleImageView {

    public MetroMapView(Context context, AttributeSet attr) {
        super(context, attr);
        setDoubleTapZoomDpi(480);
        setMinimumDpi(320);
    }

    public void showMap(City city) {
        showMap(city, null);
    }

    public void showMap(City city, ImageViewState state) {
        setImage(
                ImageSource.resource(city.mapResId).dimensions(city.mapWidth, city.mapHeight),
                ImageSource.resource(city.mapPreviewResId),
                state);
    }
}