package com.mabsapps.propertydodo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView txtList, txtBack;
    String[] propertiesLat = {""};
    String[] propertiesLong = {""};
    String[] idLatLong = {""};
    String [] clusterClickItems;
    JSONArray array, properties;
    String finalResult, finalResult2, subCategory, mapCategory = "null", onMapLat, onMapLong, url, filterSearch,
            url2 = "https://admin.propertydodo.ae/api/properties/areas-data", path = "null";
    private Button btnRent, btnBuy, btnBack, btnFilter;
    private ClusterManager<StringClusterItem> mClusterManager;
    private ClusterRenderer clusterRenderer;
    private FloatingActionButton btnList;
    RelativeLayout header, header2;
    View mapView;
    private boolean category = false;
    private AutoCompleteTextView autoText;
    private ArrayAdapter<String> adapter;
    private String[] id, label;
    HashMap<String, LatLng> mapLocations = new HashMap<String, LatLng>();
    FrameLayout frameLayoutDetails;
    private ArrayList<String> clusterItems;

    private int ct, rt, c, bf, bt, af, at, pf, pt, fu, l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();

        mapView = mapFragment.getView();
        mapCategory = intent.getStringExtra("category");
        ct = intent.getIntExtra("ct", 0);
        onMapLat = intent.getStringExtra("lat");
        onMapLong = intent.getStringExtra("long");
        path = intent.getStringExtra("path");
        header = (RelativeLayout) findViewById(R.id.header);
        header2 = (RelativeLayout) findViewById(R.id.header2);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        autoText = (AutoCompleteTextView) findViewById(R.id.autoText);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        btnBuy = (Button) findViewById(R.id.btnBuy);
        btnRent = (Button) findViewById(R.id.btnRent);
        btnBack = (Button) findViewById(R.id.btnBack);
        frameLayoutDetails = (FrameLayout) findViewById(R.id.fragment_container_details);
        frameLayoutDetails.setVisibility(View.GONE);
        txtList = (TextView) findViewById(R.id.txtList);

        txtList.setTypeface(font);
        btnBack.setTypeface(font);
        txtList.setText(R.string.icon_list);
        txtList.setTextColor(Color.parseColor("#4c6fb3"));
        ct = intent.getIntExtra("contract", ct);
        rt = intent.getIntExtra("payment", 0);
        c = intent.getIntExtra("type", 0);
        bf = intent.getIntExtra("minBedroom", 0);
        bt = intent.getIntExtra("maxBedroom", 0);
        af = intent.getIntExtra("minArea", 0);
        at = intent.getIntExtra("maxArea", 0);
        pf = intent.getIntExtra("minPrice", 0);
        pt = intent.getIntExtra("maxPrice", 0);
        fu = intent.getIntExtra("furnishing", 0);
        subCategory = intent.getStringExtra("subCategory");
        setUrl();
        getLocationsData();

        filterSearch = intent.getStringExtra("category");
        if (filterSearch == null) {
            filterSearch = "Rent";
            ct = 1;
        }

        if (filterSearch.equals("Rent")) {
            btnRent.performClick();

        } else {
            btnBuy.performClick();

        }
        txtList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonMap(null);
            }
        });

        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = (String) adapterView.getAdapter().getItem(i);
                searchIdFromLocation(selectedItem);

                if (filterSearch.equals("Rent")) {
                    btnRent.performClick();

                } else {
                    btnBuy.performClick();

                }
            }
        });

    }

    public void getLocationsData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url2).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                if (response.isSuccessful()) {
                    finalResult2 = response.body().string().toString();
                    getJsonDataLoc(finalResult2);
                }
            }
        });
    }

    public void getJsonDataLoc(String response) {
        try {
            array = new JSONArray(response);
            label = new String[array.length()];
            id = new String[array.length()];

            for (int j = 0; j < array.length(); j++) {
                id[j] = (array.getJSONObject(j).getString("id"));

                label[j] = (array.getJSONObject(j).getString("label"));
            }

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, label);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    autoText.setAdapter(adapter);
                    autoText.setThreshold(1);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchIdFromLocation(String location) {

        for (int i = 0; i < label.length; i++) {
            if (location.equals(label[i]) && location != null) {
                l = Integer.valueOf(id[i]);
            }
        }
    }

    private void setUrl() {
        url = "https://admin.propertydodo.ae/api/properties/map/all?ct=";
        url += ct;
        url += (rt != 0) ? "&rt=" + rt : "";
        url += (c != 0) ? "&c=" + c : "";
        url += (bf != 0) ? "&bf=" + bf : "";
        url += (bt != 0) ? "&bt=" + bt : "";
        url += (af != 0) ? "&af=" + af : "";
        url += (at != 0) ? "&at=" + at : "";
        url += (pf != 0) ? "&pf=" + pf : "";
        url += (pt != 0) ? "&pt=" + pt : "";
        url += (fu != 0) ? "&fu=" + fu : "";
        url += (l != 0) ? "&l=" + l : "";
    }

    public void setProperties() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResult = response.body().string().toString();
                    getJsonData(finalResult);

                }
            }
        });
    }

    public void buttonFilter(View view) {
        Intent intent = new Intent(MapsActivity.this, filters.class);
        intent.putExtra("typeActivity", "map");
        if (category) {
            intent.putExtra("category", "Buy");
        } else {
            intent.putExtra("category", "Rent");
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void buttonMap(View view) {

        txtList.setText(R.string.icon_list);
        txtList.setTextColor(Color.parseColor("#4c6fb3"));

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.putExtra("category", mapCategory);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);


        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapCategory = "Rent";
                category = false;
                ct = 1;
                if (subCategory != null && subCategory.equals("Commercial")) {
                    ct = 3;
                }
                setUrl();
                btnRent.setBackgroundColor(Color.parseColor("#4c6fb3"));
                btnRent.setTextColor(Color.parseColor("#e7e7e7"));

                btnBuy.setBackgroundColor(Color.parseColor("#e7e7e7"));
                btnBuy.setTextColor(Color.parseColor("#4c6fb3"));

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response.isSuccessful()) {
                            finalResult = response.body().string().toString();
                            getJsonData(finalResult);
                            // setProperties();
                            if (getApplicationContext() == null) {
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.clear();
                                    LatLng UAE = new LatLng(23.4241, 53.8478);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UAE, 7));
                                    mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

                                    mMap.setOnCameraChangeListener(mClusterManager);

                                    mClusterManager.setRenderer(new ClusterRenderer(MapsActivity.this, mMap, mClusterManager));
                                    //mMap.setOnCameraIdleListener(mClusterManager);
                                    mMap.setOnMarkerClickListener(mClusterManager);
                                    mMap.setOnInfoWindowClickListener(mClusterManager);
                                    mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<StringClusterItem>() {
                                        @Override
                                        public boolean onClusterClick(Cluster<StringClusterItem> cluster) {

                                            clusterItems = new ArrayList<>(cluster.getSize());

                                            try{
                                                for(StringClusterItem clusterProps : cluster.getItems()){
                                                    clusterItems.add(clusterProps.title);
                                                    clusterClickItems = clusterItems.toArray(new String[clusterItems.size()]);
                                                    clusterRequest();                                                }

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                            return false;
                                        }
                                    });
                                    mClusterManager.setOnClusterInfoWindowClickListener(null);
                                    mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<StringClusterItem>() {
                                        @Override
                                        public boolean onClusterItemClick(StringClusterItem marker) {

                                            LatLng id = mapLocations.get(marker.getTitle());
                                            String specificId = marker.getTitle();

                                            // String specificId = idBuy[position];
                                            //mapCategory = "null";
                                            frameLayoutDetails.setVisibility(View.VISIBLE);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", specificId);
                                            bundle.putString("path", "map");
                                            Fragment fragment2 = new BuyPropertyDetail_Fragment();
                                            fragment2.setArguments(bundle);
                                            getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragment_container_details, fragment2).commit();


                                            return false;
                                        }
                                    });
                                    mClusterManager.setOnClusterItemInfoWindowClickListener(null);

                                    for (int i = 0; i < propertiesLat.length; i++) {
                                        final LatLng latLng = new LatLng(Float.valueOf(propertiesLat[i]), Float.valueOf(propertiesLong[i]));
                                        mClusterManager.addItem(new StringClusterItem(idLatLong[i], latLng));

                                        mapLocations.put(idLatLong[i], latLng);
                                    }
                                    mClusterManager.cluster();

                                }
                            });

                        }
                    }

                });
            }
        });


        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapCategory = "Buy";
                category = true;
                ct = 2;
                if (subCategory != null && subCategory.equals("Commercial")) {
                    ct = 4;
                }
                setUrl();
                btnBuy.setBackgroundColor(Color.parseColor("#4c6fb3"));
                btnBuy.setTextColor(Color.parseColor("#e7e7e7"));

                btnRent.setBackgroundColor(Color.parseColor("#e7e7e7"));
                btnRent.setTextColor(Color.parseColor("#4c6fb3"));

                // try {
                //clusterProps();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response.isSuccessful()) {
                            finalResult = response.body().string().toString();
                            getJsonData(finalResult);
                            // setProperties();
                            if (getApplicationContext() == null) {
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.clear();
                                    LatLng UAE = new LatLng(23.4241, 53.8478);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UAE, 7));
                                    mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

                                    mMap.setOnCameraChangeListener(mClusterManager);

                                    mClusterManager.setRenderer(new ClusterRenderer(MapsActivity.this, mMap, mClusterManager));
                                    //mMap.setOnCameraIdleListener(mClusterManager);
                                    mMap.setOnMarkerClickListener(mClusterManager);
                                    mMap.setOnInfoWindowClickListener(mClusterManager);
                                    mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<StringClusterItem>() {
                                        @Override
                                        public boolean onClusterClick(Cluster<StringClusterItem> cluster) {

                                            clusterItems = new ArrayList<>(cluster.getSize());

                                            try{
                                                for(StringClusterItem clusterProps : cluster.getItems()){
                                                    clusterItems.add(clusterProps.title);
                                                    clusterClickItems = clusterItems.toArray(new String[clusterItems.size()]);
                                                    clusterRequest();
                                                }

                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

//                                            Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }
                                    });
                                    mClusterManager.setOnClusterInfoWindowClickListener(null);
                                    mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<StringClusterItem>() {
                                        @Override
                                        public boolean onClusterItemClick(StringClusterItem marker) {


                                            LatLng id = mapLocations.get(marker.getTitle());
                                            String specificId = marker.getTitle();

                                            frameLayoutDetails.setVisibility(View.VISIBLE);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", specificId);
                                            bundle.putString("path", "map");
                                            Fragment fragment2 = new BuyPropertyDetail_Fragment();
                                            fragment2.setArguments(bundle);
                                            getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragment_container_details, fragment2).commit();


                                            return false;
                                        }
                                    });
                                    mClusterManager.setOnClusterItemInfoWindowClickListener(null);

                                    for (int i = 0; i < propertiesLat.length; i++) {
                                        final LatLng latLng = new LatLng(Float.valueOf(propertiesLat[i]), Float.valueOf(propertiesLong[i]));
                                        mClusterManager.addItem(new StringClusterItem(idLatLong[i], latLng));

                                        mapLocations.put(idLatLong[i], latLng);
                                    }
                                    mClusterManager.cluster();

                                }
                            });

                        }
                    }

                });

            }
        });

        if (path.equals("main")) {
            header2.setVisibility(View.GONE);
            if (mapCategory.equals("Buy")) {
                btnBuy.performClick();
            } else if (mapCategory.equals("Rent")) {
                btnRent.performClick();
            }
        } else {
            txtList.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            singleProperty(mMap, Double.parseDouble(onMapLat), Double.parseDouble(onMapLong));
        }


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 10, 80);
        }
    }

    private void clusterRequest() {

        String url3="https://admin.propertydodo.ae/api/properties/cluster";

        for(int i=0;i<clusterClickItems.length;i++){
            url3+=(i==0) ? "?":"&";
            url3+="properties[]="+clusterClickItems[i];
        }

    }

    private void clusterProps() throws IOException {

        Log.d("---MAP--", "" + url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResult = response.body().string().toString();
                    getJsonData(finalResult);
                    // setProperties();
                    if (getApplicationContext() == null) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();
                            LatLng UAE = new LatLng(23.4241, 53.8478);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UAE, 7));
                            mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

                            mMap.setOnCameraChangeListener(mClusterManager);

                            mClusterManager.setRenderer(new ClusterRenderer(MapsActivity.this, mMap, mClusterManager));


                            for (int i = 0; i < propertiesLat.length; i++) {
                                final LatLng latLng = new LatLng(Float.valueOf(propertiesLat[i]), Float.valueOf(propertiesLong[i]));
                                mClusterManager.addItem(new StringClusterItem(idLatLong[i], latLng));

                                mapLocations.put(idLatLong[i], latLng);
                            }
                            mClusterManager.cluster();

                        }
                    });

                }
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                } else {

                }
                return;
            }

        }
    }


    public void singleProperty(GoogleMap mMap, Double mlat, Double mlong) {
        LatLng property = new LatLng(mlat, mlong);
        mMap.addMarker(new MarkerOptions().position(property));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(property, 8));
    }


    public void onbtnBackPressed(View view) {
        android.app.FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        android.app.FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            //fm.popBackStack();
            this.finish();
        } else {
            fm.popBackStack();
            //super.onBackPressed();
        }
    }

    public void getJsonData(String response) {
        properties = new JSONArray();
        try {
            array = new JSONObject(response).getJSONArray("locations");

            propertiesLat = new String[array.length()];
            propertiesLong = new String[array.length()];
            idLatLong = new String[array.length()];
            for (int i = 0; i < propertiesLong.length; i++) {

                propertiesLat[i] = array.getJSONObject(i).getString("Lat");
                propertiesLong[i] = array.getJSONObject(i).getString("Long");
                idLatLong[i] = array.getJSONObject(i).getString("property");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
