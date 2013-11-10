package com.example.joggr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	Button _startButton;
	Button _previousRunsButton;
	Button _settingsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_view);
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
	
	private void _showMapLogging() {
		Intent mapViewIntent = new Intent(this, MapViewActivity.class);
		startActivity(mapViewIntent);
	}
	
	private void _startPreviousRunListings() {
		Toast.makeText(this.getBaseContext(), "Showen ma rans", Toast.LENGTH_SHORT).show();
	}
	
	private void _showSettings() {
		Intent settingsIntent = new Intent(this, SettingsViewActivity.class);
		startActivity(settingsIntent);
	}
	
}
