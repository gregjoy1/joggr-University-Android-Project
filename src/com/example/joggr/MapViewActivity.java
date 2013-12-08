package com.example.joggr;

import java.util.Date;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MapViewActivity extends FragmentActivity {

	private GoogleMap _map; 
	private UiSettings _ui;
	private LocationListener _locationlistener;
	private LocationManager _locationManager;
	private Routes _route;
	private RouteInfo _routeInfo;
	private Location _lastLocation;
	private boolean _isLogging;
	private Button _startStopButton;
	private Settings _settings;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		this._settings = new Settings(this.getApplicationContext());
		
		this._route = new Routes(this.getApplicationContext());
		this._routeInfo = new RouteInfo();
		
		this._startStopButton = (Button) this.findViewById(R.id.startBtn);

		this._startStopButton.setText("Start logging");
		this._startStopButton.setBackgroundColor(Color.GREEN);
		
		this._isLogging = false;
		
		this._startStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_toggleLogging();
			}
			
		});

		this._locationlistener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				_lastLocation = location;
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			  
			}

			public void onProviderEnabled(String provider) {
			  
			}

			public void onProviderDisabled(String provider) {
			  
		  }

		};
		
		this._locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		this._locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this._locationlistener);

		this._locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this._locationlistener);
		
		this._initMap();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this._initMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // keep my cheeky eyes on this
	    // Checks the orientation of the screen
//	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	Log.d("gps", "orientation changed");
//	    }
    }
	
	private void _toggleLogging() {
		
		if(this._isLogging) {
			this._startStopButton.setText("Start logging");
			this._startStopButton.setBackgroundColor(Color.GREEN);
			this._getFinishAlertDialog().show();		
		} else {
			this._startStopButton.setText("Stop logging");
			this._startStopButton.setBackgroundColor(Color.RED);
			Toast.makeText(getBaseContext(), "Logging started...", Toast.LENGTH_SHORT).show();
			new AsyncLogger((this._settings.getLoggingIncrements() * 1000)).execute("");
		}

		this._isLogging = !this._isLogging;

	}
	
	private AlertDialog.Builder _getFinishAlertDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("What would you like to do now?");
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "Saving run...", Toast.LENGTH_SHORT).show();
				_continueToRunDetails(_saveRun(false));
			}
			
		});

		
		builder.setNegativeButton("Pause", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "Save Cancelled...", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		return builder;
	}
	
	private long _saveRun(boolean Upload) {
		return this._route.insertRoute(this._routeInfo);
	}
	
	private void _continueToRunDetails(long runID) {
		
		Intent continueToRunDetailsIntent = new Intent(this, PreviousRunDetailActivity.class);;
		continueToRunDetailsIntent.putExtra("com.example.joggr.runID", runID);
		
		this.startActivity(continueToRunDetailsIntent);
		
		Toast.makeText(getApplicationContext(), "Making Continues to the page", Toast.LENGTH_SHORT).show();
	}
	
	private void _initMap()
	{
		if(this._map != null)
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
		this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.751944,-1.257778), 15));
		this._map.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
	}
	
	private void _pushLocationUpdate() {
		
		// DELETE

			LatLng latitudeLongitude = new LatLng(51.751944,-1.257778);

			this._routeInfo.addLocation(new RouteNode(latitudeLongitude, (long) (new Date().getTime() * 1000)));

			PolylineOptions polyLineOptions = this._routeInfo.getNewPolyLineOption();
			
			if(polyLineOptions != null) {
				_map.addPolyline(polyLineOptions);
			}
			
			MarkerOptions markerOptions = this._routeInfo.getNewMarkerOptions();
			
			if(markerOptions != null) {
				_map.addMarker(markerOptions);
				
			}
							
			_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, _map.getCameraPosition().zoom));


		// DELETE 
		
//		if(this._lastLocation != null && this._routeInfo.doesLocationAlreadyExist(this._lastLocation)) {
//
//			LatLng latitudeLongitude = new LatLng(this._lastLocation.getLatitude(), this._lastLocation.getLongitude());
//			
//			this._routeInfo.addLocation(new RouteNode(latitudeLongitude, (long) (this._lastLocation.getTime() * 1000)));
//
//			PolylineOptions polyLineOptions = this._routeInfo.getNewPolyLineOption();
//			
//			if(polyLineOptions != null) {
//				_map.addPolyline(polyLineOptions);
//			}
//			
//			MarkerOptions markerOptions = this._routeInfo.getNewMarkerOptions();
//			
//			if(markerOptions != null) {
//				_map.addMarker(markerOptions);
//				
//			}
//							
//			_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, _map.getCameraPosition().zoom));
//		}

	}
	
	private class AsyncLogger extends AsyncTask<String, Void, String> {
		
		private int _logInterval;
		
		public AsyncLogger(int LogInterval) {
			this._logInterval = LogInterval;
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			
			while(_isLogging) {
				try {
					this.publishProgress();
					Thread.sleep(this._logInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		
        @Override
        protected void onPostExecute(String result) {
        	Log.d("gps", "Logging stopped...");
        }

        @Override
        protected void onPreExecute() {
        	Log.d("gps", "Logging started...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
			_pushLocationUpdate();        
        }
		
	}

}
