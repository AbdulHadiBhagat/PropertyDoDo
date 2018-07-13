package com.mabsapps.propertydodo;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class filters extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private Button btnBack;
    private Spinner spinner_type;
    private LinearLayout bedroomsLinear, furnishingLinear, paymentLinear;
    private TableRow bedroomsRow, furnishingRow, paymentRow;
    private RadioButton radioCommercial, radioResidential;
    private String category, subCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        btnBack = (Button) findViewById(R.id.btnBack);

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

        spinner_type = (Spinner) findViewById(R.id.typeSpinner);
        subCategory = null;
        filtersSet();
        btnBack.setTypeface(font);

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
}
