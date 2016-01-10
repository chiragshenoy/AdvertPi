package com.example.chirag.under25;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.glomadrian.loadingballs.BallView;
import com.squareup.picasso.Picasso;

import net.grobas.view.MovingImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

public class MainActivity extends AppCompatActivity {


    RecyclerView mRecyclerView;
    ArrayList<Item> itemArrayList;
    TextView tag;
    String sTag;
    MyAdapter mAdapter;
    String imageURL = "";
    ImageView header_image;
    MovingImageView movingImageView;
    BallView ballView;
    private int mShortAnimationDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ballView = (BallView) findViewById(R.id.loader);

        movingImageView = (MovingImageView) findViewById(R.id.miv);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        itemArrayList = new ArrayList<Item>();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setMinimumHeight(800);

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_longAnimTime);

        String ssid = "Test";
        String key = "0000000000";

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new con().execute();
//                ballView.setVisibility(View.GONE);
//
                crossfade();

            }
        }, 3000);

        mAdapter = new MyAdapter(getApplication(), itemArrayList);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void crossfade() {

        mRecyclerView.setAlpha(0f);
        mRecyclerView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mRecyclerView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        ballView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ballView.setVisibility(View.GONE);
                    }
                });

    }


    public class con extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mAdapter.notifyDataSetChanged();
            Picasso.with(getApplicationContext())
                    .load(imageURL)
                    .resize(500, 150)
                    .into(movingImageView);

        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;

            String serverResponse = null;
            try {

                HttpGet get = new HttpGet("http://abcd.com/");


                response = client.execute(get);
//*
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    serverResponse = convertStreamToString(in);
                    Log.d("serverResponse", serverResponse);


                    JSONObject jObj = new JSONObject(serverResponse);
                    sTag = jObj.getString("name");
                    imageURL = jObj.getString("logo");
                    Log.d("name", sTag);
                    JSONArray arr = jObj.getJSONArray("array");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject arJ = arr.getJSONObject(i);
//                        String im = arJ.getString("image");
//                        String head = arJ.getString("heading");
//                        String desc = arJ.getString("description");
//                        String oldPrice = arJ.getString("oldprice");
//                        String newPrice = arJ.getString("newprice");
                        Item item = new Item();

                        item.setDescription(arJ.getString("description"));
                        item.setHeader(arJ.getString("heading"));
                        item.setNew_price(arJ.getString("newprice"));
                        item.setOld_price(arJ.getString("oldprice"));
                        item.setImage(arJ.getString("image"));
                        itemArrayList.add(item);

//                        Log.d("ARRAY ", "" + im + "\n" + head + "\n" + desc);
                    }
                    JSONObject jo = jObj.getJSONObject("trending");

                    String image = jo.getString("image");
                    String description = jo.getString("description");
                    String price = jo.getString("price");
                    String discount = jo.getString("discount");

                    Log.d("TRENDING ", "" + image + "\n" + description + "\n" + price + "\n" + discount);


                }
            } catch (ClientProtocolException | UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return serverResponse;

        }

        private String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append((line + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

}