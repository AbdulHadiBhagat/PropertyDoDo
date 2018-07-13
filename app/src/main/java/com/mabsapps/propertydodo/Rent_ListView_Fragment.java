package com.mabsapps.propertydodo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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



public class Rent_ListView_Fragment extends Fragment {

    String TAG="---------MSG----------";
    private String finalResult;
    String [] imagesRent = {""};
    String[] namesRent = {""};
    String[] titleRent = {""};
    String[] bedroomsRent = {""};
    String[] bathroomsRent = {""};
    ListView listView;
    View rootView;
    JSONArray array,properties;
    CustomAdapterforRent customAdapterforRent;

    public Rent_ListView_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_rent_listview, container, false);
        setProperties();
         listView = (ListView) getActivity().findViewById(R.id.ListView);
         customAdapterforRent = new CustomAdapterforRent();
        listView.setAdapter(customAdapterforRent);
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

                if(array.getJSONObject(j).get("contract_type").equals("Rent") || array.getJSONObject(j).get("contract_type").equals("Commercial Rent")){
                    properties.put(array.get(j));
                }
            }
           namesRent = new String[properties.length()];
           titleRent = new String[properties.length()];
           imagesRent = new String[properties.length()];
           bedroomsRent = new String[properties.length()];
           bathroomsRent = new String[properties.length()];
            for(int i=0;i<properties.length();i++){
                namesRent[i]=properties.getJSONObject(i).getString("price");
                titleRent[i]=properties.getJSONObject(i).getString("title");
                imagesRent[i]=properties.getJSONObject(i).getString("image");
                bedroomsRent[i]=properties.getJSONObject(i).getString("bedrooms");
                bathroomsRent[i]=properties.getJSONObject(i).getString("bathrooms");

                if(getActivity()==null){
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        customAdapterforRent.notifyDataSetChanged();
                    }
                });
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class CustomAdapterforRent extends BaseAdapter {

       @Override
        public int getCount() { return namesRent.length; }

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

            convertView = getLayoutInflater().inflate(R.layout.listview_layout, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView txtNames = (TextView) convertView.findViewById(R.id.txtName);
            TextView txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
            TextView txtBedrooms = (TextView)convertView.findViewById(R.id.txtBedRooms);
            TextView txtBathrooms = (TextView)convertView.findViewById(R.id.txtbath);
            try{
                Picasso.with(rootView.getContext()).load(imagesRent[position]).fit().into(imageView);
            }catch (Exception e){
                e.printStackTrace();
            }
            txtBedrooms.setText(bedroomsRent[position]+" AED");
            txtBathrooms.setText(bathroomsRent[position]);
            txtNames.setText(namesRent[position]);
            txtDescription.setText(titleRent[position]);
            return convertView;
        }
    }
}
