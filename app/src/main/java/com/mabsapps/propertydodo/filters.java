package com.mabsapps.propertydodo;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.LocaleDisplayNames;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

public class filtersOLD extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private Button btnBack;
    private String finalResultFurnishing, finalResultType;

    private Spinner typeSpinner, minAreaSpinner, maxAreaSpinner, minBedroomSpinner, maxBedroomSpinner, furnishingSPinner,
            paymentSpinner, minPriceSpinner, maxPriceSpinner;
    private LinearLayout bedroomsLinear, furnishingLinear, paymentLinear;
    private TableRow bedroomsRow, furnishingRow, paymentRow;
    private RadioButton radioCommercial, radioResidential;
    private String category, subCategory = null;

    private JSONArray array;
    ArrayList<FiltersFurnishing> furnishing;
    ArrayList<FiltersType> type;
    String[] furnishings, types;

    ArrayAdapter<String> furnishingAdapter, TypeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        btnBack = (Button) findViewById(R.id.btnBack);

        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        minAreaSpinner = (Spinner) findViewById(R.id.minAreaSpinner);
        maxAreaSpinner = (Spinner) findViewById(R.id.maxAreaSpinner);
        minBedroomSpinner = (Spinner) findViewById(R.id.minBedroomSpinner);
        maxBedroomSpinner = (Spinner) findViewById(R.id.maxBedroomsSpinner);
        furnishingSPinner = (Spinner) findViewById(R.id.furnishingSpinner);
        paymentSpinner = (Spinner) findViewById(R.id.paymentSpinner);
        minPriceSpinner = (Spinner) findViewById(R.id.minPriceSpinner);
        maxPriceSpinner = (Spinner) findViewById(R.id.maxPriceSpinner);


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


        subCategory = null;
        filtersSet();
        btnBack.setTypeface(font);
       // setfurnishingFilter();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    public void filtersSet() {
        if (category.equals("Buy")) {
            Toast.makeText(getApplicationContext(), "In BUY", Toast.LENGTH_SHORT).show();
            furnishingRow.setVisibility(View.GONE);
            furnishingLinear.setVisibility(View.GONE);

            paymentRow.setVisibility(View.GONE);
            paymentLinear.setVisibility(View.GONE);
        } else {
         //   setTypeFilter();
        }
        if (subCategory != null) {
            if (subCategory.equals("Commercial")) {
                Toast.makeText(getApplicationContext(), "Commercial", Toast.LENGTH_SHORT).show();
                if (category.equals("Buy")) {
                    bedroomsRow.setVisibility(View.GONE);
                    bedroomsLinear.setVisibility(View.GONE);
                } else {
                    bedroomsLinear.setVisibility(View.GONE);
                    bedroomsRow.setVisibility(View.GONE);

                    furnishingLinear.setVisibility(View.GONE);
                    furnishingRow.setVisibility(View.GONE);
                }

            } else if (subCategory.equals("Residential")) {
                Toast.makeText(getApplicationContext(), "Residential", Toast.LENGTH_SHORT).show();
                if (category.equals("Buy")) {

                    bedroomsRow.setVisibility(View.VISIBLE);
                    bedroomsLinear.setVisibility(View.VISIBLE);

                } else {
                    bedroomsLinear.setVisibility(View.VISIBLE);
                    bedroomsRow.setVisibility(View.VISIBLE);

                    furnishingLinear.setVisibility(View.VISIBLE);
                    furnishingRow.setVisibility(View.VISIBLE);
                  //  setTypeFilter();
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

    //------------------------------------FURNISHING FILTER CODE START------------------------------------

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
                    finalResultFurnishing = response.body().string().toString();
                    getJsonDataforFurnishing(finalResultFurnishing);
                }
            }
        });
    }

    private void getJsonDataforFurnishing(String response) {
        furnishing = new ArrayList<>();
        try {
            array = new JSONArray(response);
            List<String> list = new ArrayList<>();

            for (int j = 0; j < array.length(); j++) {
                FiltersFurnishing filtersFurnishing = new FiltersFurnishing();
                filtersFurnishing.setId(array.getJSONObject(j).getString("id"));
                filtersFurnishing.setName(array.getJSONObject(j).getString("furnishings"));
                list.add(array.getJSONObject(j).getString("furnishings"));
                furnishing.add(filtersFurnishing);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setFurnishingAdapter();
                    }
                });
            }
            Log.d("MSG","before setting items"+list);
            furnishings = new String[list.size()];
            furnishings = list.toArray(furnishings);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFurnishingAdapter() {
        furnishingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, furnishings);
        furnishingSPinner.setAdapter(furnishingAdapter);
    }
    //------------------------------------FURNISHING FILTER CODE END------------------------------------


    //------------------------------------TYPE FILTER CODE START------------------------------------
//    public void setTypeFilter() {
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://admin.propertydodo.ae/api/properties/categories/1";
//        Request request = new Request.Builder().url(url).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    finalResultType = response.body().string().toString();
//                    getJsonDataforType(finalResultType);
//                }
//            }
//        });
//    }
//    List<String> list;
//    private void getJsonDataforType(String response) {
//        type = new ArrayList<>();
//        try {
//            array = new JSONArray(response);
//             list = new ArrayList<>();
//
//            for (int j = 0; j < array.length(); j++) {
//                FiltersType filterType = new FiltersType();
//                filterType.setId(array.getJSONObject(j).getString("id"));
//                filterType.setName(array.getJSONObject(j).getString("category"));
//                list.add(array.getJSONObject(j).getString("category"));
//                type.add(filterType);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setTypeAdapter();
//                        types = new String[list.size()];
//                        types = list.toArray(types);
//                    }
//                });
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setTypeAdapter() {
//        TypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
//        typeSpinner.setAdapter(TypeAdapter);
//    }

    //------------------------------------TYPE FILTER CODE END------------------------------------
}

