package com.example.administrator.weatherdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView timeText, cityText, tmpMaxText, tmpMinText, dayTimeText, nightText, windDirText, windScText, tomorrowCondText, tomorrowTmpText;
    //时间，城市，最高温度，最低温度，白天天气，夜晚天气，风向，风力，明日天气，明日温度
    TextView nowWeather,nowTmp,nowWindDir,nowWindSc;
    Button searchBtn;
    Spinner cityItem;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                String result=msg.obj.toString();
                updateUI(result);
                Log.d(TAG, "handleMessage: "+result);
            }

        }
    };
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    public void updateUI(String result){
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            timeText.setText(simpleDateFormat.format(new Date()));
            JSONArray jsonArray=new JSONObject(result).getJSONArray("HeWeather6");
            JSONObject jsonObject=jsonArray.getJSONObject(0);
            JSONObject now=jsonObject.getJSONObject("now");
            cityText.setText(cityItem.getSelectedItem().toString());
            nowWeather.setText(now.get("cond_txt")+"");
            nowTmp.setText(now.get("tmp")+"℃");
            nowWindDir.setText(now.get("wind_dir")+"");
            nowWindSc.setText(now.get("wind_sc")+"级");
            JSONArray jsonArray1=jsonObject.getJSONArray("daily_forecast");
            JSONObject todayJson= (JSONObject) jsonArray1.get(0);
            tmpMaxText.setText(todayJson.get("tmp_max")+"℃");
            tmpMinText.setText(todayJson.get("tmp_min")+"℃");
            dayTimeText.setText(todayJson.get("cond_txt_d")+"");
            nightText.setText(todayJson.get("cond_txt_n")+"");
            windDirText.setText(todayJson.get("wind_dir")+"");
            windScText.setText(todayJson.get("wind_sc")+"级");
            JSONObject tomorrowJson= (JSONObject) jsonArray1.get(1);
            tomorrowTmpText.setText(tomorrowJson.get("cond_txt_d")+"");
            tomorrowCondText.setText(tomorrowJson.get("tmp_min")+"-"+tomorrowJson.get("tmp_max")+"℃");
            Log.d(TAG, "updateUI: "+todayJson);
            Log.d(TAG, "updateUI: "+jsonArray);
            Log.d(TAG, "updateUI: "+now);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void initUI() {
        nowWeather=findViewById(R.id.now_weather_text_view);
        nowTmp=findViewById(R.id.now_tmp_text_view);
        nowWindDir=findViewById(R.id.now_wind_dir_text_view);
        nowWindSc=findViewById(R.id.now_wind_sc_text_view);
        searchBtn = findViewById(R.id.search_button);
        searchBtn.setOnClickListener(this);
        cityItem = findViewById(R.id.city_item);
        timeText = findViewById(R.id.time_text_view);
        cityText = findViewById(R.id.city_text_view);
        tmpMaxText = findViewById(R.id.tmp_max_text_view);
        tmpMinText = findViewById(R.id.tmp_min_text_view);
        dayTimeText = findViewById(R.id.daytime_text_view);
        nightText = findViewById(R.id.night_text_view);
        windDirText = findViewById(R.id.wind_dir_text_view);
        windScText = findViewById(R.id.wind_sc_text_view);
        tomorrowCondText = findViewById(R.id.tomorrow_cond);
        tomorrowTmpText = findViewById(R.id.tomorrow_temperature);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                search();
                break;

        }
    }

    public void search() {
        Log.d(TAG, "search: 1111");
        OkHttpClient client = new OkHttpClient();
        String uri = "https://free-api.heweather.com/s6/weather?location=" + cityItem.getSelectedItem().toString() + "&key=28447ca39f494c3ab07ebfbcdd9d465a";
        Request request = new Request.Builder()
                .url(uri)
                .build();
        final Call call = client.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 222222");
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onResponse: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message=new Message();
                        message.what=1;
                        message.obj=response.body().string();
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();

    }
}

