package com.windigo.sample.weather;

public class WindResponse {

    private float speed;
    
    public WindResponse(float speed) {
    	this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public String toString() {
		return "\nSpeed : " + this.speed;
	}
}
