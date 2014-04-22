package com.windigo.sample;

import java.util.Map;

import com.windigo.annotations.Get;
import com.windigo.annotations.QueryParam;
import com.windigo.annotations.QueryParamMap;
import com.windigo.annotations.QueryParamObject;
import com.windigo.annotations.RestApi;
import com.windigo.sample.weather.ForecastResponse;
import com.windigo.sample.weather.LocationRequest;

@RestApi
public interface OpenWeatherApi {

	@Get("/weather")
	ForecastResponse getForecast(@QueryParam("lat") double latitude, @QueryParam("lon") double longtiude);
	
	
	@Get("/weather")
	ForecastResponse getForecastWithMap(@QueryParamMap Map<String, Double> coordinates);
	
	
	@Get("/weather")
	ForecastResponse getForecastWithObject(@QueryParamObject LocationRequest location);
}
