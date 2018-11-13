package com.jblearning.kitchen;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.*;
import java.net.*;
import java.net.URL;
import java.io.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private Button queryBtn;
    private TextView label;
    private String TAG = "yang";
    private StringBuffer queryRes;
    private TextView ins;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queryBtn = findViewById(R.id.btn1);
        label = findViewById(R.id.label);
        ins = findViewById(R.id.Ins);
        img = findViewById(R.id.ig);
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                sendRequestWithHttpURLConnection();
                sendRequest2();

            }
        });

    }

    private void sendRequest2(){
        RecipeRequest.get("recipes/324694/analyzedInstructions?stepBreakdown=false", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                label.setText(response.toString());
                System.out.println(response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                label.setText(response.toString());
                System.out.println(response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, errorResponse.toString());
            }

        });
    }



    private void sendRequestWithHttpURLConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try{
                    URL url = new URL("https://api.edamam.com/" +
                            "search?q=fish&app_id=80c0b7d0&app_key" +
                            "=fc02ed665af3ef207f48e372144507f3&from" +
                            "=0&to=3&calories=591-722&health=alcohol-free");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream ins = connection.getInputStream();
                    reader = new BufferedReader( new InputStreamReader(ins));
                    String line;
                    queryRes = new StringBuffer();
                    while ((line = reader.readLine()) != null){
                        queryRes.append(line);
                    }
                }catch (Exception e){
                    label.setText(e.toString());
                    Log.d(TAG, e.toString());
                }finally {
                    if (reader != null) {
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) connection.disconnect();
                }
                transferJson(queryRes.toString());
            }
        }).start();

    }

    private void transferJson(String jsonString){
        try{
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray recipe = jsonObj.getJSONArray("hits");
            JSONObject rec1 = (JSONObject)recipe.get(0);
            JSONObject recObj = (JSONObject)rec1.get("recipe");
            label.setText(recObj.get("label").toString());
            ins.setText(recObj.get("ingredientLines").toString());
            loadImageFromNetwork(recObj.get("image").toString());
        }catch (Exception e){
            Log.d(TAG,"can not transfer json");
        }
    }

    private void loadImageFromNetwork(String url){
        try{
            final Drawable drawable = Drawable.createFromStream(new URL(url).openStream(),"dish.jpg");
            img.post(new Runnable() {
                @Override
                public void run() {
                    img.setImageDrawable(drawable);
                }
            });
        }catch (IOException e){
            Log.d(TAG, "can not load image");
        }

    }


}
