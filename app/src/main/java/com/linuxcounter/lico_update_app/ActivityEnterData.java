package com.linuxcounter.lico_update_app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.linuxcounter.lico_update_app.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews.ActionException;
import android.widget.Toast;

public class ActivityEnterData extends Activity implements OnClickListener {

	final String TAG = "MyDebugOutput";

	protected backgroundService service;
	protected backgroundServiceConnection serviceConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enterdata);
		Button button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(this);

		Log.v(TAG, "starting enterData...");

		String filename = ".linuxcounter";
		String filepath = Environment.getExternalStorageDirectory()
				+ "/data/com.linuxcounter.lico_update_app";
		File readFile = new File(filepath, filename);
		String load = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(readFile)), 1000);
			load = reader.readLine();
			reader.close();
			String[] toks = load.split(" ");
			EditText myText1 = (EditText) this.findViewById(R.id.editText1);
			myText1.setText(toks[0]);
			EditText myText2 = (EditText) this.findViewById(R.id.editText2);
			myText2.setText(toks[1]);
			EditText myText3 = (EditText) this.findViewById(R.id.editText3);
			myText3.setText(toks[2]);
		} catch (Exception e1) {
			// Do nothing
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		EditText myText1 = (EditText) this.findViewById(R.id.editText1);
		String counternum = myText1.getText().toString();
		EditText myText2 = (EditText) this.findViewById(R.id.editText2);
		String machinenum = myText2.getText().toString();
		EditText myText3 = (EditText) this.findViewById(R.id.editText3);
		String updatekey = myText3.getText().toString();
		SaveToFile(counternum + " " + machinenum + " " + updatekey + "\n");

		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnected();
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnected();

		Log.v(TAG, "Connection 3G: " + is3g + " | Connection wifi: " + isWifi);
		if (!is3g && !isWifi) {
			Toast.makeText(getApplicationContext(),
					"Please make sure, your network connection is ON ",
					Toast.LENGTH_LONG).show();
		} else {
			if (updatekey != "" && machinenum != "") {
				connectToService();
			}
			startActivity(new Intent(this, getSysInfo.class));
			finish();
		}
	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	public static String getStringFromFile(String filePath) throws Exception {
		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}

	public void SaveToFile(String thisstring) {
		final String TAG = "MyDebugOutput";
		String filename = ".linuxcounter";
		String filepath = Environment.getExternalStorageDirectory()
				+ "/data/com.linuxcounter.lico_update_app";
		File file = new File(filepath);
		file.mkdirs();
		File writeFile = new File(filepath, filename);
		if (thisstring != "") {
			try {
				FileWriter filewriter = new FileWriter(writeFile);
				BufferedWriter out = new BufferedWriter(filewriter);
				out.write(thisstring + "\n");
				out.close();
			} catch (IOException e) {
				Log.e(TAG, "Could not write file " + e.getMessage());
			}
		}
	}

	// Helper function for connecting to backgroundService.
	private void connectToService() {
		// Calling startService() first prevents it from being killed on
		Log.v(TAG, "Starte Service für backgroundService.class...");
		startService(new Intent(this, backgroundService.class));

		// Now connect to it
		Log.v(TAG, "Connecte zu backgroundService...");
		serviceConnection = new backgroundServiceConnection();

		boolean result = bindService(new Intent(this, backgroundService.class),
				serviceConnection, BIND_AUTO_CREATE);

		if (!result) {
			throw new RuntimeException("Unable to bind with service.");
		}
	}

	protected class backgroundServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			Log.i(TAG, "onServiceConnected backgroundService");
			service = ((backgroundService.LocalBinder) binder).getService();
			callServiceFunction();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "onServiceDisconnected backgroundService");
			service = null;
		}

	}

	protected void callServiceFunction() throws ActionException {
		Log.i(TAG, "Starte service.doTheBackgroundWork()...");
		service.doTheBackgroundWork();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}
}
