package com.example.joggr;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

// overall routes class
// acts as a helper and a container for routeinfo instances 
public class Routes {

	private ArrayList<RouteInfo> _routes;
	private RouteDatabase _routeDatabase;
	
	public Routes(Context context) {
		this._routes = new ArrayList<RouteInfo>();
		// instantiates the database helper
		this._routeDatabase = new RouteDatabase(context);
	 	this.loadRoutes();
	}
	
	public void loadRoutes() {

		// runs a query on all the routes in sqlite
		Cursor routeCursor = this._routeDatabase.getAndOpenReadableDatabase().query(
				RouteDatabase.RouteTableConstants.ROUTE_TABLE_NAME, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null
		);
		
		// loops through each and every entry and instantiates the object
		for (routeCursor.moveToFirst(); !(routeCursor.isAfterLast()); routeCursor.moveToNext()){
			this._routes.add(this._createRouteInfoInstanceFromCursor(routeCursor));
		}
		
	}
	
	// uses the cursor from database to return RouteInfo instance with 
	// RouteNode children
	private RouteInfo _createRouteInfoInstanceFromCursor(Cursor cursor) {
		
		RouteInfo routeInfo = new RouteInfo();

		// extracts data from the columns and populates the RouteInfo instance
		routeInfo.setId(cursor.getInt(0));
		routeInfo.setExternalId(cursor.getInt(1));
		routeInfo.setRunTimeStamp(cursor.getLong(2));
		routeInfo.setTimeTakenInSeconds(cursor.getLong(3));
		routeInfo.setName(cursor.getString(4));

		// runs a query on the route nodes with the RouteInfo id
		Cursor routeNodeCursor = 
				this._routeDatabase.getAndOpenReadableDatabase().query(
					RouteDatabase.RouteNodeTableConstants.ROUTE_NODE_TABLE_NAME, 
					null, 
					RouteDatabase.RouteNodeTableConstants.ROUTE_ID + " = " + cursor.getInt(0), 
					null, 
					null, 
					null, 
					RouteDatabase.RouteNodeTableConstants.TIME + " ASC"
				);
		
		// interates and instantiates each and every routenode and pushes it into RouteInfo
		for (routeNodeCursor.moveToFirst(); !(routeNodeCursor.isAfterLast()); routeNodeCursor.moveToNext()){
			LatLng location = new LatLng(
					Double.parseDouble(routeNodeCursor.getString(2)), 
					Double.parseDouble(routeNodeCursor.getString(1))
				);
			
			// pushes the newly instantiated RouteNode into the RouteInfo object
			routeInfo.addLocation(new RouteNode(location, routeNodeCursor.getLong(4), routeNodeCursor.getDouble(3)));
		}
		
		return routeInfo;
	}
	
	// gets a RouteInfo instance and dumps it into the database
	public long insertRoute(RouteInfo routeInfo) {
		
		long lastInsertedID = -1;
		
		ContentValues contentValues = new ContentValues();
		
		// populates the ContentValues object with the RouteInfo attributes for insertion
		contentValues.put(RouteDatabase.RouteTableConstants.EXTERNAL_ID, routeInfo.getExternalId());		
		contentValues.put(RouteDatabase.RouteTableConstants.DATE, new Date().getTime() / 1000);
		contentValues.put(RouteDatabase.RouteTableConstants.TIMETAKEN, routeInfo.getTimeTakenInSeconds());
		contentValues.put(RouteDatabase.RouteTableConstants.NAME, routeInfo.getName());

		// inserts the records for RouteInfo
		this._routeDatabase.getAndOpenWritableDatabase().insert(RouteDatabase.RouteTableConstants.ROUTE_TABLE_NAME, null, contentValues);
		
		// aquires the last auto incrementing id from the insert to be used for inserting the RouteNodes
		// so that they can be associated with each other
		Cursor lastInsertedIDCusor = this._routeDatabase.getAndOpenReadableDatabase().rawQuery("select _id from routes order by _id desc limit 1;", null);
		
		lastInsertedIDCusor.moveToFirst();
		
		if(!lastInsertedIDCusor.isAfterLast()) {
			
			lastInsertedID = lastInsertedIDCusor.getLong(0);
			
			for(int inc = 0; inc<routeInfo.getLocations().size(); inc++) {
				ContentValues routeNodeContentValues = new ContentValues();
				// populates the ContentValues object with RouteNodes attributes
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.ROUTE_ID, lastInsertedIDCusor.getLong(0));
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.LONGITUDE, routeInfo.getLocations().get(inc).getLocation().longitude);
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.LATITUDE, routeInfo.getLocations().get(inc).getLocation().latitude);
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.ELEVATION, routeInfo.getLocations().get(inc).getElevation());
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.TIME, routeInfo.getLocations().get(inc).getTime());
				// uses inc for its sort order (so that they are ordered)
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.SORT, inc);
				
				// inserts the RouteNode into the database
				this._routeDatabase.getAndOpenWritableDatabase().insert(RouteDatabase.RouteNodeTableConstants.ROUTE_NODE_TABLE_NAME, null, routeNodeContentValues);
			}
		}
		
		return lastInsertedID;
		
	}
	
	public ArrayList<RouteInfo> getRoutes() {
		return this._routes;
	}
	
	public RouteInfo getRouteByID(long id) {
		for(int inc = 0; inc < this.getRoutes().size(); inc++) {
			if((long) this.getRoutes().get(inc).getId() == id) {
				return this.getRoutes().get(inc);
			}
		}
		return null;
	}
}
