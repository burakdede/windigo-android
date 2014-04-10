package com.windigo.sample.weather;

public class WeatherResponse {
    private String description, icon;

    
    public WeatherResponse(String description, String icon) {
    	this.description = description;
    	this.icon = icon;
    }

    public String getDescription() {
        return description;
    }


    public String getIcon() {
        return icon;
    }


	public void setDescription(String description) {
		this.description = description;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		return "\nDescription : " + description + " icon : " + icon;
	}

}
