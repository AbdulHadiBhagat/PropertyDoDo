package com.mabsapps.propertydodo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class filters extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private Button btnBack,search,btnReset;
    private String finalResult,finalResultTypeRent,finalResultTypeBuy,finalResultTypeCommercialRent,finalResultTypeCommercialBuy,finalResultPayment;
    private Spinner typeSpinner, minAreaSpinner, maxAreaSpinner, minBedroomSpinner, maxBedroomSpinner, furnishingSPinner,
            paymentSpinner, minPriceSpinner, maxPriceSpinner;
    private LinearLayout bedroomsLinear, furnishingLinear, paymentLinear;
    private TableRow bedroomsRow, furnishingRow, paymentRow;
    private RadioButton radioCommercial, radioResidential;
    private String category, subCategory = null;
    private ProgressBar progressBar;
    private String activityType;
    private RelativeLayout progressBarLayout;
    private int ct,rt,c,bf,bt,af,at,pf,pt,fu;

    private JSONArray arrayJsonFurnishing,arrayJsonPayment,arrayJsonTypeRent,arrayJsonTypeCommRent,arrayJsonTypeBuy,arrayJsonTypeCommBuy;
    ArrayList<FiltersFurnishing> furnishingRent;
    ArrayList<FiltersPayment> paymentRent;
    ArrayList<FiltersTypes> typeRent,typeCommRent,typeBuy,typeCommBuy;
    String[] furnishings, typesRent,typesCommRent,typesBuy,typesCommBuy,payments,areaTo,areaFrom,minPrice,maxPrice,minBedrooms,maxBedrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Intent intent = getIntent();

        category = intent.getStringExtra("category");
