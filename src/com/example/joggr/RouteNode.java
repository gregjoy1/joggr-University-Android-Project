package com.example.joggr;

import com.google.android.gms.maps.model.LatLng;

// just a data structure really...
public class RouteNode {

	private long _timestamp;
	private LatLng _location;
	private double _elevation;
	
	public RouteNode(LatLng location, long timeStamp, double elevation) {
		this.setLocation(location);
		this.setTime(timeStamp);
		this.setElevation(elevation);
	}
	
	public void setTime(long time) {
		this._timestamp = time;
	}
	
	public long getTime() {
		return this._timestamp;
	}
	
	public double getElevation() {
		return this._elevation;
	}
	
	public void setLocation(LatLng location) {
		this._location = location;
	}
	
	public LatLng getLocation() {
		return this._location;
	}
	
	public void setElevation(double elevation) {
		this._elevation = elevation;
	}
	
}
