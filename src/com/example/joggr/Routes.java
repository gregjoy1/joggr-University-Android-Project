package com.example.joggr;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Routes {

	private ArrayList<RouteInfo> _routes;
	private RouteDatabase _routeDatabase;
	
	public Routes(Context context) {
		this._routes = new ArrayList<RouteInfo>();
		 this._routeDatabase = new RouteDatabase(context);
		 this.loadRoutes();
	}
	
	public void loadRoutes() {

		Cursor routeCursor = this._routeDatabase.getAndOpenReadableDatabase().query(
				RouteDatabase.RouteTableConstants.ROUTE_TABLE_NAME, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null
		);
		
		for (routeCursor.moveToFirst(); !(routeCursor.isAfterLast()); routeCursor.moveToNext()){
			this._routes.add(this._createRouteInfoInstanceFromCursor(routeCursor));
		}
		
	}
	
	private RouteInfo _createRouteInfoInstanceFromCursor(Cursor cursor) {
		
		RouteInfo routeInfo = new RouteInfo();

		routeInfo.setId(cursor.getInt(0));
		routeInfo.setExternalId(cursor.getInt(1));

		Cursor routeNodeCursor = 
				this._routeDatabase.getAndOpenReadableDatabase().query(
					RouteDatabase.RouteNodeTableConstants.ROUTE_NODE_TABLE_NAME, 
					null, 
					RouteDatabase.RouteNodeTableConstants.ROUTE_ID + " = " + cursor.getInt(0), 
					null, 
					null, 
					null, 
					RouteDatabase.RouteNodeTableConstants.SORT + " ASC"
				);
		
		for (routeNodeCursor.moveToFirst(); !(routeNodeCursor.isAfterLast()); routeNodeCursor.moveToNext()){
			LatLng location = new LatLng(
					Double.parseDouble(routeNodeCursor.getString(2)), 
					Double.parseDouble(routeNodeCursor.getString(1))
				);
			
			routeInfo.addLocation(new RouteNode(location, routeNodeCursor.getLong(3)));
		}
		
		return routeInfo;
	}
	
	public long insertRoute(RouteInfo routeInfo) {
		
		long lastInsertedID = -1;
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(RouteDatabase.RouteTableConstants.EXTERNAL_ID, routeInfo.getExternalId());		
		contentValues.put(RouteDatabase.RouteTableConstants.DATE, new Date().getTime() * 1000);
		
		this._routeDatabase.getAndOpenWritableDatabase().insert(RouteDatabase.RouteTableConstants.ROUTE_TABLE_NAME, null, contentValues);
		
		Cursor lastInsertedIDCusor = this._routeDatabase.getAndOpenReadableDatabase().rawQuery("select _id from routes order by _id desc limit 1;", null);
		
		lastInsertedIDCusor.moveToFirst();
		
		if(!lastInsertedIDCusor.isAfterLast()) {
			
			lastInsertedID = lastInsertedIDCusor.getLong(0);
			
			for(int inc = 0; inc<routeInfo.getLocations().size(); inc++) {
				ContentValues routeNodeContentValues = new ContentValues();
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.ROUTE_ID, lastInsertedIDCusor.getLong(0));
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.LONGITUDE, routeInfo.getLocations().get(inc).getLocation().longitude);
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.LATITUDE, routeInfo.getLocations().get(inc).getLocation().latitude);
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.TIME, routeInfo.getLocations().get(inc).getTime());
				routeNodeContentValues.put(RouteDatabase.RouteNodeTableConstants.SORT, inc);
				
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