//        ct = intent.getIntExtra("contract",0);
//        rt = intent.getIntExtra("payment",0);
//        c = intent.getIntExtra("type",0);
//        bf = intent.getIntExtra("minBedroom",0);
//        bt = intent.getIntExtra("maxBedroom",0);
//        af = intent.getIntExtra("minArea",0);
//        at = intent.getIntExtra("maxArea",0);
//        pf = intent.getIntExtra("minPrice",0);
//        pt = intent.getIntExtra("maxPrice",0);
//        fu = intent.getIntExtra("furnishing",0);
        activityType = intent.getStringExtra("typeActivity");


        subCategory = intent.getStringExtra("subCategory");

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        btnBack = (Button) findViewById(R.id.btnBack);
        btnReset = (Button) findViewById(R.id.btnReset);
        search = (Button) findViewById(R.id.filterSearch);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        minAreaSpinner = (Spinner) findViewById(R.id.minAreaSpinner);
        maxAreaSpinner = (Spinner) findViewById(R.id.maxAreaSpinner);
        minBedroomSpinner = (Spinner) findViewById(R.id.minBedroomSpinner);
        maxBedroomSpinner = (Spinner) findViewById(R.id.maxBedroomsSpinner);
        furnishingSPinner = (Spinner) findViewById(R.id.furnishingSpinner);
        paymentSpinner = (Spinner) findViewById(R.id.paymentSpinner);
        minPriceSpinner = (Spinner) findViewById(R.id.minPriceSpinner);
        maxPriceSpinner = (Spinner) findViewById(R.id.maxPriceSpinner);
        progressBarLayout = (RelativeLayout)findViewById(R.id.progressBarLayout);
        bedroomsLinear = (LinearLayout) findViewById(R.id.bedroomsLinear);
        furnishingLinear = (LinearLayout) findViewById(R.id.furnishingLinear);
        paymentLinear = (LinearLayout) findViewById(R.id.paymentLinear);

        bedroomsRow = (TableRow) findViewById(R.id.bedroomsRow);
        furnishingRow = (TableRow) findViewById(R.id.furnishingRow);
        paymentRow = (TableRow) findViewById(R.id.paymentRow);

        radioCommercial = (RadioButton) findViewById(R.id.radioCommerecial);
        radioResidential = (RadioButton) findViewById(R.id.radioResidencial);

        radioResidential.setChecked(true);
        radioCommercial.setOnCheckedChangeListener(this);
        radioResidential.setOnCheckedChangeListener(this);

        areaTo = getResources().getStringArray(R.array.AreaToDropDown);
        areaFrom = getResources().getStringArray(R.array.AreaFromDropDown);
        minBedrooms = getResources().getStringArray(R.array.bedDropDown);
        maxBedrooms = getResources().getStringArray(R.array.bedDropDown);
        minPrice = getResources().getStringArray(R.array.priceDropDown);
        maxPrice = getResources().getStringArray(R.array.priceDropDown);

        setMaxAreaAdapter();
        setMinAreaAdapter();
        subCategory = null;
        filtersSet();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setfurnishingFilter();
                setPaymentFilter();

            }
        });
        btnBack.setTypeface(font);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                progressBarLayout.setVisibility(View.GONE);
            }
        },1800);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeSpinner.setSelection(0);
                minAreaSpinner.setSelection(0);
                maxAreaSpinner.setSelection(0);
                minBedroomSpinner.setSelection(0);
                maxBedroomSpinner.setSelection(0);
                furnishingSPinner.setSelection(0);
                paymentSpinner.setSelection(0);
                minPriceSpinner.setSelection(0);
                maxPriceSpinner.setSelection(0);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public void onBackPressed() {
        android.app.FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount() > 0){
            fm.popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    public void filterSearch(View v){

        c = typeSpinner.getSelectedItemPosition();
        af = minAreaSpinner.getSelectedItemPosition();
        at = maxAreaSpinner.getSelectedItemPosition();
        bf = minBedroomSpinner.getSelectedItemPosition();
        bt = maxBedroomSpinner.getSelectedItemPosition();
        fu = furnishingSPinner.getSelectedItemPosition();
        rt = paymentSpinner.getSelectedItemPosition();
        pf = minPriceSpinner.getSelectedItemPosition();
        pt = maxPriceSpinner.getSelectedItemPosition();

        Log.d("typeActivity in Filter",""+activityType);

        if(activityType.equals("main")) {
         Intent intent = new Intent(filters.this, MainActivity.class);
            intent.putExtra("subCategory",subCategory);
            intent.putExtra("category",category);
            intent.putExtra("contract",ct);
            intent.putExtra("type",c);
            intent.putExtra("minArea",af);
            intent.putExtra("maxArea",at);
            intent.putExtra("minBedroom",bf);
            intent.putExtra("maxBedroom",bt);
            intent.putExtra("furnishing",fu);
            intent.putExtra("payment",rt);
            intent.putExtra("minPrice",pf);
            intent.putExtra("maxPrice",pt);
            intent.putExtra("path", "main");
            intent.putExtra("typeActivity","main");
            startActivity(intent);
        }else if (activityType.equals("map")){
          Intent  intent = new Intent(filters.this, MapsActivity.class);
            intent.putExtra("subCategory",subCategory);
            intent.putExtra("category",category);
            intent.putExtra("contract",ct);
            intent.putExtra("type",c);
            intent.putExtra("minArea",af);
            intent.putExtra("maxArea",at);
            intent.putExtra("minBedroom",bf);
            intent.putExtra("maxBedroom",bt);
            intent.putExtra("furnishing",fu);
            intent.putExtra("payment",rt);
            intent.putExtra("minPrice",pf);
            intent.putExtra("maxPrice",pt);
            intent.putExtra("path", "main");
            intent.putExtra("typeActivity","main");
            startActivity(intent);
        }

    }

    public void filtersSet() {
        if (category.equals("Buy")) {

            furnishingRow.setVisibility(View.GONE);
            furnishingLinear.setVisibility(View.GONE);

            paymentRow.setVisibility(View.GONE);
            paymentLinear.setVisibility(View.GONE);
            setTypeBuyFilter();
            ct =2;
        } else {
            ct=1;
            setTypeRentFilter();
        }
        if (subCategory != null) {
            if (subCategory.equals("Commercial")) {

                if (category.equals("Buy")) {
                    bedroomsRow.setVisibility(View.GONE);
                    bedroomsLinear.setVisibility(View.GONE);
                    ct =4;
                    setTypeCommercialBuyFilter();


                } else {
                    bedroomsLinear.setVisibility(View.GONE);
                    bedroomsRow.setVisibility(View.GONE);

                    furnishingLinear.setVisibility(View.GONE);
                    furnishingRow.setVisibility(View.GONE);
                    ct = 3;
                    setTypeCommercialRentFilter();

                }

            } else if (subCategory.equals("Residential")) {

                if (category.equals("Buy")) {

                    bedroomsRow.setVisibility(View.VISIBLE);
                    bedroomsLinear.setVisibility(View.VISIBLE);
                    ct = 2;
                    setTypeBuyFilter();
                } else {
                    bedroomsLinear.setVisibility(View.VISIBLE);
                    bedroomsRow.setVisibility(View.VISIBLE);

                    furnishingLinear.setVisibility(View.VISIBLE);
                    furnishingRow.setVisibility(View.VISIBLE);
                    ct = 1;
                    setTypeRentFilter();

                }
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            if (buttonView.getId() == R.id.radioCommerecial) {
                radioResidential.setChecked(false);
                subCategory = "Commercial";
                filtersSet();
            }
            if (buttonView.getId() == R.id.radioResidencial) {
                radioCommercial.setChecked(false);
                subCategory = "Residential";
                filtersSet();
            }
        }

    }

    //--------------------FURNISHING FILTER CODE START--------------------
    public void setfurnishingFilter() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/furnishings";
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
    private void getJsonData(String response) {
        furnishingRent = new ArrayList<>();

        try {
            arrayJsonFurnishing = new JSONArray(response);
            List<String> list = new ArrayList<>();
            list.add("All furnishing");

            for (int j = 0; j < arrayJsonFurnishing.length(); j++) {
                FiltersFurnishing filtersFurnishing = new FiltersFurnishing();
                filtersFurnishing.setId(arrayJsonFurnishing.getJSONObject(j).getString("id"));
                filtersFurnishing.setName(arrayJsonFurnishing.getJSONObject(j).getString("furnishings"));
                list.add(arrayJsonFurnishing.getJSONObject(j).getString("furnishings"));
                furnishingRent.add(filtersFurnishing);

            }
            furnishings = new String[list.size()];
            furnishings = list.toArray(furnishings);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFurnishingAdapter();
                        furnishingSPinner.setSelection(fu);
                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setFurnishingAdapter() {
        final ArrayAdapter<String> furnishingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, furnishings);
        furnishingSPinner.setAdapter(furnishingAdapter);
    }
    //--------------------FURNISHING FILTER CODE END--------------------

    //--------------------PAYMENT FILTER CODE START--------------------
    public void setPaymentFilter() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/rent-types";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResultPayment = response.body().string().toString();
                    getJsonDataforPayment(finalResultPayment);
                }
            }
        });
    }
    private void getJsonDataforPayment(String response) {
        paymentRent = new ArrayList<>();
        try {
            arrayJsonPayment = new JSONArray(response);
            List<String> list = new ArrayList<>();

            for (int j = 0; j < arrayJsonPayment.length(); j++) {
                FiltersPayment filtersPayment = new FiltersPayment();
                filtersPayment.setId(arrayJsonPayment.getJSONObject(j).getString("id"));
                filtersPayment.setName(arrayJsonPayment.getJSONObject(j).getString("type"));
                list.add(arrayJsonPayment.getJSONObject(j).getString("type"));
                paymentRent.add(filtersPayment);

            }

            payments = new String[list.size()];
            payments = list.toArray(payments);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setPaymentAdapter();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setPaymentAdapter() {
        final ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, payments);
        paymentSpinner.setAdapter(paymentAdapter);

    }
    //--------------------PAYMENT FILTER CODE END--------------------


    //--------------------TYPE RENT FILTER CODE START--------------------
    public void setTypeRentFilter() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/categories/1";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResultTypeRent = response.body().string().toString();
                    getJsonDataforTypeRent(finalResultTypeRent);
                }
            }
        });
    }
    private void getJsonDataforTypeRent(String response) {
        typeRent = new ArrayList<>();
        try {
            arrayJsonTypeRent = new JSONArray(response);
            List<String> list = new ArrayList<>();
            list.add("All");

            for (int j = 0; j < arrayJsonTypeRent.length(); j++) {
                FiltersTypes filtersFurnishing = new FiltersTypes();
                filtersFurnishing.setId(arrayJsonTypeRent.getJSONObject(j).getString("id"));
                filtersFurnishing.setName(arrayJsonTypeRent.getJSONObject(j).getString("category"));
                list.add(arrayJsonTypeRent.getJSONObject(j).getString("category"));
                typeRent.add(filtersFurnishing);

            }

            typesRent = new String[list.size()];
            typesRent = list.toArray(typesRent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTypeRentAdapter();
                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setTypeRentAdapter() {
        final ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, typesRent);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        if(typesRent !=null) {
            typeSpinner.setAdapter(typeAdapter);
        }else{
            Toast.makeText(getApplicationContext(),"yhi masla",Toast.LENGTH_LONG).show();
        }

    }
    //--------------------TYPE RENT FILTER CODE END--------------------


    //--------------------TYPE BUY FILTER CODE START--------------------
    public void setTypeBuyFilter() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/categories/2";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResultTypeBuy = response.body().string().toString();
                    getJsonDataforTypeBuy(finalResultTypeBuy);
                }
            }
        });
    }
    private void getJsonDataforTypeBuy(String response) {
        typeBuy = new ArrayList<>();
        try {
            arrayJsonTypeBuy = new JSONArray(response);
            List<String> list = new ArrayList<>();
            list.add("All");

            for (int j = 0; j < arrayJsonTypeBuy.length(); j++) {
                FiltersTypes filtersFurnishing = new FiltersTypes();
                filtersFurnishing.setId(arrayJsonTypeBuy.getJSONObject(j).getString("id"));
                filtersFurnishing.setName(arrayJsonTypeBuy.getJSONObject(j).getString("category"));
                list.add(arrayJsonTypeBuy.getJSONObject(j).getString("category"));
                typeBuy.add(filtersFurnishing);

            }

            typesBuy = new String[list.size()];
            typesBuy = list.toArray(typesBuy);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTypeAdapter();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setTypeAdapter() {
        final ArrayAdapter<String> typeAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, typesBuy);
        typeAdapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        if(typesBuy != null) {
            typeSpinner.setAdapter(typeAdapter2);
        }else{
            Toast.makeText(getApplicationContext(),"yhi masla",Toast.LENGTH_LONG).show();
        }


    }
    //--------------------TYPE BUY FILTER CODE END--------------------


    //--------------------TYPE COMMERCIAL RENT FILTER CODE START--------------------
    public void setTypeCommercialRentFilter() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/categories/3";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResultTypeCommercialRent = response.body().string().toString();
                    getJsonDataforTypeCommercialRent(finalResultTypeCommercialRent);
                }
            }
        });
    }
    private void getJsonDataforTypeCommercialRent(String response) {
        typeCommRent = new ArrayList<>();
        try {
            arrayJsonTypeCommRent = new JSONArray(response);
            List<String> list = new ArrayList<>();
            list.add("All");

            for (int j = 0; j < arrayJsonTypeCommRent.length(); j++) {
                FiltersTypes filtersFurnishing = new FiltersTypes();
                filtersFurnishing.setId(arrayJsonTypeCommRent.getJSONObject(j).getString("id"));
                filtersFurnishing.setName(arrayJsonTypeCommRent.getJSONObject(j).getString("category"));
                list.add(arrayJsonTypeCommRent.getJSONObject(j).getString("category"));
                typeCommRent.add(filtersFurnishing);

            }

            typesCommRent = new String[list.size()];
            typesCommRent = list.toArray(typesCommRent);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTypeCommercialRentAdapter();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setTypeCommercialRentAdapter() {
        final ArrayAdapter<String> typeAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, typesCommRent);

        typeAdapter3.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        if(typesCommRent != null) {
            typeSpinner.setAdapter(typeAdapter3);
        }else{
            Toast.makeText(getApplicationContext(),"yhi masla",Toast.LENGTH_LONG).show();
        }

    }
    //--------------------TYPE COMMERCIAL RENT FILTER CODE END--------------------


    //--------------------TYPE COMMERCIAL BUY FILTER CODE START--------------------
    public void setTypeCommercialBuyFilter() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/categories/4";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    finalResultTypeCommercialBuy = response.body().string().toString();
                    getJsonDataforTypeCommercialBuy(finalResultTypeCommercialBuy);
                }
            }
        });
    }
    private void getJsonDataforTypeCommercialBuy(String response) {
        typeCommBuy = new ArrayList<>();
        try {
            arrayJsonTypeCommBuy = new JSONArray(response);
            List<String> list = new ArrayList<>();
            list.add("All");

            for (int j = 0; j < arrayJsonTypeCommBuy.length(); j++) {
                FiltersTypes filtersFurnishing = new FiltersTypes();
                filtersFurnishing.setId(arrayJsonTypeCommBuy.getJSONObject(j).getString("id"));
                filtersFurnishing.setName(arrayJsonTypeCommBuy.getJSONObject(j).getString("category"));
                list.add(arrayJsonTypeCommBuy.getJSONObject(j).getString("category"));
                typeCommBuy.add(filtersFurnishing);

            }

            typesCommBuy = new String[list.size()];
            typesCommBuy = list.toArray(typesCommBuy);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTypeCommercialBuyAdapter();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void setTypeCommercialBuyAdapter() {
        final ArrayAdapter<String> typeAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, typesCommBuy);

        typeAdapter3.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        if(typesCommBuy != null) {
            typeSpinner.setAdapter(typeAdapter3);
        }else{
            Toast.makeText(getApplicationContext(),"yhi masla",Toast.LENGTH_LONG).show();
        }

    }
    //--------------------TYPE COMMERCIAL BUY FILTER CODE END--------------------

    //--------------------MAX AREA ADAPTER SET, ARRAY IS HARD CODED IN res/values/strings--------------------
    public void setMaxAreaAdapter(){
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,areaTo);
        maxAreaSpinner.setAdapter(areaAdapter);
    }

    //--------------------MINIMUM AREA ADAPTER SET, ARRAY IS HARD CODED IN res/values/strings--------------------
    public void setMinAreaAdapter(){
        final ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,areaFrom);
        minAreaSpinner.setAdapter(areaAdapter);
    }

    //--------------------MINIMUM PRICE ADAPTER SET, ARRAY IS HARD CODED IN res/values/strings--------------------
    public void setMinPriceAdapter(){
        final ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,minPrice);
        minPriceSpinner.setAdapter(priceAdapter);
    }

    //--------------------MAX PRICE ADAPTER SET, ARRAY IS HARD CODED IN res/values/strings--------------------
    public void setMaxPriceAdapter(){
        final ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,maxPrice);
        maxPriceSpinner.setAdapter(priceAdapter);
    }

    //--------------------MINIMUM BEDROOMS ADAPTER SET, ARRAY IS HARD CODED IN res/values/strings--------------------
    public void setMinBedroomsAdapter(){
        final ArrayAdapter<String> bedroomsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,minBedrooms);
        minBedroomSpinner.setAdapter(bedroomsAdapter);
    }

    //--------------------MAX BEDROOMS ADAPTER SET, ARRAY IS HARD CODED IN res/values/strings--------------------
    public void setMaxBedroomsAdapter(){
        final ArrayAdapter<String> bedRoomsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,maxBedrooms);
        maxBedroomSpinner.setAdapter(bedRoomsAdapter);
    }
}

