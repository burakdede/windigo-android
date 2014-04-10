package com.windigo.sample.weather;

import java.util.List;


public class ForecastResponse {

    private String name;
    private MainResponse main;
    private WindResponse wind;
    private List<WeatherResponse> weather;

    public String getName() {
        return name;
    }

    public MainResponse getMain() {
        return main;
    }

    public List<WeatherResponse> getWeather() {
        return weather;
    }

    public WindResponse getWind() {
        return wind;
    }

	public void setName(String name) {
		this.name = name;
	}

	public void setMain(MainResponse main) {
		this.main = main;
	}

	public void setWind(WindResponse wind) {
		this.wind = wind;
	}

	public void setWeather(List<WeatherResponse> weather) {
		this.weather = weather;
	}
	
	@Override
	public String toString() {
		
		StringBuilder weatherStringBuilder = new StringBuilder();
		
		for(WeatherResponse weatherResponse : weather) weatherStringBuilder.append(weatherResponse.toString());
		
		return "Name : " + name + " MainResponse : " + main.toString() + " WindResponse : " + wind.toString()
				+ weatherStringBuilder.toString();
	}
}
