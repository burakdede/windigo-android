package com.windigo.sample;

import com.windigo.annotations.Get;
import com.windigo.annotations.QueryParam;
import com.windigo.annotations.QueryParamsWith;
import com.windigo.annotations.RestApi;
import com.windigo.sample.weather.ForecastResponse;
import com.windigo.sample.weather.LocationRequest;

import java.util.Map;

@RestApi
public interface OpenWeatherApi {

	@Get("/weather")
	ForecastResponse getForecast(@QueryParam("lat") double latitude, @QueryParam("lon") double longtiude);
	
	
	@Get("/weather")
	ForecastResponse getForecastWithMap(@QueryParamsWith Map<String, Double> coordinates);
	
	
	@Get("/weather")
	ForecastResponse getForecastWithObject(@QueryParamsWith LocationRequest location);
}
