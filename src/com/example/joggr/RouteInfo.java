package com.example.joggr;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class RouteInfo {
	
	private ArrayList<LatLng> _routeNodes;
	private int _routeId;
	
	public RouteInfo(int RouteId) {
		this.setId(RouteId);
	}
	
	public int getId() {
		return this._routeId;
	}
	
	public void setId(int RouteId) {
		this._routeId = RouteId;
	}
	
	public void addLocation(LatLng Location) {
		this._routeNodes.add(Location);
	}
	
	public ArrayList<LatLng> getLocations() {
		return this._routeNodes;
	}
	
	public String getXml() {
		// Implement
		return "";
	}
	
	private class RouteContentProvider extends ContentProvider {

		@Override
		public int delete(Uri arg0, String arg1, String[] arg2) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getType(Uri arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Uri insert(Uri uri, ContentValues values) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean onCreate() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection,
				String[] selectionArgs) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
}
