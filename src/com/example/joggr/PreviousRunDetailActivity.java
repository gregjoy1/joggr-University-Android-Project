package com.example.joggr;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PreviousRunDetailActivity extends FragmentActivity {

	// declarations
	private Routes _routes;
	
	private GoogleMap _map;
	private UiSettings _ui;
	
	private TextView _timeTakenTextView;
	private TextView _averageSpeedTextView;
	private TextView _maximumSpeedTextView;
	private TextView _distanceRanTextView;
	
	private RouteInfo _routeInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.previous_run_detail);
		
		this._routes = new Routes(this.getApplicationContext());

		// finds and sets the routeinfo from intent "putextra messages"
		this._setRouteInfo(this._findRouteInfoFromIntent());
		
		this._initMap();
		
		this._initAndPopulateTextViews(this._getRouteInfo());
		
	}
	
	// find the RouteInfo instance from the intent putextra message 
	private RouteInfo _findRouteInfoFromIntent() {
		
		if(this.getIntent().getExtras() == null) {
			return null;
		} else {
			return this._routes.getRouteByID(this.getIntent().getExtras().getLong("com.example.joggr.runID"));
		}		
	}
	
	// initialises and populates all the textviews
	private void _initAndPopulateTextViews(RouteInfo routeInfo) {
		this._initTextViews();
		
		this._timeTakenTextView.setText(this._routeInfo.getTimeTakenInMinutes());
		this._averageSpeedTextView.setText(this._routeInfo.getAverageSpeedInMps() + " meters per second");
		this._maximumSpeedTextView.setText(this._routeInfo.getTopSpeedInMps() + " meters per second");
		this._distanceRanTextView.setText(this._routeInfo.getDistanceTravelledInKM() + " kilometers");
	}
	
	private void _initTextViews() {
		this._timeTakenTextView = (TextView) this.findViewById(R.id.timeTakenText);
		this._averageSpeedTextView = (TextView) this.findViewById(R.id.averageSpeedText);
		this._maximumSpeedTextView = (TextView) this.findViewById(R.id.maxSpeedText);
		this._distanceRanTextView = (TextView) this.findViewById(R.id.distanceRanText);
	}
	
	// initialises the map
	private void _initMap()
	{
		if(this._map != null || this._getRouteInfo() == null)
		{
			return;
		}
		else
		{
			this._map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmapView)).getMap();
			if(this._map == null)
			{
				Log.d("JOGGR", "gmaps messed up...");
				return;
			}
		}
		
		this._map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		this._ui = this._map.getUiSettings();
		this._ui.setMyLocationButtonEnabled(false);
		this._ui.setZoomControlsEnabled(true);

		this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(this._getRouteInfo().getLocations().get(0).getLocation(), 15));
		this._map.animateCamera(CameraUpdateFactory.zoomTo(10), 500, null);

		// adds all markers to map
		for(int inc=0; inc<this._routeInfo.getAllMarkers().length; inc++) {
			this._map.addMarker(this._routeInfo.getAllMarkers()[inc]);
		}
		
		// adds all polylines
		for(int inc=0; inc<this._routeInfo.getAllPolylines().length; inc++) {
			this._map.addPolyline(this._routeInfo.getAllPolylines()[inc]);
		}
		
	}
	
	private void _setRouteInfo(RouteInfo routeInfo) {
		this._routeInfo = routeInfo;
	}
	
	private RouteInfo _getRouteInfo() {
		return this._routeInfo;
	}
	
}
