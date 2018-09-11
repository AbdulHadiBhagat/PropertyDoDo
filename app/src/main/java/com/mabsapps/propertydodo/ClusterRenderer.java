package com.mabsapps.propertydodo;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class ClusterRenderer extends DefaultClusterRenderer<StringClusterItem> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<StringClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected int getColor(int clusterSize) {
        return Color.parseColor("#4c6fb3");
    }

    protected void onBeforeClusterItemRendered(StringClusterItem item, MarkerOptions markerOptions) {

        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
