package com.example.joggr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FragmentActivity {
	
	Button _startButton;
	Button _previousRunsButton;
	Button _settingsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_view);
		// create and initialise menu buttons
		this._initMenuView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void _initMenuView() {
		this._startButton = (Button) this.findViewById(R.id.startBtn);
		this._previousRunsButton = (Button) this.findViewById(R.id.prevRunsBtn);
		this._settingsButton = (Button) this.findViewById(R.id.settingsBtn);
		
		this._initMenuButtonListeners();
	}
	
	private void _initMenuButtonListeners() {
		
		// Defines all the button listeners
		this._startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_showMapLogging();
			}
			
		});
		
		this._previousRunsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_startPreviousRunListings();
			}
			
		});
		
		this._settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_showSettings();
			}
			
		});
		
	}
	
	// Starts the map logging activity
	private void _showMapLogging() {
		Intent mapViewIntent = new Intent(this, MapViewActivity.class);
		startActivity(mapViewIntent);
	}
	
	// Starts the previous run listing activity
	private void _startPreviousRunListings() {
		Intent runListingIntent = new Intent(this, PreviousRunListing.class);
		this.startActivity(runListingIntent);
	}
	
	// Starts the settings activity
	private void _showSettings() {
		Intent settingsIntent = new Intent(this, SettingsViewActivity.class);
		this.startActivity(settingsIntent);
	}
	
}
