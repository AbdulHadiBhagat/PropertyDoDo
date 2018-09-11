package com.mabsapps.propertydodo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BuyPropertyDetail_Fragment extends Fragment {
    Button btnBack, btnHeart, btnShare, btnCall, btnSms, btnEmail, btnViewOnMap,btnViewBrokerProp;
    FrameLayout frameLayoutDetails;
    Typeface font;
    ViewPager slider;
    String propertyId, finalResult, price, headline, beds, baths, subtitle, type, reference, permit, area, description,
            onMapLat, onMapLong,agentName,agentCompany,agentImage,companyImage,companyID,path;
    String[] imagesUrl;
    ViewPagerAdapter viewPagerAdapter;
    JSONArray array;
    JSONObject properties;
    TextView txtPrice, txtHeadline, txtSubtitle, txtPriceValue, txtTypeValue, txtReferenceValue, txtPermitValue,
            txtBathroomValue, txtBedroomValue, txtAreaValue, txtDescriptionValue,txtAgentName,txtAgentCompany;
    ImageView imageAgent,imageCompany;
    private Fragment fragment;

    View v;
    View vBed, vBath;
    TableRow row7, row6;

    String url = "https://admin.propertydodo.ae/api/properties/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_buy_property_detail, container, false);

        Bundle bundle = getArguments();

        propertyId = bundle.getString("id");
        path = bundle.getString("path");
        url += propertyId;
        btnBack = (Button) v.findViewById(R.id.btnBack);
        btnHeart = (Button) v.findViewById(R.id.btnHeart);
        btnShare = (Button) v.findViewById(R.id.btnShare);
        btnSms = (Button) v.findViewById(R.id.btnSms);
        btnEmail = (Button) v.findViewById(R.id.btnEmail);
        btnCall = (Button) v.findViewById(R.id.btnCall);
        btnViewOnMap = (Button) v.findViewById(R.id.btnViewOnMap);
        btnViewBrokerProp = (Button) v.findViewById(R.id.btnViewBrokerProp);
        txtPrice = (TextView) v.findViewById(R.id.txtPrice);
        txtPriceValue = (TextView) v.findViewById(R.id.txtSinglePropPriceValue);
        txtTypeValue = (TextView) v.findViewById(R.id.txtSinglePropTypeValue);
        txtReferenceValue = (TextView) v.findViewById(R.id.txtSinglePropReferenceValue);
        txtPermitValue = (TextView) v.findViewById(R.id.txtSinglePropPermitValue);
        txtBathroomValue = (TextView) v.findViewById(R.id.txtSinglePropBathroomsValue);
        txtBedroomValue = (TextView) v.findViewById(R.id.txtSinglePropBedroomsValue);
        txtAreaValue = (TextView) v.findViewById(R.id.txtSinglePropAreaValue);
        txtDescriptionValue = (TextView) v.findViewById(R.id.txtSinglePropDescriptionValue);
        txtHeadline = (TextView) v.findViewById(R.id.txtHeadline);
        txtSubtitle = (TextView) v.findViewById(R.id.txtSubtitle);
        txtAgentName = (TextView) v.findViewById(R.id.txtAgentName);
        txtAgentCompany = (TextView) v.findViewById(R.id.txtAgentCompany);
        imageAgent = (ImageView) v.findViewById(R.id.imageAgent);
        imageCompany = (ImageView) v.findViewById(R.id.imageCompany);
        vBath = (View) v.findViewById(R.id.viewBath);
        vBed = (View) v.findViewById(R.id.viewBed);

        frameLayoutDetails = (FrameLayout) getActivity().findViewById(R.id.fragment_container_details);
        slider = (ViewPager) v.findViewById(R.id.slider);
        row7 = (TableRow) v.findViewById(R.id.row7);
        row6 = (TableRow) v.findViewById(R.id.row6);

        font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome-webfont.ttf");

        btnBack.setTypeface(font);
        btnHeart.setTypeface(font);
        btnShare.setTypeface(font);
        btnSms.setTypeface(font);
        btnCall.setTypeface(font);
        btnEmail.setTypeface(font);
        setProperties();
        viewPagerAdapter = new ViewPagerAdapter(getContext());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayoutDetails.setVisibility(View.GONE);
            }
        });
        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("lat", onMapLat);
                intent.putExtra("long", onMapLong);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });

        btnViewBrokerProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (path.equals("map")) {

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("detailsPath","map");
                    intent.putExtra("companyId",companyID);
                    getActivity().startActivity(intent);

                } else {
                    Bundle args = new Bundle();
                    args.putString("companyId", companyID);
                    frameLayoutDetails.setVisibility(View.GONE);
                    fragment = new Buy_ListView_Fragment();
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragment_container, fragment).commit();


                }
            }
        });

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK){


                    return true;
                }
                return false;
            }
        });
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

    public void getJsonData(String response) {
        try {
            array = new JSONObject(response).getJSONObject("properties").getJSONArray("images");
            properties = new JSONObject(response).getJSONObject("properties").getJSONObject("property");
            imagesUrl = new String[array.length()];

            price = properties.getString("price");
            headline = properties.getString("category");
            type = properties.getString("category");
            beds = properties.getString("bedrooms");
            baths = properties.getString("bathrooms");
            subtitle = properties.getString("subtitle");
            reference = properties.getString("ref_number");
            permit = properties.getString("permit_number");
            area = properties.getString("area");
            description = properties.getString("description");
            onMapLat = properties.getString("Lat");
            onMapLong = properties.getString("Long");
            agentName = properties.getString("agent");
            agentCompany = properties.getString("company");
            agentImage = properties.getString("agent_image");
            companyImage = properties.getString("company_logo");
            companyID = properties.getString("company_id");

            if (beds != null && beds != "null" && beds != "0") {
                if (Integer.parseInt(beds) == 1) {
                    headline += ", " + beds + " bed, ";
                } else if (Integer.parseInt(beds) > 1) {
                    headline += ", " + beds + " beds, ";
                }
            }
            if (beds.equals("null") || beds.equals(null) || beds.equals("0")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        row6.setVisibility(View.GONE);
                        vBed.setVisibility(View.GONE);
                    }
                });

            }

            if (baths != null && baths != "null" && baths != "0") {
                if (Integer.parseInt(baths) == 1) {
                    headline += baths + " bath";
                } else if (Integer.parseInt(beds) > 1) {
                    headline += baths + " baths";
                }
            }
            if (baths.equals("null") || baths.equals(null) || baths.equals("0")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        row7.setVisibility(View.GONE);
                        vBath.setVisibility(View.GONE);
                    }
                });

            }
            if (subtitle.equals("null") || subtitle.equals(null) || subtitle.equals("0")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtSubtitle.setVisibility(View.GONE);
                    }
                });
            }

            for (int i = 0; i < array.length(); i++) {
                imagesUrl[i] = "https://propertydodoimages.s3.amazonaws.com/" + array.getJSONObject(i).getString("image");
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    slider.setAdapter(viewPagerAdapter);
                    txtPrice.setText(price + " AED");
                    txtHeadline.setText(headline);
                    if (txtSubtitle.getVisibility() == View.VISIBLE) {
                        txtSubtitle.setText(subtitle);
                    }
                    txtPriceValue.setText(price);
                    txtTypeValue.setText(type);
                    txtReferenceValue.setText(reference);
                    txtPermitValue.setText(permit);
                    if (row6.getVisibility() == View.VISIBLE) {
                        txtBedroomValue.setText(beds);
                    }
                    if (row7.getVisibility() == View.VISIBLE) {
                        txtBathroomValue.setText(baths);
                    }
                    txtAreaValue.setText(area + " sqft");
                    txtDescriptionValue.setText(description);
                    txtAgentName.setText(agentName);
                    txtAgentCompany.setText(agentCompany);
                    try {
                        Picasso.with(v.getContext()).load(agentImage).fit().into(imageAgent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Picasso.with(v.getContext()).load(companyImage).fit().into(imageCompany);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return array.length();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.custom_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);

            try {
                Picasso.with(v.getContext()).load(imagesUrl[position]).fit().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }
    }

}


