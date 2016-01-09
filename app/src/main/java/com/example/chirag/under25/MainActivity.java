package com.example.chirag.under25;

import android.app.DownloadManager;
import android.net.http.RequestQueue;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    JSONObject json;
    HttpClient client;
    //       String URL = "http://10.10.0.1:80/a.php";
//    String URL = "https://api.myjson.com/bins/1uln5";
//    String URL = "http://115.242.173.133:8080/inheritance.php";
    String URL = "http://abcd.com";
    String ssid;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);

        ssid = "Test";
        key = "0000000000";

        client = new DefaultHttpClient();


        initWifi();


    }

    private void initWifi() {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                new Read().execute();
            }
        }, 3000);

    }


    public class Read extends AsyncTask<String, Integer, String> {
        /**
         * progress dialog to show user that the backup is processing.
         */

        //Only getting the list of the subjects and getting basic info
        @Override
        protected String doInBackground(String... params) {
            try {
                json = get_entire_json();
                String string_basic_info = json.getString("offers");
                Log.e("offers", string_basic_info);
                return null;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }
    }

    public JSONObject get_entire_json() throws IOException, JSONException {

//        StringBuilder url = new StringBuilder(URL + email + PASSWORD);
        StringBuilder url = new StringBuilder(URL);
        HttpGet get = new HttpGet(url.toString());
        HttpResponse r = client.execute(get);

        int status = r.getStatusLine().getStatusCode();

        if (status == 200) {
            HttpEntity e = r.getEntity();
            String data = EntityUtils.toString(e);
            JSONObject full_json = new JSONObject(data);

            return full_json;
        } else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
            return null;

        }
    }


}
