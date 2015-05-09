package com.linuxcounter.lico_update_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sendSysInfo extends Activity {

	final String TAG = "MyDebugOutput";
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_sys_info);
	    LinearLayout lView = (LinearLayout)findViewById(R.id.mylinearlayout2);
	    TextView myText = new TextView(this);
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy); 
		String response = postData(getSysInfo.aSendData);
	    myText.setText(response);
	    lView.addView(myText);
	}



	public String postData(String postdata[]) {
		Log.i(TAG, "sendSysInfo: start postData()...");
		String responseBody = "";
		String[] firstseparated = postdata[0].split("#");
		String url = firstseparated[1];
		String[] secseparated = postdata[1].split("#");
		String machine_id = secseparated[1];
		String[] thirdseparated = postdata[2].split("#");
		String machine_updatekey = thirdseparated[1];

		String data = null;
		String contentType;
		contentType = "application/x-www-form-urlencoded";
		Log.i(TAG, "sendSysInfo: start DefaultHttpClient()...");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPatch httpPatch= new HttpPatch(url);
		httpPatch.setHeader("x-lico-machine-updatekey", machine_updatekey);
		httpPatch.setHeader("Content-Type", contentType);
		httpPatch.setHeader("Accept", "application/json");
		httpPatch.setHeader("Accept-Charset", "utf-8");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			for (int i = 0; i < postdata.length; i++) {
				String[] separated = postdata[i].split("#");
				Log.i(TAG, "sendSysInfo: POST data:  "+separated[0]+"="+separated[1]);
				nameValuePairs.add(new BasicNameValuePair(separated[0], separated[1]));
			}
			httpPatch.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP PATCH Request
			Log.i(TAG, "sendSysInfo: httpClient.execute(httpPatch)");
			HttpResponse response = httpClient.execute(httpPatch);
			int responseCode = response.getStatusLine().getStatusCode();
			switch (responseCode) {
				case 200:
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						responseBody = EntityUtils.toString(entity);
					}
					break;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return responseBody;
	}
}
