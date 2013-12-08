package com.example.joggr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PreviousRunListing extends Activity {
	
	// declarations
	private Routes _routes;
	private ListView _runList;
	private ArrayAdapter<RouteInfo> _routeInfoArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.previous_run_list);
		
		this._routes = new Routes(this.getApplicationContext());
		
		this._initList();
	}
	
	// just instantiates and populates the list
	private void _initList() {
		this._runList = (ListView) this.findViewById(R.id.runlist);
		
		this._routeInfoArrayAdapter = new ArrayAdapter<RouteInfo>(this, android.R.layout.simple_list_item_1, this._routes.getRoutes());

		this._runList.setAdapter(this._routeInfoArrayAdapter);
		this._runList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				_proceedToRunDetail(_routes.getRoutes().get(position).getId());
			}
			
		});
	}
	
	// used to launch the previous run details activity with a desired id
	private void _proceedToRunDetail(long runID) {
		Intent continueToRunDetailsIntent = new Intent(this, PreviousRunDetailActivity.class);;
		continueToRunDetailsIntent.putExtra("com.example.joggr.runID", runID);
		
		this.startActivity(continueToRunDetailsIntent);
		
		Toast.makeText(getApplicationContext(), "Making Continues to the page", Toast.LENGTH_SHORT).show();
	}
	
}
