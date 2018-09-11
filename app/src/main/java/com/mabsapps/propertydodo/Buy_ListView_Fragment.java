package com.mabsapps.propertydodo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class Buy_ListView_Fragment extends Fragment {
    String finalResult, sort;
    String TAG = "---------MSG----------";
    String[] imagesBuy = {""};
    String[] namesBuy = {""};
    String[] titleBuy = {""};
    String[] bedroomsBuy = {""};
    String[] bathroomsBuy = {""};
    String[] detailsBuy = {""};
    String[] idBuy = {""};
    JSONArray array, properties;
    ListView listView;
    CustomAdapterforBuy customAdapterforBuy;
    Fragment fragment;
    View rootView;
    FrameLayout frameLayoutDetails;
    String urlProperty = "https://admin.propertydodo.ae/api/properties/all?ct=";
    String companyId = null;
    String urlCompany = "https://admin.propertydodo.ae/api/companies/";
    boolean check = false;

    public Buy_ListView_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_buy_list_view_, container, false);

        Bundle bundle = getArguments();
        companyId = bundle.getString("companyId");
        urlCompany += companyId;
        int ct = getArguments().getInt("ct");
        int rt = getArguments().getInt("rt");
        int c = getArguments().getInt("c");
        int bf = getArguments().getInt("bf");
        int bt = getArguments().getInt("bt");
        int af = getArguments().getInt("af");
        int at = getArguments().getInt("at");
        int pf = getArguments().getInt("pf");
        int pt = getArguments().getInt("pt");
        int fu = getArguments().getInt("fu");
        int l = getArguments().getInt("l");
        sort = getArguments().getString("sort");

        urlProperty += ct;
        urlProperty += (rt != 0) ? "&rt=" + rt : "";
        urlProperty += (c != 0) ? "&c=" + c : "";
        urlProperty += (bf != 0) ? "&bf=" + bf : "";
        urlProperty += (bt != 0) ? "&bt=" + bt : "";
        urlProperty += (af != 0) ? "&af=" + af : "";
        urlProperty += (at != 0) ? "&at=" + at : "";
        urlProperty += (pf != 0) ? "&pf=" + pf : "";
        urlProperty += (pt != 0) ? "&pt=" + pt : "";
        urlProperty += (fu != 0) ? "&fu=" + fu : "";
        urlProperty += (l != 0) ? "&l=" + l : "";
        urlProperty += sort;
        setProperties();

        listView = (ListView) getActivity().findViewById(R.id.ListView);
        frameLayoutDetails = (FrameLayout) getActivity().findViewById(R.id.fragment_container_details);
        customAdapterforBuy = new CustomAdapterforBuy();

        Toast.makeText(getContext(), "Loading Properties ...Please Wait", Toast.LENGTH_SHORT).show();

        listView.setAdapter(customAdapterforBuy);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String specificId = idBuy[position];
                frameLayoutDetails.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putString("id", specificId);
                bundle.putString("path", "list");
                Fragment fragment2 = new BuyPropertyDetail_Fragment();
                fragment2.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragment_container_details, fragment2).commit();
            }
        });
        listView.setVisibility(View.GONE);
        return rootView;
    }

    public void setProperties() {
        OkHttpClient client = new OkHttpClient();
        String url;

        if (companyId != null) {
            check = true;

            url = urlCompany;
        } else {
            check = false;

            url = urlProperty;
        }


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
                    if (check == false) {
                        getJsonData(finalResult);

                    } else {
                        getJsonDataCompany(finalResult);

                    }
                }
            }
        });
    }


    public void getJsonData(String response) {
        properties = new JSONArray();
        try {
            array = new JSONObject(response).getJSONArray("properties");
            if (array.length() == 0) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Properties not found", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            for (int j = 0; j < array.length(); j++) {
                if (array.getJSONObject(j).get("contract_type").equals("Buy") || array.getJSONObject(j).get("contract_type").equals("Commercial Buy")) {
                    properties.put(array.get(j));
                }
            }
            namesBuy = new String[properties.length()];
            titleBuy = new String[properties.length()];
            imagesBuy = new String[properties.length()];
            bedroomsBuy = new String[properties.length()];
            bathroomsBuy = new String[properties.length()];
            detailsBuy = new String[properties.length()];
            idBuy = new String[properties.length()];
            for (int i = 0; i < properties.length(); i++) {
                namesBuy[i] = properties.getJSONObject(i).getString("price");
                titleBuy[i] = properties.getJSONObject(i).getString("category");
                imagesBuy[i] = properties.getJSONObject(i).getString("image");
                bedroomsBuy[i] = properties.getJSONObject(i).getString("bedrooms");
                bathroomsBuy[i] = properties.getJSONObject(i).getString("bathrooms");
                detailsBuy[i] = properties.getJSONObject(i).getString("location");
                idBuy[i] = properties.getJSONObject(i).getString("id");

                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customAdapterforBuy.notifyDataSetChanged();
                        if (properties != null) {

                            listView.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJsonDataCompany(String response) {
        properties = new JSONArray();
        try {
            array = new JSONObject(response).getJSONObject("companies").getJSONArray("properties");

            for (int j = 0; j < array.length(); j++) {
                properties.put(array.get(j));
            }

            namesBuy = new String[properties.length()];
            titleBuy = new String[properties.length()];
            imagesBuy = new String[properties.length()];
            bedroomsBuy = new String[properties.length()];
            bathroomsBuy = new String[properties.length()];
            detailsBuy = new String[properties.length()];
            idBuy = new String[properties.length()];
            for (int i = 0; i < properties.length(); i++) {
                namesBuy[i] = properties.getJSONObject(i).getString("price");
                titleBuy[i] = properties.getJSONObject(i).getString("category");
                imagesBuy[i] = properties.getJSONObject(i).getString("image");
                bedroomsBuy[i] = properties.getJSONObject(i).getString("bedrooms");
                bathroomsBuy[i] = properties.getJSONObject(i).getString("bathrooms");
                detailsBuy[i] = properties.getJSONObject(i).getString("location");
                idBuy[i] = properties.getJSONObject(i).getString("id");

                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customAdapterforBuy.notifyDataSetChanged();
                        if (properties != null) {

                            listView.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        if (listView.getVisibility() == View.VISIBLE) {

            listView.setVisibility(View.GONE);
        }
    }

    class CustomAdapterforBuy extends BaseAdapter {

        @Override
        public int getCount() {
            return namesBuy.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            final ImageView imageBed = (ImageView) convertView.findViewById(R.id.imageBed);
            final ImageView imageBath = (ImageView) convertView.findViewById(R.id.imageBath);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView txtNames = (TextView) convertView.findViewById(R.id.txtName);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            TextView txtBedrooms = (TextView) convertView.findViewById(R.id.txtBedRooms);
            TextView txtBathrooms = (TextView) convertView.findViewById(R.id.txtbath);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

            try {
                Picasso.with(rootView.getContext()).load(imagesBuy[position]).fit().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bathroomsBuy.length > 0 && bathroomsBuy != null) {
                if (bathroomsBuy[position].equals("null") || bathroomsBuy[position].equals("0") || bathroomsBuy.equals("")) {
                    imageBath.setVisibility(convertView.GONE);
                    txtBathrooms.setVisibility(convertView.GONE);
                } else {
                    imageBath.setVisibility(convertView.VISIBLE);
                    txtBathrooms.setVisibility(convertView.VISIBLE);
                }
            }

            if (bedroomsBuy.length > 0 & bedroomsBuy != null) {
                if (bedroomsBuy[position].equals("null") || bedroomsBuy[position].equals("0") || bedroomsBuy.equals("")) {
                    imageBed.setVisibility(convertView.GONE);
                    txtBedrooms.setVisibility(convertView.GONE);
                } else {
                    imageBed.setVisibility(convertView.VISIBLE);
                    txtBedrooms.setVisibility(convertView.VISIBLE);
                }
            }

            txtTitle.setText(titleBuy[position]);
            txtNames.setText(namesBuy[position] + " AED");
            txtBathrooms.setText(bathroomsBuy[position]);
            txtBedrooms.setText(bedroomsBuy[position]);
            txtDescription.setText(detailsBuy[position]);
            return convertView;
        }
    }
}
