package com.mabsapps.propertydodo;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Type;


public class BuyPropertyDetail_Fragment extends Fragment {
    Button btnBack,btnHeart,btnShare,btnCall,btnSms,btnEmail;
    Typeface font;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(),"Detail Fragment",Toast.LENGTH_LONG).show();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_buy_property_detail, container, false);
        btnBack = (Button)v.findViewById(R.id.btnBack);
        btnHeart = (Button)v.findViewById(R.id.btnHeart);
        btnShare = (Button)v.findViewById(R.id.btnShare);
        btnSms = (Button)v.findViewById(R.id.btnSms);
        btnEmail = (Button)v.findViewById(R.id.btnEmail);
        btnCall = (Button)v.findViewById(R.id.btnCall);

        font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fontawesome-webfont.ttf");

        btnBack.setTypeface(font);
        btnHeart.setTypeface(font);
        btnShare.setTypeface(font);
        btnSms.setTypeface(font);
        btnCall.setTypeface(font);
        btnEmail.setTypeface(font);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clicked",Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }
}
