package com.linuxcounter.lico_update_app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.linuxcounter.lico_update_app.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.LinearLayout;
import android.widget.TextView;

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
		
	    
	    
		String response = postData(getSysInfo.senddata);
		
		
		
	    myText.setText(response);
	    lView.addView(myText);
	}

	
	
	public String postData(String postdata) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://linuxcounter.net/api.php");
	    HttpResponse response = null;
	    String responseBody = "";

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("senddata", postdata));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        response = httpclient.execute(httppost);

	        int responseCode = response.getStatusLine().getStatusCode();
	        switch(responseCode) {
		        case 200:
			        HttpEntity entity = response.getEntity();
			            if(entity != null) {
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
