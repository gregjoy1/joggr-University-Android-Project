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

		this._setRouteInfo(this._findRouteInfoFromIntent());
		
	}
	
	private RouteInfo _findRouteInfoFromIntent() {
		
		if(this.getIntent().getExtras() == null) {
			return null;
		} else {
			return this._routes.getRouteByID(Long.parseLong((String) this.getIntent().getExtras().get("com.example.joggr.runID")));
		}		
	}
	
	private void _initAndPopulateTextViews(RouteInfo routeInfo) {
		this._initTextViews();
		
		this._timeTakenTextView.setText("");
		this._averageSpeedTextView.setText("");
		this._maximumSpeedTextView.setText("");
		this._distanceRanTextView.setText("");
	}
	
	private void _initTextViews() {
		this._timeTakenTextView = (TextView) this.findViewById(R.id.timeTakenText);
		this._averageSpeedTextView = (TextView) this.findViewById(R.id.averageSpeedText);
		this._maximumSpeedTextView = (TextView) this.findViewById(R.id.maxSpeedText);
		this._distanceRanTextView = (TextView) this.findViewById(R.id.distanceRanText);
	}
	
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
				Log.d("gmaps", "gmaps messed up...");
				return;
			}
		}
		
		this._map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		this._ui = this._map.getUiSettings();
		this._ui.setMyLocationButtonEnabled(false);
		this._ui.setZoomControlsEnabled(true);

		// start off with oxford
		this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(this._getRouteInfo().getLocations().get(0).getLocation(), 15));
		this._map.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
		
		
		
	}
	
	private void _setRouteInfo(RouteInfo routeInfo) {
		this._routeInfo = routeInfo;
	}
	
	private RouteInfo _getRouteInfo() {
		return this._routeInfo;
	}
	
}
