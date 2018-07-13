package com.mabsapps.propertydodo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
    String finalResult;
    String TAG="---------MSG----------";
    String [] imagesBuy = {""};
    String[] namesBuy = {""};
    String[] titleBuy = {""};
    String[] bedroomsBuy = {""};
    String[] bathroomsBuy = {""};
    JSONArray array,properties;
    ListView listView;
    CustomAdapterforBuy customAdapterforBuy;
    Fragment fragment;
    View rootView;
    FrameLayout frameLayoutDetails;

    public Buy_ListView_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_buy_list_view_, container, false);
        setProperties();
        listView = (ListView) getActivity().findViewById(R.id.ListView);
        frameLayoutDetails = (FrameLayout)getActivity().findViewById(R.id.fragment_container_details);
        customAdapterforBuy = new CustomAdapterforBuy();
        listView.setAdapter(customAdapterforBuy);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                frameLayoutDetails.setVisibility(View.VISIBLE);
                Fragment fragment2 = new BuyPropertyDetail_Fragment();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container_details, fragment2).commit();


//                Fragment fragment2 = new Fragment();
//                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, fragment2); // give your fragment container id in first parameter
//                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//                transaction.commit();
            }
        });

        return rootView;
    }
    public void setProperties(){
        OkHttpClient client = new OkHttpClient();
        String url = "https://admin.propertydodo.ae/api/properties/all";
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
                }
            }
        });
    }
    public void getJsonData(String response){
        properties = new JSONArray();
        try {
            array = new JSONObject(response).getJSONArray("properties");
            for(int j=0;j<array.length();j++){
                if(array.getJSONObject(j).get("contract_type").equals("Buy") || array.getJSONObject(j).get("contract_type").equals("Commercial Buy")){
                    properties.put(array.get(j));
                }
            }
            namesBuy = new String[properties.length()];
            titleBuy = new String[properties.length()];
            imagesBuy = new String[properties.length()];
            bedroomsBuy= new String[properties.length()];
            bathroomsBuy = new String[properties.length()];
            for(int i=0;i<properties.length();i++){
                namesBuy[i]=properties.getJSONObject(i).getString("price");
                titleBuy[i]=properties.getJSONObject(i).getString("title");
                imagesBuy[i]=properties.getJSONObject(i).getString("image");
                bedroomsBuy[i]=properties.getJSONObject(i).getString("bedrooms");
                bathroomsBuy[i]=properties.getJSONObject(i).getString("bathrooms");

                if(getActivity()==null){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customAdapterforBuy.notifyDataSetChanged();
                    }
                });
            }

        }catch (JSONException e) {
            e.printStackTrace();
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
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.listview_layout,null);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            TextView txtNames = (TextView)convertView.findViewById(R.id.txtName);
            TextView txtDescription = (TextView)convertView.findViewById(R.id.txtDescription);
            TextView txtBedrooms= (TextView)convertView.findViewById(R.id.txtBedRooms);
            TextView txtBathrooms = (TextView)convertView.findViewById(R.id.txtbath);
            try{
                Picasso.with(rootView.getContext()).load(imagesBuy[position]).fit().into(imageView);
            }catch (Exception e){
                e.printStackTrace();
            }
            txtNames.setText(namesBuy[position]+" AED");
            txtBathrooms.setText(bathroomsBuy[position]);
            txtBedrooms.setText(bedroomsBuy[position]);
            txtDescription.setText(titleBuy[position]);
            return convertView;
        }
    }
}
