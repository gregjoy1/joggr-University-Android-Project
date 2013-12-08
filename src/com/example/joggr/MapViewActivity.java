package com.example.joggr;

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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	
	// FOR DEBUGGING
//	private double tmpLat = 51.751944;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		// instantiates all required classes and attributes
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

		// instantiates the location listener
		this._locationlistener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				// at every update, the location is logged. then the 
				// latest location is used at each logging increment.
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
	
	private void _toggleLogging() {
		
		if(this._isLogging) {
			this._startStopButton.setText("Start logging");
			this._startStopButton.setBackgroundColor(Color.GREEN);
			this._routeInfo.setEndTime();
			// if button is unpaused, and there are at least two locations
			// it shows the first of the save step dialogs
			if(this._routeInfo.getLocations().size() >= 2) {
				this._getFinishAlertDialog().show();				
			} else {
				Toast.makeText(this.getApplicationContext(), "Not enough gps data to save...", Toast.LENGTH_SHORT).show();
			}
		} else {
			// starts logging
			this._startStopButton.setText("Stop logging");
			this._startStopButton.setBackgroundColor(Color.RED);
			Toast.makeText(getBaseContext(), "Logging started...", Toast.LENGTH_SHORT).show();
			// starts the logging thread
			new AsyncLogger((this._settings.getLoggingIncrements() * 1000)).execute("");
			this._routeInfo.setStartTime();
		}

		this._isLogging = !this._isLogging;

	}
	
	// if they want to save the run or to pause it
	private AlertDialog.Builder _getFinishAlertDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("What would you like to do now?");
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// if the user wants to save, it opens the next dialog.
				_getRunInfoPromptDialog().show();
			}
			
		});

		
		builder.setNegativeButton("Pause", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// if they want to cancel, it closes the dialog.
				Toast.makeText(getApplicationContext(), "Save Cancelled...", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		return builder;
	}

	// generates the dialog that prompts the user for the run name
	private AlertDialog.Builder _getRunInfoPromptDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Name your run!");
		
		// adds a textfield to the dialog to capture the run name
		final EditText runName = new EditText(this.getApplicationContext());
		
		runName.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(runName);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// proceeds to next activity
				Toast.makeText(getApplicationContext(), "Saving run...", Toast.LENGTH_SHORT).show();
				_continueToRunDetails(_saveRun(_settings.doesAppAutoUpload(), runName.getText().toString()));
			}
			
		});

		// this aborts the save process and returns to the paused logging
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "Save Cancelled...", Toast.LENGTH_SHORT).show();
			}
			
		});

		builder.setNeutralButton("Auto Upload Settings", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// opens dialog for obtaining auto upload preference
				_getAutoUploadDialog().show();
			}
			
		});
		
		return builder;
	}
	
	// returns the dialog asking the user whether or not they would like to auto upload
	private AlertDialog.Builder _getAutoUploadDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// creates a checkbox [YES|NO] to determine preference
		final CharSequence[] items = {"Auto Upload", "Dont Auto Upload"};
		builder.setSingleChoiceItems(items, (this._settings.doesAppAutoUpload() ? 0 : 1), new DialogInterface.OnClickListener() {
		
				@Override
				public void onClick(DialogInterface dialogInterface, int item) {
					// depending on what the user chose, it saves that preference in settings
					_settings.setIfAppAutoUploads(item == 0);
					_settings.saveSettings();
					// re-opens previous dialog
					_getRunInfoPromptDialog().show();
					Toast.makeText(getApplicationContext(), "Settings Updated...", Toast.LENGTH_SHORT).show();
				}
				
			});
		
		return builder;
	}
	
	// saves the run, also uploads it to server if desired
	private long _saveRun(boolean upload, String name) {
		this._routeInfo.setName(name);
		
		
		if(upload) {
			SocketHelper socketHelper = new SocketHelper(this.getApplicationContext());
			socketHelper.setRoute(this._routeInfo);
			socketHelper.setUsername(this._settings.getUsername());
			socketHelper.execute("");
		}
		
		return this._route.insertRoute(this._routeInfo);
	}
	
	// creates the run details intent and launches it
	private void _continueToRunDetails(long runID) {
		
		Intent continueToRunDetailsIntent = new Intent(this, PreviousRunDetailActivity.class);;
		continueToRunDetailsIntent.putExtra("com.example.joggr.runID", runID);
		
		this.startActivity(continueToRunDetailsIntent);
		
		Toast.makeText(getApplicationContext(), "Making Continues to the page", Toast.LENGTH_SHORT).show();
	}
	
	// initialises google maps fragment
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
				Log.d("JOGGR", "gmaps messed up...");
				return;
			}
		}
		
		this._map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		this._ui = this._map.getUiSettings();
		this._ui.setMyLocationButtonEnabled(false);
		this._ui.setZoomControlsEnabled(true);

		// start off with oxford (why not?)
		this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.751944,-1.257778), 15));
		this._map.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
	}
	
	private void _pushLocationUpdate() {
		
//		FOR DEBUGGING - ignore =================================================================
//
//			this.tmpLat += 0.0001;
//		
//			LatLng latitudeLongitude = new LatLng(this.tmpLat,-1.257778);
//
//			this._routeInfo.addLocation(new RouteNode(latitudeLongitude, (long) (new Date().getTime() / 1000), 40));
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
//
//		DEBUGGING ================================================================= 
		
		// Makes sure that there is a location to "push"
		if(this._lastLocation != null && this._routeInfo.doesLocationAlreadyExist(this._lastLocation)) {

			// extracts long lat to instantiate a LatLng object
			LatLng latitudeLongitude = new LatLng(this._lastLocation.getLatitude(), this._lastLocation.getLongitude());
			
			// adds the location to routeInfo
			this._routeInfo.addLocation(new RouteNode(latitudeLongitude, (long) (this._lastLocation.getTime() / 1000), this._lastLocation.getAltitude()));

			// adds a poly line to google maps
			PolylineOptions polyLineOptions = this._routeInfo.getNewPolyLineOption();
			
			if(polyLineOptions != null) {
				_map.addPolyline(polyLineOptions);
			}
			
			// adds a marker to google maps
			MarkerOptions markerOptions = this._routeInfo.getNewMarkerOptions();
			
			if(markerOptions != null) {
				_map.addMarker(markerOptions);
				
			}
			
			// moves the camera to the new location
			_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudeLongitude, _map.getCameraPosition().zoom));
		}

	}
	
	// logger class, at defined intervals (in settings) this loops and sleep,
	// and at every interval, it calls _pushLocationUpdate to add the new location
	// to routeInfo and to the map
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
        	Log.d("JOGGR", "Logging stopped...");
        }

        @Override
        protected void onPreExecute() {
        	Log.d("JOGGR", "Logging started...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
			_pushLocationUpdate();        
        }
		
	}

}
