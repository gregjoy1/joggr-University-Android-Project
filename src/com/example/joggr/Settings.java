package com.example.joggr;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	// constants
	private static final String USERNAME_KEY = "username";
	private static final String LOGGER_INCREMENTS_KEY = "logging-inc";
	private static final String AUTO_UPLOAD_KEY = "auto-upload";
	
	// declarations
	private String _username;
	private int _loggerIncrements;
	private boolean _autoUpload;
	private SharedPreferences _sharedPreferences;
	private SharedPreferences.Editor _editor;
	
	public Settings(Context context) {
		// sets context from constructor for shared preferences to use
		this._sharedPreferences = context.getSharedPreferences("JoggrPref", 0);
		this._editor = this._sharedPreferences.edit();
		
		// populates the model
		this.loadSettings();
	}

	// populates the attributes of the model from sharedpreferences
	public void loadSettings() {
		this._username = this._sharedPreferences.getString(USERNAME_KEY, "user");
		this._loggerIncrements = this._sharedPreferences.getInt(LOGGER_INCREMENTS_KEY, 5);
		this._autoUpload = this._sharedPreferences.getBoolean(AUTO_UPLOAD_KEY, false);
	}

	// saves the attributes of the model into shared preferences
	public void saveSettings() {
		this._editor.putString(USERNAME_KEY, this.getUsername());
		this._editor.putInt(LOGGER_INCREMENTS_KEY, this.getLoggingIncrements());
		this._editor.putBoolean(AUTO_UPLOAD_KEY, this.doesAppAutoUpload());
		this._editor.commit();
	}
	
	// standard getters and setters...
	
	public String getUsername() {
		return this._username;
	}
	
	public int getLoggingIncrements() {
		return this._loggerIncrements;
	}
	
	public boolean doesAppAutoUpload() {
		return this._autoUpload;
	}

	public void setUsername(String Username) {
		this._username = Username;
	}
	
	public void setLoggingIncrements(int increments) {
		this._loggerIncrements = increments;
	}
	
	public void setIfAppAutoUploads(boolean doesIt) {
		this._autoUpload = doesIt;
	}
	
}
