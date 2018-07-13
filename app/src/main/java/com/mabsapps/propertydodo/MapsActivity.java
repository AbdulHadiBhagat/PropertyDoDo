package com.mabsapps.propertydodo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView txtList;
    String [] propertiesLat = {""};
    String [] propertiesLong = {""};
    JSONArray array,properties;
    String finalResult;
    private Button btnRent, btnBuy;
    private ClusterManager<StringClusterItem> mClusterManager;
    private FloatingActionButton btnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        btnBuy = (Button)findViewById(R.id.btnBuy);
        btnRent = (Button)findViewById(R.id.btnRent);
        btnList =(FloatingActionButton)findViewById(R.id.btnList);
        txtList =(TextView)findViewById(R.id.txtList);
        txtList.setTypeface(font);
        txtList.setText(R.string.icon_list);
        txtList.setTextColor(Color.parseColor("#4c6fb3"));

    }

    public void setProperties(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://adminstage.propertydodo.ae/api/properties/map/rent";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()) {
                    finalResult = response.body().string().toString();
                    getJsonData(finalResult);
                    Log.d("----MSG----",finalResult+"");
                }
            }
        });
    }

    public void buttonMap(View view) {

            txtList.setText(R.string.icon_list);
            txtList.setTextColor(Color.parseColor("#4c6fb3"));

            Intent intent = new Intent(MapsActivity.this,MainActivity.class);
            startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //setProperties();

        mMap = googleMap;

        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRent.setBackgroundColor(Color.parseColor("#4c6fb3"));
                btnRent.setTextColor(Color.parseColor("#e7e7e7"));

                btnBuy.setBackgroundColor(Color.parseColor("#e7e7e7"));
                btnBuy.setTextColor(Color.parseColor("#4c6fb3"));
                OkHttpClient client = new OkHttpClient();
                String url = "https://adminstage.propertydodo.ae/api/properties/map/rent";
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if(response.isSuccessful()) {
                            finalResult = response.body().string().toString();
                            getJsonData(finalResult);
                            // Log.d("----MSG----",finalResult+"");
                            setProperties();
                            if(getApplicationContext()==null){
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.clear();
                                    LatLng sydney = new LatLng(23.4241, 53.8478);
                                    mMap.addMarker(new MarkerOptions().position(sydney).title("UAE"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));
                                    mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

                                    mMap.setOnCameraChangeListener(mClusterManager);

                                    for (int i = 0; i < propertiesLat.length; i++) {
                                        final LatLng latLng = new LatLng(Float.valueOf(propertiesLat[i]), Float.valueOf(propertiesLong[i]));
                                        mClusterManager.addItem(new StringClusterItem("Marker #" + (i + 1), latLng));
                                    }
                                    //Log.d("----MSG----", "run: "+propertiesLat.length);
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
                btnBuy.setBackgroundColor(Color.parseColor("#4c6fb3"));
                btnBuy.setTextColor(Color.parseColor("#e7e7e7"));

                btnRent.setBackgroundColor(Color.parseColor("#e7e7e7"));
                btnRent.setTextColor(Color.parseColor("#4c6fb3"));
                OkHttpClient client = new OkHttpClient();
                String url = "https://adminstage.propertydodo.ae/api/properties/map/buy";
                Request request = new Request.Builder().url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if(response.isSuccessful()) {
                            finalResult = response.body().string().toString();
                            getJsonData(finalResult);
                            // Log.d("----MSG----",finalResult+"");
                            if(getApplicationContext()==null){
                                return;
                            }
                            setProperties();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMap.clear();
                                    LatLng sydney = new LatLng(23.4241, 53.8478);
                                    mMap.addMarker(new MarkerOptions().position(sydney).title("UAE"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,4));
                                    mClusterManager = new ClusterManager<>(getApplicationContext(), mMap);

                                    mMap.setOnCameraChangeListener(mClusterManager);

                                    for (int i = 0; i < propertiesLat.length; i++) {
                                        final LatLng latLng = new LatLng(Float.valueOf(propertiesLat[i]), Float.valueOf(propertiesLong[i]));
                                        mClusterManager.addItem(new StringClusterItem("Marker #" + (i + 1), latLng));
                                    }
                                    //Log.d("----MSG----", "run: "+propertiesLat.length);
                                    mClusterManager.cluster();
                                }
                            });

                        }
                    }
                });
            }
        });


    }
    public void getJsonData(String response){
        properties = new JSONArray();
        try {
            array = new JSONObject(response).getJSONArray("locations");
            for(int j=0;j<array.length();j++){

            }

            propertiesLat = new String[array.length()];
            propertiesLong = new String[array.length()];
            for(int i=0;i<array.length();i++){

                propertiesLat[i]=array.getJSONObject(i).getString("Lat");
                propertiesLong[i]=array.getJSONObject(i).getString("Long");

            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
