package com.example.joggr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SocketHelper extends AsyncTask<String, Void, String>{

	// declarations
	private String _host = "stronach.mitchellfamily.org.uk";
	private int _port = 44365;
	private Socket _socket;
	private PrintWriter _printWriter;
	private RouteInfo _route;
	private String _username;
	private boolean _success;
	private Context _context;
	
	// sets the context for Toast to use later...
	public SocketHelper(Context context) {
		this._context = context;
	}
	
	@Override
	protected String doInBackground(String... params) {
		try {
			// instantiates socket and buffered writers
			this._socket = new Socket(this._host, this._port);
			this._printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this._socket.getOutputStream())), true);
			this._success = true;
			// generates xml and sends it to the server
			this._printWriter.println("STORE " + this._route.getXml(this._username));
			this._printWriter.flush();
			this._printWriter.close();
			this._socket.close();
			Log.d("JOGGR", "sent socket...");

		// didnt want any exceptions to kill the application
		// just inform the user it failed with a friendly Toast
		} catch (UnknownHostException e) {
			Log.d("JOGGR", e.getMessage());
			this._success = false;
//			e.printStackTrace();
		} catch (IOException e) {
			Log.d("JOGGR", e.getMessage());
//			e.printStackTrace();
			this._success = false;
		}

		// show toasts
		this.publishProgress();
		
		return null;
	}

	public void setRoute(RouteInfo route) {
		this._route = route;
	}
	
	public void setUsername(String username) {
		this._username = username;
	}
	
    @Override
    protected void onProgressUpdate(Void... values) {
    	if(this._success == true) {
    		Toast.makeText(this._context, "Upload successful...", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(this._context, "Upload failed...", Toast.LENGTH_SHORT).show();
    	}
    }
	
}
