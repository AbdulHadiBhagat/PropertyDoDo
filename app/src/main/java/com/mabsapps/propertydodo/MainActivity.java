package com.mabsapps.propertydodo;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
//import android.widget.SearchView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView txtMap, txtSort;
    private boolean category = false;
    private Button btnSaved, btnSearch, btnSettings, btnRent, btnBuy, btnFilter;
    private String filterSearch, subCategory,sort="&sort=",
            url = "https://admin.propertydodo.ae/api/properties/areas-data",finalResult,detailsPath=null,companyID;
    private boolean categoryChoice = false;
    private FloatingActionButton btnMap, btnSort;
    private FrameLayout frameLayout, frameLayoutDetails;
    private RelativeLayout header;
    private Fragment fragment;
    private Bundle bundleFilters;
    private JSONArray array;
    private String[] id,label;
    private AutoCompleteTextView autoText;
    private ArrayAdapter<String> adapter;



    private int ct, rt, c, bf, bt, af, at, pf, pt, fu, l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSaved = (Button) findViewById(R.id.btnSaved);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnBuy = (Button) findViewById(R.id.btnBuy);
        btnRent = (Button) findViewById(R.id.btnRent);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        frameLayoutDetails = (FrameLayout) findViewById(R.id.fragment_container_details);
        btnMap = (FloatingActionButton) findViewById(R.id.btnMap);
        btnSort = (FloatingActionButton) findViewById(R.id.btnSort);
        header = (RelativeLayout) findViewById(R.id.header);
        txtMap = (TextView) findViewById(R.id.txtMap);
        txtSort = (TextView) findViewById(R.id.txtSort);
        autoText = (AutoCompleteTextView)findViewById(R.id.autoText);
        txtMap.setTypeface(font);
        txtSort.setTypeface(font);
        btnSaved.setTypeface(font);
        btnSearch.setTypeface(font);
        btnSettings.setTypeface(font);
        Intent intent = getIntent();
        ct = intent.getIntExtra("contract", 0);
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
        detailsPath = intent.getStringExtra("detailsPath");
        companyID = intent.getStringExtra("companyId");



        frameLayoutDetails.setVisibility(View.GONE);
        filterSearch = intent.getStringExtra("category");
        if (filterSearch == null) {
            filterSearch = "Rent";
            ct = 1;
        }

        if (filterSearch.equals("Rent")) {
            buttonRent(null);
        } else {
            buttonBuy(null);
        }

        buttonSearch(null);
        getLocationsData();

        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = (String) adapterView.getAdapter().getItem(i);
                searchIdFromLocation(selectedItem);

                if (filterSearch.equals("Rent")) {
                    buttonRent(null);
                } else {
                    buttonBuy(null);
                }
            }
        });
        header.setVisibility(View.VISIBLE);

        if(detailsPath != null){

            Log.d("--MSG--","start");
            Bundle args = new Bundle();
            args.putString("companyId", companyID);
            fragment = new Buy_ListView_Fragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragment_container, fragment).commit();
            header.setVisibility(View.GONE);
        }

        frameLayoutDetails.setVisibility(View.GONE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        checkForUpdates();

    }

    public void getLocationsData() {
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
    public void getJsonData(String response){

            try{
                array = new JSONArray(response);
                label = new String[array.length()];
                id = new String[array.length()];
                for (int j = 0 ;j<array.length();j++){
                id[j] = (array.getJSONObject(j).getString("id"));

                label[j] = (array.getJSONObject(j).getString("label"));
            }

            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,label);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    autoText.setAdapter(adapter);
                    autoText.setThreshold(1);

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void searchIdFromLocation(String location){

        for(int i =0;i<label.length;i++){
            if(location.equals(label[i]) && location != null){
                l = Integer.valueOf(id[i]);
            }
        }
    }

    public void buttonMap(View view) {

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        Log.d("Ct before map",""+ct);
        if (category) {

            intent.putExtra("category", "Buy");
            intent.putExtra("path", "main");
            intent.putExtra("ct", ct);
            startActivity(intent);
        } else {
            intent.putExtra("category", "Rent");
            intent.putExtra("path", "main");
            intent.putExtra("ct", ct);
            startActivity(intent);
        }
    }

    public void buttonSort(View view){

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnSort);
        popupMenu.getMenuInflater().inflate(R.menu.sort_popup,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

               // url+= (fu != 0 ) ? "&fu="+fu:"";

               // if(menuItem.getTitle().equals("Newest")){
                    sort = "&sort=";
                    sort += (menuItem.getTitle().equals("Newest")) ?  "t" :"";
                    sort += (menuItem.getTitle().equals("Price (low)")) ?  "pl" :"";
                    sort += (menuItem.getTitle().equals("Price (high)")) ?  "ph" :"";
                    sort += (menuItem.getTitle().equals("Beds (least)")) ?  "bl" :"";
                    sort += (menuItem.getTitle().equals("Beds (most)")) ?  "bh" :"";
                //}
                //else if{

                //}
                if(categoryChoice == false){
                    buttonBuy(null);
                }else{
                    buttonRent(null);
                }

                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onBackPressed() {
        android.app.FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    public void buttonFilter(View view) {
        Intent intent = new Intent(MainActivity.this, filters.class);
        intent.putExtra("ct", ct);
        intent.putExtra("rt", rt);
        intent.putExtra("c", c);
        intent.putExtra("bf", bf);
        intent.putExtra("bt", bt);
        intent.putExtra("af", af);
        intent.putExtra("at", at);
        intent.putExtra("pf", pf);
        intent.putExtra("pt", pt);
        intent.putExtra("fu", fu);
        intent.putExtra("typeActivity","main");

        if (category) {
            intent.putExtra("category", "Buy");
        } else {
            intent.putExtra("category", "Rent");
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void buttonSaved(View view) {

        frameLayout.setVisibility(View.VISIBLE);

        btnSaved.setTextColor(Color.parseColor("#4c6fb3"));
        btnSearch.setTextColor(Color.parseColor("#808080"));
        btnSettings.setTextColor(Color.parseColor("#808080"));

        header.setVisibility(View.GONE);

        fragment = new Saved_Fragment();
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment).commit();
    }

    public void buttonSearch(View view) {

        btnSaved.setTextColor(Color.parseColor("#808080"));
        btnSearch.setTextColor(Color.parseColor("#4c6fb3"));
        btnSettings.setTextColor(Color.parseColor("#808080"));

        header.setVisibility(View.VISIBLE);

        frameLayout.setVisibility(View.GONE);

    }

    public void buttonSettings(View view) {

        btnSaved.setTextColor(Color.parseColor("#808080"));
        btnSearch.setTextColor(Color.parseColor("#808080"));
        btnSettings.setTextColor(Color.parseColor("#4c6fb3"));

        header.setVisibility(View.GONE);

        frameLayout.setVisibility(View.VISIBLE);

        fragment = new Settings_Fragment();
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment).commit();
    }

    public void buttonRent(View view) {
        ct = 1;
        categoryChoice = true;

        if (subCategory != null && subCategory.equals("Commercial")) {
            ct = 3;
        }
        filterSearch = "Rent";
        category = false;
        btnRent.setBackgroundColor(Color.parseColor("#4c6fb3"));
        btnRent.setTextColor(Color.parseColor("#e7e7e7"));

        btnBuy.setBackgroundColor(Color.parseColor("#e7e7e7"));
        btnBuy.setTextColor(Color.parseColor("#4c6fb3"));

        bundleFilters = new Bundle();
        bundleFilters.putInt("ct", ct);
        bundleFilters.putInt("rt", rt);
        bundleFilters.putInt("c", c);
        bundleFilters.putInt("bf", bf);
        bundleFilters.putInt("bt", bt);
        bundleFilters.putInt("af", af);
        bundleFilters.putInt("at", at);
        bundleFilters.putInt("pf", pf);
        bundleFilters.putInt("pt", pt);
        bundleFilters.putInt("fu", fu);
        bundleFilters.putInt("l",l);
        bundleFilters.putString("sort",sort);

        subCategory = "Residential";

        fragment = new Rent_ListView_Fragment();
        fragment.setArguments(bundleFilters);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment).commit();
        getLocationsData();
    }

    public void buttonBuy(View view) {
        ct = 2;
        categoryChoice = false;
        if (subCategory != null && subCategory.equals("Commercial")) {
            ct = 4;
        }
        filterSearch = "Buy";
        category = true;
        btnBuy.setBackgroundColor(Color.parseColor("#4c6fb3"));
        btnBuy.setTextColor(Color.parseColor("#e7e7e7"));

        btnRent.setBackgroundColor(Color.parseColor("#e7e7e7"));
        btnRent.setTextColor(Color.parseColor("#4c6fb3"));

        bundleFilters = new Bundle();
        bundleFilters.putInt("ct", ct);
        bundleFilters.putInt("rt", rt);
        bundleFilters.putInt("c", c);
        bundleFilters.putInt("bf", bf);
        bundleFilters.putInt("bt", bt);
        bundleFilters.putInt("af", af);
        bundleFilters.putInt("at", at);
        bundleFilters.putInt("pf", pf);
        bundleFilters.putInt("pt", pt);
        bundleFilters.putInt("fu", fu);
        bundleFilters.putInt("l",l);
        bundleFilters.putString("sort",sort);
        fragment = new Buy_ListView_Fragment();
        fragment.setArguments(bundleFilters);
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment).commit();
        getLocationsData();
    }


//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//
//        if (id == R.id.filter1) {
//            Toast.makeText(getApplicationContext(), "Filter 1 Clicked", Toast.LENGTH_SHORT).show();
//        }
//        if (id == R.id.filter2) {
//            Toast.makeText(getApplicationContext(), "filter 2 Clicket", Toast.LENGTH_SHORT).show();
//        }
//
//        return false;
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        // ... your own onResume implementation
//        checkForCrashes();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        unregisterManagers();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterManagers();
//    }
//    private void checkForCrashes() {
//        CrashManager.register(this);
//    }
//
//    private void checkForUpdates() {
//        // Remove this for store builds!
//        UpdateManager.register(this);
//    }
//
//    private void unregisterManagers() {
//        UpdateManager.unregister();
//    }
}