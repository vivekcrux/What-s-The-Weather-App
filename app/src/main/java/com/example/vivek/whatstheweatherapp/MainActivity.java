package com.example.vivek.whatstheweatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    TextView mainReport;
    EditText cityEditText;

    public void getTheWeather(View view){

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null) {
            mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);
        }


        String city = cityEditText.getText().toString();

        String api = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=efa65fc7710d33a4ae42fff909c602d7";

        DownloadTask task = new DownloadTask();

        try {

            task.execute(api);

        } catch (Exception e){
            e.getStackTrace();
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1){

                   result += (char)data;
                   data = reader.read();

                }



            } catch (Exception e){
                e.getStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {

                JSONObject jsonObject = new JSONObject(result);
                String weather = jsonObject.getString("weather");
                String temp = jsonObject.getString("main");
                JSONArray arr = new JSONArray(weather);

                for (int i = 0; i < arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);
                    JSONObject jsonPart2 = new JSONObject(temp);

                    String main = jsonPart.getString("main");
                    String desc = jsonPart.getString("description");

                    Double temperatureDouble = Double.parseDouble(jsonPart2.getString("temp"));
                    temperatureDouble -= 273;
                    String temprature = String.format("%.2f", temperatureDouble);


                    mainReport.setText(main + ": " + desc + "\n" + "Temprature: " + temprature + "°c");

                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Enter a Valid City!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainReport = (TextView) findViewById(R.id.main);
        cityEditText = (EditText) findViewById(R.id.cityEditText);


    }
}
