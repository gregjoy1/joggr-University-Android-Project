package com.example.joggr;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class RouteNode {

	private long _timestamp;
	private LatLng _location;
	
	public RouteNode(LatLng location, long timeStamp) {
		this.setLocation(location);
		this.setTime(timeStamp);
		
		Log.d("gps", timeStamp+"");
	}
	
	public void setTime(long time) {
		this._timestamp = time;
	}
	
	public long getTime() {
		return this._timestamp;
	}
	
	public void setLocation(LatLng location) {
		this._location = location;
	}
	
	public LatLng getLocation() {
		return this._location;
	}
	
}
