package com.example.joggr;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MapViewActivity extends FragmentActivity {

	private GoogleMap _map; 
	private UiSettings _ui;
	private LocationListener _locationlistener;
	private LocationManager _locationManager;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		
		this._locationlistener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				MarkerOptions mo = new MarkerOptions();
				mo.position(new LatLng(location.getLatitude(), location.getLongitude()));
				mo.title("My current location?!");
				_map.addMarker(mo);
				
				_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
				_map.animateCamera(CameraUpdateFactory.zoomTo(10), 5000, null);
				
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			  
			}

			public void onProviderEnabled(String provider) {
			  
			}

			public void onProviderDisabled(String provider) {
			  
		  }

		};
		
		this._locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
//		this._locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this._locationlistener);

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

}
