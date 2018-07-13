package com.mabsapps.propertydodo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtMap;
    private  boolean category=false;
    private Button btnSaved, btnSearch, btnSettings, btnRent, btnBuy, btnFilter;
//    private DrawerLayout mDrawerLayout;
//    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private FloatingActionButton btnMap;
    private FrameLayout frameLayout,frameLayoutDetails;
    private RelativeLayout header;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface font = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSaved = (Button) findViewById(R.id.btnSaved);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnBuy = (Button) findViewById(R.id.btnBuy);
        btnRent = (Button) findViewById(R.id.btnRent);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        frameLayout = (FrameLayout)findViewById(R.id.fragment_container);
        frameLayoutDetails = (FrameLayout)findViewById(R.id.fragment_container_details);
        btnMap = (FloatingActionButton)findViewById(R.id.btnMap);
        header = (RelativeLayout)findViewById(R.id.header);
        txtMap = (TextView)findViewById(R.id.txtMap);

        txtMap.setTypeface(font);
        btnSaved.setTypeface(font);
        btnSearch.setTypeface(font);
        btnSettings.setTypeface(font);


        buttonRent(null);
        buttonSearch(null);

        frameLayoutDetails.setVisibility(View.GONE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        checkForUpdates();
    }

    public void buttonMap(View view) {

            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);
    }

    public void buttonFilter(View view){
        Intent intent = new Intent(MainActivity.this,filters.class);
        if(category){
            intent.putExtra("category","Buy");
        }else{
            intent.putExtra("category","Rent");
        }

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void buttonSaved(View view) {

        frameLayout.setVisibility(View.VISIBLE);

        btnSaved.setTextColor(Color.parseColor("#4c6fb3"));
        btnSearch.setTextColor(Color.parseColor("#808080"));
        btnSettings.setTextColor(Color.parseColor("#808080"));

        header.setVisibility(View.GONE);

        fragment = new Saved_Fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
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
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    public void buttonRent(View view) {

        category = false;
        btnRent.setBackgroundColor(Color.parseColor("#4c6fb3"));
        btnRent.setTextColor(Color.parseColor("#e7e7e7"));

        btnBuy.setBackgroundColor(Color.parseColor("#e7e7e7"));
        btnBuy.setTextColor(Color.parseColor("#4c6fb3"));

        fragment = new Rent_ListView_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void buttonBuy(View view) {
        category=true;
        btnBuy.setBackgroundColor(Color.parseColor("#4c6fb3"));
        btnBuy.setTextColor(Color.parseColor("#e7e7e7"));

        btnRent.setBackgroundColor(Color.parseColor("#e7e7e7"));
        btnRent.setTextColor(Color.parseColor("#4c6fb3"));

        fragment = new Buy_ListView_Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.filter1) {
            Toast.makeText(getApplicationContext(), "Filter 1 Clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.filter2) {
            Toast.makeText(getApplicationContext(), "filter 2 Clicket", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
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