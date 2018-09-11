package com.mabsapps.propertydodo;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class StringClusterItem implements ClusterItem {

    final String title;
    final LatLng latLng;
    public StringClusterItem(String title, LatLng latLng) {
        this.title = title;
        this.latLng = latLng;
    }
    @Override public LatLng getPosition() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }
}
