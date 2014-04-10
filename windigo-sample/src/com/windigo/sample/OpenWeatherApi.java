package com.windigo.sample;

import com.windigo.annotations.Get;
import com.windigo.annotations.QueryParam;
import com.windigo.annotations.RestApi;
import com.windigo.sample.weather.ForecastResponse;

@RestApi
public interface OpenWeatherApi {

	@Get("/weather")
	ForecastResponse getForecast(@QueryParam("lat") double latitude, @QueryParam("lon") double longtiude);
}
