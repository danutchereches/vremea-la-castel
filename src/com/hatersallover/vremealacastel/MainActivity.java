package com.hatersallover.vremealacastel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather?id=667262&units=metric&APPID=3ceef9d4ba9bddb275a84a23495bd8ff";
	
	private Tracker mTracker = null;
	
	private TextView mWeatherStatus;
	private TextView mTemperature;
	private TextView mLoading;
	private View mLine1, mLine2;
	private ImageView mDescImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mWeatherStatus = (TextView) findViewById(R.id.weather_status);
		mTemperature = (TextView) findViewById(R.id.temperature);
		mLoading = (TextView) findViewById(R.id.loading);
		mLine1 = findViewById(R.id.line1);
		mLine2 = findViewById(R.id.line2);
		mDescImage = (ImageView) findViewById(R.id.desc_image);
		
		loadWeather();
		
		GoogleAnalytics analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(this);
		analytics.setLocalDispatchPeriod(5);
		mTracker = analytics.newTracker("UA-55456421-9");
		mTracker.enableAdvertisingIdCollection(true);
		
		mTracker.setScreenName("main_activity");
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		GoogleAnalytics.getInstance(this).dispatchLocalHits();
	}
	
	public void loadWeather() {
		new RequestTask().execute(WEATHER_API_URL);
	}
	
	class RequestTask extends AsyncTask<String, String, JSONObject>{
		
		@Override
		protected JSONObject doInBackground(String... uri) {
			mLoading.setVisibility(View.VISIBLE);
			mTemperature.setVisibility(View.INVISIBLE);
			mLine1.setVisibility(View.INVISIBLE);
			mLine2.setVisibility(View.INVISIBLE);
			mDescImage.setVisibility(View.INVISIBLE);
			
			try {
				URL url = new URL(uri[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(15000);
				conn.setRequestProperty("charset", "utf-8");
				InputStream is = conn.getInputStream();
				InputStreamReader r = new InputStreamReader(is);
				Reader in = new BufferedReader(r);
				StringBuffer buffer = new StringBuffer();
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char)ch);
				}
				in.close();
				
				return new JSONObject(buffer.toString());
			} catch (Exception e) {
				Log.e("bontida", e.getMessage());
				
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			
			if (result == null) {
				mLoading.setText("N-ai net, nu stii care-i treaba la castel! Prefa-te ca nu ploua!".toUpperCase());
				mLoading.setVisibility(View.VISIBLE);
			} else {
				try {
					JSONObject main = result.getJSONObject("main");
					JSONObject weather = result.getJSONArray("weather").getJSONObject(0);
					
					mLoading.setVisibility(View.INVISIBLE);
					mLine1.setVisibility(View.VISIBLE);
					mLine2.setVisibility(View.VISIBLE);
					
					if (weather.getString("main").toLowerCase().contains("rain")) {
						if (weather.getString("description").toLowerCase().contains("light")) {
							mWeatherStatus.setText("Ploua! Revine mocirla!".toUpperCase());
							mTemperature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_lrain, 0, 0, 0);
							mDescImage.setImageResource(R.drawable.img_rain);
						} else {
							mWeatherStatus.setText("Ploua de rupe!".toUpperCase());
							mTemperature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_rain, 0, 0, 0);
							mDescImage.setImageResource(R.drawable.img_rain);
						}
					} else if (weather.getString("main").toLowerCase().contains("cloud")) {
						mWeatherStatus.setText("Nu mai ploua! Noroiul insista!".toUpperCase());
						mTemperature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_clouds, 0, 0, 0);
						mDescImage.setImageResource(R.drawable.img_rain);
					} else if (main.getDouble("temp") < 22){
						mWeatherStatus.setText("Nu mai ploua! Deocamdata!".toUpperCase());
						mTemperature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_cloudy, 0, 0, 0);
						mDescImage.setImageResource(R.drawable.img_sun);
					} else {
						mWeatherStatus.setText("Nu mai ploua! Deocamdata!".toUpperCase());
						mTemperature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weather_sun, 0, 0, 0);
						mDescImage.setImageResource(R.drawable.img_sun);
					}
					
					mWeatherStatus.setVisibility(View.VISIBLE);
					mDescImage.setVisibility(View.VISIBLE);
					
					mTemperature.setText(Integer.toString((int) main.getDouble("temp")) + "\u00b0 C");
					mTemperature.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					mLoading.setText("N-ai net, nu stii care-i treaba la castel! Prefa-te ca nu ploua!".toUpperCase());
					mLoading.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
