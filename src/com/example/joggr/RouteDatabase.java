package com.example.joggr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

// database helper :)
public class RouteDatabase {

	static final String DATABASE_NAME = "Routes";
	static final int DATABASE_VERSION = 1;
	
	// constants for the Route entries (RouteInfo)
	public static class RouteTableConstants {
		static final String ID = "_id";
		static final String EXTERNAL_ID = "external_id";
		static final String DATE = "date";
		static final String TIMETAKEN = "time_taken";
		static final String NAME = "name";
		
		static final String ROUTE_TABLE_NAME = "routes";
		static final String CREATE_DB_TABLE = 
				" CREATE TABLE " + ROUTE_TABLE_NAME +
				" (" + 
					ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	   				EXTERNAL_ID + " INTEGER NOT NULL, " +
		   			DATE + " LONG NOT NULL," + 
		   			TIMETAKEN + " LONG NOT NULL," + 
		   			NAME + " TEXT NOT NULL" +
	   			");";
	};
	
	// constants for the Route Node Entries (RouteNode)
	public static class RouteNodeTableConstants {
		static final String ROUTE_ID = "route_id";
		static final String LONGITUDE = "longitude";
		static final String LATITUDE = "latitude";
		static final String ELEVATION = "elevation";
		static final String TIME = "time";
		static final String SORT = "sort";
		
		static final String ROUTE_NODE_TABLE_NAME = "routenodes";
		static final String CREATE_DB_TABLE = 
				" CREATE TABLE " + ROUTE_NODE_TABLE_NAME +
				" (" + 
					ROUTE_ID + " INTEGER, " + 
	   				LONGITUDE + " TEXT NOT NULL, " +
		   			LATITUDE + " TEXT NOT NULL," +
	   				ELEVATION + " DOUBLE NOT NULL," +
		   			TIME + " DATETIME NOT NULL," + 
		   			SORT + " TEXT NOT NULL" + 
	   			");";
	}
	
	private SQLiteDatabase _sqLiteDatabase;
	private SQLiteHelper _sqLiteHelper;
	private Context _context;

	// sets a context to help create the database
	public RouteDatabase(Context context) {
		this._context = context;
     }

	// returns the readible instance of the database to make queries on
    public SQLiteDatabase getAndOpenReadableDatabase() throws android.database.SQLException {
          this._sqLiteHelper = new SQLiteHelper(this._context, this.DATABASE_NAME, null, this.DATABASE_VERSION);
          return this.setDatabase(this._sqLiteHelper.getReadableDatabase());
     }

    // returns the writable instance of the database to make insertions on
    public SQLiteDatabase getAndOpenWritableDatabase() throws android.database.SQLException {
        this._sqLiteHelper = new SQLiteHelper(this._context, this.DATABASE_NAME, null, this.DATABASE_VERSION);
        return this.setDatabase(this._sqLiteHelper.getWritableDatabase());
    }

    public SQLiteDatabase setDatabase(SQLiteDatabase database) {
    	this._sqLiteDatabase = database;
    	return database;
    }
    
    public SQLiteDatabase getDatabase() {
    	return this._sqLiteDatabase;
    }
    
    public void close() {
    	this._sqLiteHelper.close();
    }

    // a little internal class to help out with the database
	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// if the database does not exist, it creates it.
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	db.execSQL(RouteTableConstants.CREATE_DB_TABLE);
	    	db.execSQL(RouteNodeTableConstants.CREATE_DB_TABLE);
	    }

	    // if it needs an update, it just drops the database and re-creates the tables
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + RouteTableConstants.ROUTE_TABLE_NAME);
			db.execSQL(RouteTableConstants.CREATE_DB_TABLE);
			
			db.execSQL("DROP TABLE IF EXISTS " + RouteNodeTableConstants.ROUTE_NODE_TABLE_NAME);
			db.execSQL(RouteNodeTableConstants.CREATE_DB_TABLE);
		}

	}
	
}
