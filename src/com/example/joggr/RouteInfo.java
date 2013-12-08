package com.example.joggr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteInfo {
	
	//declarations 
	private ArrayList<RouteNode> _routeNodes;
	private ArrayList<MarkerOptions> _markers;
	private ArrayList<PolylineOptions> _polylines;
	private int _routeId = -1;
	private int _externalId = -1;
	private long _startTime = 0;
	private long _endTime = 0;
	private long _timeTakenInSeconds = 0;
	private long _timestamp = 0;
	private String _name;
	
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
	
	// Checks if location already exists in the internally defined arraylist
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
	
	// Adds a location to the array list aswell as the google map marker and poly line
	public void addLocation(RouteNode location) {
		this._routeNodes.add(location);
		this.getNewMarkerOptions();
		this.getNewPolyLineOption();
	}
	
	// This returns the latest polyline as well as pushing it into the arraylist
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
	
	// This returns the latest google maps markers as well as pushing it into the arraylist
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
	
	// Returns the markers array list
	public MarkerOptions[] getAllMarkers() {
		return this._markers.toArray(new MarkerOptions[this._markers.size()]);
	}
	
	// Returns the polyline array list
	public PolylineOptions[] getAllPolylines() {
		return this._polylines.toArray(new PolylineOptions[this._polylines.size()]);
	}
	
	// Returns the locations arraylist
	public ArrayList<RouteNode> getLocations() {
		return this._routeNodes;
	}
	
	// Generates and returns an xml string to be sent to the server
	public String getXml(String userID) {
		
		String xml = 	"<?xml version=\"1.0\" encoding=\"UTF-8\">" + 
							"<brookesml>" + 
								"<user-id>" + userID + "</user-id>" +
								"<date>" + this._createISO8601Date(this._timestamp) + "</date>" +
									"<trk>" +
										"<name>" + this.getName() + "</name>" +
											"<trkseg>";
		
		
		for(int inc = 0; inc<this.getLocations().size(); inc++) {
			LatLng location = this.getLocations().get(inc).getLocation();
			xml +=	"<trkpt lat=\"" + location.latitude + "\" lon=\"" + location.longitude + "\">" +
						"<ele>" + this.getLocations().get(inc).getElevation() + "</ele>" + 
						"<time>" + this._createISO8601Date(this.getLocations().get(inc).getTime()) + "</time>" + 
					"</trkpt>";

		}

		xml += "</trkseg>" + "</brookesml>";
		
		return xml;
	}
	
	// returns a date conforming the ISO 8601 date format
	private String _createISO8601Date(long timestamp) {
		String date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(new Date(timestamp * 1000));
		return date.substring(0, 19) + date.substring(22, date.length());
	}
	
	// Returns average speed in meters per second
	public float getAverageSpeedInMps() {
		return ((float) (this.getDistanceTravelledInKM() * 1000) / this.getTimeTakenInSeconds());
	}
	
	// Returns top speed in meters per second
	public float getTopSpeedInMps() {
		
		float biggestDistanceSoFar = 0;
		float timeTakenInSeconds = 0;
		
		// Acquires the biggest distance travelled, as it logs at a consistent speed
		// this determines the longest stretch of the journey
		for(int inc=1; inc<this._routeNodes.size(); inc++) {
			LatLng start = this._routeNodes.get(inc - 1).getLocation();
			LatLng end = this._routeNodes.get(inc).getLocation();

			double distanceTravelled = this._getDistanceBetweenTwoPointsInKm(start, end);
			
			if(distanceTravelled > biggestDistanceSoFar) {
				biggestDistanceSoFar = (float) distanceTravelled;
				timeTakenInSeconds = (this._routeNodes.get(inc).getTime() - this._routeNodes.get(inc - 1).getTime());
			}
		}
		// This works out how many meters where travelled in the time taken
		return ((float) (biggestDistanceSoFar * 1000) / timeTakenInSeconds);
	}
		
	// calculates and returns the total distance travelled in kilometers
	public double getDistanceTravelledInKM() {
		double distanceTravelled = 0;
		// checks if there is a journey to add up
		if(this._routeNodes.size() > 1) {
			// adds up all the distances between each point
			for(int inc = 1; inc < this._routeNodes.size(); inc++) {
				LatLng start = this._routeNodes.get(inc - 1).getLocation();
				LatLng end = this._routeNodes.get(inc).getLocation();
				distanceTravelled += this._getDistanceBetweenTwoPointsInKm(start, end);
			}	
		}

		return distanceTravelled;
	}
	
	// works out the distance between two longitude and latitude points in kilometers
	private double _getDistanceBetweenTwoPointsInKm(LatLng start, LatLng end) {
		
		double latitude1 = start.latitude;
		double longitude1 = start.longitude;
		
		double latitude2 = end.latitude;
		double longitude2 = end.longitude;
		
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

	// method used in _getDistanceBetweenTwoPointsInKm calculate 
    private double _degreesToRadians(double degrees) {
      return (degrees * Math.PI / 180.0);
    }
    
	// method used in _getDistanceBetweenTwoPointsInKm calculate 
    private double _radiansToDegrees(double radians) {
      return (radians * 180.0 / Math.PI);
    }

    // is called to set the start time, this is used to calculate
    // the amount of time the application is logging for
    public void setStartTime() {
    	this._startTime = (new Date().getTime() / 1000);
    }
    
    // is called to set the end time, this is used to calculate
    // the amount of time the application is logging for    
    public void setEndTime() {
    	this._endTime = (new Date().getTime() / 1000);
    }
    
    // this is used when loading the route from the database
    public void setTimeTakenInSeconds(long seconds) {
    	this._timeTakenInSeconds = seconds;
    }
    
    // this returns the time taken, if no time exists then it returns 1 to avoid division by zero
    public long getTimeTakenInSeconds() {
    	
    	if(this._timeTakenInSeconds == 0) {
        	if(this._startTime == 0 || this._endTime == 0) {
        		return 1;
        	}
        	else
        	{
        		return (this._endTime - this._startTime);
        	}    		
    	} else {
    		return this._timeTakenInSeconds;
    	}
    	
    }
    
    // returns a string [00:00] for the users viewing pleasure on the previous run detail activity
    public String getTimeTakenInMinutes() {
    	long timeTakenInSeconds = this.getTimeTakenInSeconds();
    	// returns string containing the time take in minutes [minutes]:[seconds]
    	return ((int) timeTakenInSeconds / 60) + ":" + (("00" + (timeTakenInSeconds % 60)).substring((timeTakenInSeconds >= 10 ? 2 : 1)));
    }

    // this is purely for the ListView on the previous run listing activity
    public String toString() {
    	return this.getName();
    }
    
    // this sets the overall route timestamp
    public void setRunTimeStamp(long timestamp) {
    	this._timestamp = timestamp;
    }
    
    // this returns the overall route timestamp
    public long getRunTimeStamp() {
    	return this._timestamp;
    }
    
    // this sets the name of the run, if the name is blank it will use the route timestamp instead
    public void setName(String name) {
    	if(name != null && !name.equalsIgnoreCase("")) {
    		this._name = name;
    	} else {
    		this._name = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(this.getRunTimeStamp() * 1000));
    	}
    }
    
    // this gets the name of the run
    public String getName() {
    	if(this._name != null && !this._name.equalsIgnoreCase("")) {
    		return this._name;
    	} else {
    		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(this.getRunTimeStamp() * 1000));
    	}    	
    }
}
