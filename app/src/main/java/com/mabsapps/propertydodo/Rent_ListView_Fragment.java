package com.mabsapps.propertydodo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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



public class Rent_ListView_Fragment extends Fragment {

    String TAG = "---------MSG----------";
    private String finalResult, sort;
    String[] imagesRent = {""};
    String[] namesRent = {""};
    String[] titleRent = {""};
    String[] bedroomsRent = {""};
    String[] bathroomsRent = {""};
    String[] detailsRent = {""};
    String[] idBuy = {""};
    ListView listView;
    View rootView;
    JSONArray array, properties;
    CustomAdapterforRent customAdapterforRent;
    FrameLayout frameLayoutDetails;
    String url = "https://admin.propertydodo.ae/api/properties/all?ct=";

    public Rent_ListView_Fragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_rent_listview, container, false);
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
        url += sort;

        setProperties();

        listView = (ListView) getActivity().findViewById(R.id.ListView);
        frameLayoutDetails = (FrameLayout) getActivity().findViewById(R.id.fragment_container_details);
        customAdapterforRent = new CustomAdapterforRent();

        Toast.makeText(getContext(), "Please Wait.. Loading Properties", Toast.LENGTH_SHORT).show();

        listView.setAdapter(customAdapterforRent);
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
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container_details, fragment2).commit();

            }
        });
        listView.setVisibility(View.GONE);
        return rootView;
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

                if (array.getJSONObject(j).get("contract_type").equals("Rent") || array.getJSONObject(j).get("contract_type").equals("Commercial Rent")) {
                    properties.put(array.get(j));
                }
            }
            namesRent = new String[properties.length()];
            titleRent = new String[properties.length()];
            imagesRent = new String[properties.length()];
            bedroomsRent = new String[properties.length()];
            bathroomsRent = new String[properties.length()];
            detailsRent = new String[properties.length()];
            idBuy = new String[properties.length()];
            for (int i = 0; i < properties.length(); i++) {
                namesRent[i] = properties.getJSONObject(i).getString("price");
                titleRent[i] = properties.getJSONObject(i).getString("category");
                imagesRent[i] = properties.getJSONObject(i).getString("image");
                bedroomsRent[i] = properties.getJSONObject(i).getString("bedrooms");
                bathroomsRent[i] = properties.getJSONObject(i).getString("bathrooms");
                detailsRent[i] = properties.getJSONObject(i).getString("location");
                idBuy[i] = properties.getJSONObject(i).getString("id");

                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customAdapterforRent.notifyDataSetChanged();
                        if (properties.length() > 0) {

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

    class CustomAdapterforRent extends BaseAdapter {

        @Override
        public int getCount() {
            return namesRent.length;
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
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            final ImageView imageBed = (ImageView) convertView.findViewById(R.id.imageBed);
            final ImageView imageBath = (ImageView) convertView.findViewById(R.id.imageBath);
            TextView txtNames = (TextView) convertView.findViewById(R.id.txtName);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            TextView txtBedrooms = (TextView) convertView.findViewById(R.id.txtBedRooms);
            TextView txtBathrooms = (TextView) convertView.findViewById(R.id.txtbath);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            try {
                Picasso.with(rootView.getContext()).load(imagesRent[position]).fit().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (bathroomsRent.length > 0) {
                if (bathroomsRent[position].equals("null") || bathroomsRent[position].equals("0") || bathroomsRent.equals("")) {
                    imageBath.setVisibility(convertView.GONE);
                    txtBathrooms.setVisibility(convertView.GONE);
                } else {
                    imageBath.setVisibility(convertView.VISIBLE);
                    txtBathrooms.setVisibility(convertView.VISIBLE);
                }
            }
            if (bedroomsRent.length > 0) {

                if (bedroomsRent[position].equals("null") || bedroomsRent[position].equals("0") || bedroomsRent.equals("")) {
                    imageBed.setVisibility(convertView.GONE);
                    txtBedrooms.setVisibility(convertView.GONE);
                } else {
                    imageBed.setVisibility(convertView.VISIBLE);
                    txtBedrooms.setVisibility(convertView.VISIBLE);
                }
            }

            txtBedrooms.setText(bedroomsRent[position]);
            txtTitle.setText(titleRent[position]);
            txtBathrooms.setText(bathroomsRent[position]);
            txtNames.setText(namesRent[position] + " AED");
            txtDescription.setText(detailsRent[position]);
            return convertView;
        }
    }
}
