package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText etCity;
    Button btnSearch;
    TextView tvResult;
    String cityUrl;
    //onclickmethod
    public void findWeather(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(etCity.getWindowToken(),0);
        DownloadText downloadText = new DownloadText();
        String result = null;
        try {
            cityUrl = URLEncoder.encode(etCity.getText().toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
        }
        try {
            result = downloadText.execute("http://api.openweathermap.org/data/2.5/weather?q="+cityUrl +"&appid=b7107a5188272f3331796ade6ae3b924").get();
            Log.i("result",result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("city",etCity.getText().toString());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity=(EditText) findViewById(R.id.etCity);
        btnSearch=(Button) findViewById(R.id.btnSearch);
        tvResult=(TextView) findViewById(R.id.tvResult);


       // Log.i("result",result);
    }

    //Download text
    public class DownloadText extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection connection=null;
            try {
                url = new URL(strings[0]);
                connection=(HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while(data!=-1)
                {
                    char current = (char) data;
                    result += current;
                    data=reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String message="";
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherInfo);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject  jsonPart = jsonArray.getJSONObject(i);
                    String main="";
                    String description="";
                    main=jsonPart.getString("main");
                    description=jsonPart.getString("description");
                    if(main != "" && description != "")
                    {
                        message +=main +":"+description+"\r\n";
                    }
                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));
                }
                if(message !="")
                {
                    tvResult.setText(message);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
                }
                Log.i("weather", weatherInfo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }//downloadtext over

}
