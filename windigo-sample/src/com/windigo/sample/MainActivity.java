package com.windigo.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.windigo.RestApiFactory;
import com.windigo.android.windigotest.R;
import com.windigo.http.HttpClient;
import com.windigo.http.HttpClientFactory;
import com.windigo.sample.LastfmRestApi;
import com.windigo.sample.OpenWeatherApi;
import com.windigo.sample.lastfm.Response;
import com.windigo.sample.weather.ForecastResponse;

public class MainActivity extends Activity {
	
	private TextView responseTextView;
	private LastfmRestApi lastfmRestApi;
	private OpenWeatherApi openWeatherApi;
	
	private static final String TAG = MainActivity.class.getCanonicalName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		responseTextView = (TextView) findViewById(R.id.responseTextView);
		
		// we need default http client
		HttpClient httpClient = HttpClientFactory.getDefaultHttpClient();
		
		// call factory method with url and interface class for rest api
		lastfmRestApi = RestApiFactory.createNewService("http://ws.audioscrobbler.com", LastfmRestApi.class, httpClient);
		
		// call factory method with url and interface class for rest api		
		openWeatherApi = RestApiFactory.createNewService("http://api.openweathermap.org/data/2.5", 
				OpenWeatherApi.class, httpClient);
		
		try {
			//new ForecastTask().execute();
			new LastfmTask().execute();
		} catch (Exception e) {
			Log.d(TAG, e.getCause().toString());
		}
	}
	
	private class LastfmTask extends AsyncTask<Void, Integer, Response> {
		@Override
		protected Response doInBackground(Void... params) {
			return lastfmRestApi.getAlbumInfo("album.getinfo", "49f6b21cab1c48100ee59f216645275e", "Cher", "Believe", "json");
		}
		
		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			// use response data however you want
			// you can access all of the properties easily
			responseTextView.setText(result.getAlbum().getName());
		}
	}
	
	
	private class ForecastTask extends AsyncTask<Void, Integer, ForecastResponse> {

		@Override
		protected ForecastResponse doInBackground(Void... params) {
			
			return openWeatherApi.getForecast(47.663267, -122.384187);
		}
		
		@Override
		protected void onPostExecute(ForecastResponse result) {
			super.onPostExecute(result);
			// use response data however you want
			// you can access all of the properties easily
			responseTextView.setText("Completed : " + result.getWind().getSpeed());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
