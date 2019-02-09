package com.example.mapapp_vaidehiyash;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = (TextView) findViewById(R.id.t);

        final Button locateButton = findViewById(R.id.enter_button);

        locateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendLocation(v);
            }
        });
        final Button weatherButton = findViewById(R.id.weather_button);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getWeather(v);
            }
        });
    }

    public void sendLocation(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        EditText editText = findViewById(R.id.loc_query);
        String location = editText.getText().toString();

        intent.putExtra("address", location);
        List<Address> addressList = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.size() == 0) {
            display.setText(" Not a valid address");
        } else {
            startActivity(intent);
        }
    }

    public void getWeather(View view){
        EditText locationText = findViewById(R.id.loc_query);
        String location = locationText.getText().toString();
        List<Address> addressList = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList.size() == 0) {
            display.setText(" Not a valid address");
        } else {
            Address res = addressList.get(0);
            new JSONTask().execute("https://api.darksky.net/forecast/b6da387c37e2586c221d43a4a7ecdc0b/"+res.getLatitude()
                    +','+res.getLongitude());
        }

    }

    public static class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            //Get request
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            JSONObject dataObj = null;
            try {
                dataObj = new JSONObject(result);
                JSONObject curr = dataObj.getJSONObject("current");
                String temperature = curr.getString("temp");
                String humidity = curr.getString("humidity");
                String precipitation = curr.getString("precipitation");
                String windSpeed = curr.getString("windSpeed");

                display.setText(" Temperature: " + temperature + "\n" + " Humidity:  " + humidity + "\n"
                + " Precipitation: " + precipitation + "%" + "\n"
                + " Wind Speed: " + windSpeed  + " mph");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
