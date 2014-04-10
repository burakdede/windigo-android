package com.windigo.sample.weather;

public class MainResponse {

    private double temp, temp_min, temp_max;
    private int humidity;
    
    public MainResponse(double temp, double temp_min, double temp_max, int humidity) {
    	this.temp = temp;
    	this.temp_min = temp_min;
    	this.temp_max = temp_max;
    	this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }


    public double getTemp_min() {
        return temp_min;
    }


    public double getTemp_max() {
        return temp_max;
    }


    public int getHumidity() {
        return humidity;
    }


	public void setTemp(double temp) {
		this.temp = temp;
	}


	public void setTemp_min(double temp_min) {
		this.temp_min = temp_min;
	}


	public void setTemp_max(double temp_max) {
		this.temp_max = temp_max;
	}


	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}
	
	@Override
	public String toString() {
		return "\nTemp : " + this.temp + " temp_max : " + temp_max + " temp_min : " + temp_min; 
	}
}
