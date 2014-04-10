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
}
