package com.example.joggr;

import java.util.ArrayList;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteInfo {
	
	private ArrayList<RouteNode> _routeNodes;
	private ArrayList<MarkerOptions> _markers;
	private ArrayList<PolylineOptions> _polylines;
	private int _routeId = -1;
	private int _externalId = -1;
	
	public RouteInfo() {
		this._routeNodes = new ArrayList<RouteNode>();
		this._markers = new ArrayList<MarkerOptions>();
		this._polylines = new ArrayList<PolylineOptions>();
	}
		
	public int getId() {
		return this._routeId;
	}
	
	public void setId(int RouteId) {
		this._routeId = RouteId;
	}
	
	public int getExternalId() {
		return this._externalId;
	}
	
	public void setExternalId(int RouteId) {
		this._externalId = RouteId;
	}	
	
	public boolean doesLocationAlreadyExist(Location location) {
		
		for(int inc = 0; inc < this._routeNodes.size(); inc++) {
			RouteNode routeNode = this._routeNodes.get(inc);
			if
			(
				routeNode.getLocation() == new LatLng(location.getLatitude(), location.getLongitude()) &&
				routeNode.getTime() == location.getTime()
			)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void addLocation(RouteNode location) {
		this._routeNodes.add(location);
	}
	
	public PolylineOptions getNewPolyLineOption() {
		if(this._routeNodes.size() > 1) {
			LatLng lastLocation = this._routeNodes.get(this._routeNodes.size() - 2).getLocation();
			LatLng currentLocation = this._routeNodes.get(this._routeNodes.size() - 1).getLocation();
			PolylineOptions newPolylineOption = new PolylineOptions().add(lastLocation, currentLocation).width(5).color(Color.RED);
			this._polylines.add(newPolylineOption);
			return newPolylineOption;
		} else {
			return null;
		}
	}
	
	public MarkerOptions getNewMarkerOptions() {
		if(this._routeNodes.size() > 0) {
			LatLng currentLocation = this._routeNodes.get(this._routeNodes.size() - 1).getLocation();
			MarkerOptions newMarkerOption = new MarkerOptions().position(currentLocation).title("Log Segment " + this._routeNodes.size());
			this._markers.add(newMarkerOption);
			return newMarkerOption;
		} else {
			return null;
		}
	}
	
	public MarkerOptions[] getAllMarkers() {
		return (MarkerOptions[]) this._markers.toArray();
	}
	
	public PolylineOptions[] getAllPolylines() {
		return (PolylineOptions[]) this._polylines.toArray();
	}
	
	public ArrayList<RouteNode> getLocations() {
		return this._routeNodes;
	}
	
	public String getXml() {
		// Implement
		return "";
	}
	
	public double getAverageSpeedInMph() {
		Long startTime = this._routeNodes.get(0).getTime();
		Long endTime = this._routeNodes.get(this._routeNodes.size() - 1).getTime();

		long hourDifference = (((endTime - startTime) / 60) / 60);
		
		return (this.getDistanceTravelledInMiles() * (1 / hourDifference));
	}
	
	public double getDistanceTravelledInMiles() {
		return (this.getDistanceTravelledInKM() * 0.62137);
	}
	
	public double getDistanceTravelledInKM() {
		double distanceTravelled = 0;
		if(this._routeNodes.size() > 1) {
			for(int inc = 1; inc < this._routeNodes.size(); inc++) {
				LatLng start = this._routeNodes.get(inc - 1).getLocation();
				LatLng end = this._routeNodes.get(inc).getLocation();

				distanceTravelled += this._getDistanceBetweenTwoPoints(start, end);
			}	
		}

		return distanceTravelled;
	}
	
	private double _getDistanceBetweenTwoPoints(LatLng start, LatLng end) {
		
		double latitude1 = start.latitude;
		double longitude1 = start.longitude;
		
		double latitude2 = start.latitude;
		double longitude2 = start.longitude;
		
		double distance = this._radiansToDegrees(
				Math.acos(
						Math.sin(this._degreesToRadians(latitude1)) * 
						Math.sin(this._degreesToRadians(latitude2)) + 
						Math.cos(this._degreesToRadians(latitude1)) * 
						Math.cos(this._degreesToRadians(latitude2)) * 
						Math.cos(this._degreesToRadians(longitude1 - longitude2))
				)
			);
		return ((distance * 60 * 1.1515) * 1.609344);
	}

    private double _degreesToRadians(double degrees) {
      return (degrees * Math.PI / 180.0);
    }

    private double _radiansToDegrees(double radians) {
      return (radians * 180.0 / Math.PI);
    }

	
}
