package com.example.joggr;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;

public class RouteInfo {
	
	private ArrayList<RouteInfoNode> _routeNodes;
	private int _routeId;
	
	public RouteInfo(int RouteId) {
		this.setId(RouteId);
	}
	
	public int getId() {
		return this._routeId;
	}
	
	public void setId(int RouteId) {
		this._routeId = RouteId;
	}
	
	public void addLocation(LatLng Location) {
		this._routeNodes.add(new RouteInfoNode(Location));
	}
	
	public ArrayList<RouteInfoNode> getLocations() {
		return this._routeNodes;
	}
	
	public String getXml() {
		// Implement
		return "";
	}
	
}
