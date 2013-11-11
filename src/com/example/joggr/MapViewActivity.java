package com.example.joggr;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
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
	private LatLng _lastLocation;
	private boolean _isLogging;
	private Button _startStopButton;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		this._startStopButton = (Button) this.findViewById(R.id.startBtn);
		
		this._isLogging = false;
		
		this._startStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_toggleLogging();
			}
			
		});
		
		

		this._locationlistener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
//				MarkerOptions mo = new MarkerOptions();
//				mo.position(new LatLng(location.getLatitude(), location.getLongitude()));
//				mo.title("My current location?!");
//				_map.addMarker(mo);
//				
//				Log.d("gps", "updaten ma locationz");
//				
//				_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
//				_map.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);

				_lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
				
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
	
	private void _toggleLogging() {
		
		if(this._isLogging) {
			this._startStopButton.setText("Start logging");
			this._startStopButton.setBackgroundColor(Color.GREEN);
			Toast.makeText(getBaseContext(), "Logging stopped...", Toast.LENGTH_SHORT).show();			
		} else {
			this._startStopButton.setText("Stop logging");
			this._startStopButton.setBackgroundColor(Color.RED);
			Toast.makeText(getBaseContext(), "Logging started...", Toast.LENGTH_SHORT).show();
			new AsyncLogger(5000).execute("");
		}

		this._isLogging = !this._isLogging;

	}
	
	private void _initMap()
	{
		if(this._map != null)
		{
			return;
		}
		else
		{
			this._map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap)).getMap();
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
		Log.d("gps", this._lastLocation.toString());

		MarkerOptions mo = new MarkerOptions();
		mo.position(this._lastLocation);
		mo.title("My current location?!");
		_map.addMarker(mo);
				
		_map.moveCamera(CameraUpdateFactory.newLatLngZoom(this._lastLocation, 15));
		_map.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);

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
