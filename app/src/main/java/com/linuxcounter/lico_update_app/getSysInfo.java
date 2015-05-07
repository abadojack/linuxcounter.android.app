package com.linuxcounter.lico_update_app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.linuxcounter.lico_update_app.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class getSysInfo extends Activity implements OnClickListener {

	public String sAppVersion = "0.0.4";
	
	static String senddata = null;
	
	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "SdCardPath" })
	protected void onCreate(Bundle savedInstanceState) {
		String toks[] = null;
		String MemTotalt = null;
		int MemTotal = 0;
		String MemFreet = null;
		int MemFree = 0;
		String SwapTotalt = null;
		int SwapTotal = 0;
		String SwapFreet = null;
		int SwapFree = 0;
		String cpumodel = null;
		String scpunum = null;
		int cpunum = 0;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_sys_info);
		
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);
		
	    LinearLayout lView = (LinearLayout)findViewById(R.id.mylinearlayout);
	    TextView myText = new TextView(this);
	    
	    System.getProperty("user.region");
	    
	    String androidversion;
	    androidversion = System.getProperty("http.agent").replaceAll(".*Android *([0-9.]+).*", "$1");
	    
	    System.getProperty("os.version");
	    
	    Point size = new Point();
	    WindowManager w = getWindowManager();
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
          w.getDefaultDisplay().getSize(size); 
        }else{
          Display d = w.getDefaultDisplay(); 
          d.getWidth(); 
          d.getHeight(); 
        }
	    
        String loadavg = "";
	    try {
	    	BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( "/proc/loadavg" ) ), 1000 );
	        String[] temp = reader.readLine().split(" ");
	        loadavg = temp[0]+" "+temp[1]+" "+temp[2];
	        reader.close();
	    } catch(IOException ex) {
	    	ex.printStackTrace();
	    }
	    String cpuinfo = "";
		try {
			cpuinfo = getStringFromFile("/proc/cpuinfo").replace("\r", "").replace("\n\n", "\n");
			toks = cpuinfo.split("\n");
			for (int a = 0; a<toks.length; a++) {
			   String k = (String) toks[a];
			   String[] toks2 = k.split(":");
			   if (toks2[0].trim().matches("^Processor.*") && toks2[1].trim().matches("^[a-zA-Z]+.*")) {
				   cpumodel = toks2[1].trim();
			   } else if (toks2[0].trim().matches("^processor.*") && toks2[1].trim().matches("^[0-9]+")) {
				   scpunum = toks2[1].trim();
			   }
			}
			try {
			    cpunum = Integer.parseInt(scpunum);
			    cpunum++;
			} catch(NumberFormatException nfe) {
			   System.out.println("Could not parse " + nfe);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    String flags = "";
		try {
			flags = getStringFromFile("/proc/cpuinfo").replace("\r", "").replace("\n\n", "\n");
			toks = flags.split("\n");
			for (int a = 0; a<toks.length; a++) {
			   String k = (String) toks[a];
			   String[] toks2 = k.split(":");
			   if (toks2[0].trim().matches("^Features.*") && toks2[1].trim().matches("^[a-zA-Z]+.*")) {
				   flags = toks2[1].trim();
			   }
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    String meminfo = "";
		try {
			meminfo = getStringFromFile("/proc/meminfo");
			toks = meminfo.split("\n");
			for (int a = 0; a<toks.length; a++) {
			   String k = (String) toks[a];
			   String[] toks2 = k.split(":");
			   if (toks2[0].trim().matches(".*MemTotal.*")) {
				   MemTotalt = toks2[1].replace(" kB", "").trim();
				   MemTotal = Integer.parseInt(MemTotalt.toString());
//				   MemTotal = (MemTotal * 1024);
			   } else if (toks2[0].trim().matches(".*MemFree.*")) {
				   MemFreet = toks2[1].replace(" kB", "").trim();
				   MemFree = Integer.parseInt(MemFreet.toString());
//				   MemFree = (MemFree * 1024);
			   } else if (toks2[0].trim().matches(".*SwapTotal.*")) {
				   SwapTotalt = toks2[1].replace(" kB", "").trim();
				   SwapTotal = Integer.parseInt(SwapTotalt.toString());
//				   SwapTotal = (SwapTotal * 1024);
			   } else if (toks2[0].trim().matches(".*SwapFree.*")) {
				   SwapFreet = toks2[1].replace(" kB", "").trim();
				   SwapFree = Integer.parseInt(SwapFreet.toString());
//				   SwapFree = (SwapFree * 1024);
			   }
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		long total = 0;
		long avail = 0;
		File dir = null;
		boolean extexists = false;
		
		dir = new File("/mnt/sdcard/external_sd/");
		if(dir.exists() && (dir.isDirectory() || dir.isFile())) {
			total = TotalMemoryOfDir("/mnt/sdcard/external_sd/");
			avail = FreeMemoryOfDir("/mnt/sdcard/external_sd/");
			extexists = true;
		}
		dir = new File("/mnt/extSdCard/");
		if(dir.exists() && (dir.isDirectory() || dir.isFile())) {
			total = TotalMemoryOfDir("/mnt/extSdCard/");
			avail = FreeMemoryOfDir("/mnt/extSdCard/");
			extexists = true;
		}
		String sdpath = Environment.getExternalStorageDirectory().getPath();
		dir = new File(sdpath);
		if(dir.exists() && (dir.isDirectory() || dir.isFile())) {
			total = TotalMemoryOfDir(sdpath);
			avail = FreeMemoryOfDir(sdpath);
			extexists = true;
		}
		
		long disktotal = 0;
		if (extexists == true) {
			disktotal = TotalMemory() + total;
		} else {
			disktotal = TotalMemory();
		}
		
		long freedisk = 0;
		if (extexists == true) {
			freedisk = FreeMemory() + avail;
		} else {
			freedisk = FreeMemory();
		}
		
		String hostname = "localhost";
		String filename = ".linuxcounter";
		String filepath = Environment.getExternalStorageDirectory()+ "/data/com.linuxcounter.lico_update_app";
		File readFile = new File(filepath, filename);
        String counter_number = "";
        String machine_number = "";
        String update_key = "";
		String load = "";
		try {
	    	BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(readFile) ), 1000 );
	        load = reader.readLine();
	        reader.close();
	        String[] toks1 = load.split(" ");
	        counter_number = (String)toks1[0];
	        machine_number = (String)toks1[1];
	        update_key = (String)toks1[2];
		} catch(Exception e1) {
			// Do nothing
		}
		
		String machine = System.getProperty("os.arch");
		String version = "";
		try {
			version = Command("uname -r").trim();
		} catch (Exception e1) {
			try {
				version = getStringFromFile("/proc/sys/kernel/osrelease");
			} catch (Exception e2) {
				version = "unknown";
			}
		}
		
		String uptime = Command("uptime").trim();
		String[] toks2 = uptime.split(", ");
		uptime = toks2[0].replace("up time: ", "").trim();
		String cpufreqt = "0";
		try {
			cpufreqt = getStringFromFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq").trim();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Float cpufreq = Float.parseFloat(cpufreqt);
		cpufreq = (cpufreq / 1024);
		
		senddata = 
				"lico-update.app version " + sAppVersion + "##" +
				hostname + "##" +
				counter_number + "##" +
				machine_number + "##" +
				cpumodel + "##" +
				cpunum + "##" +
				"1" + "##" +
				disktotal + "##" +
				MemTotal + "##" +
				"1" + "##" +
				freedisk + "##" +
				MemFree + "##" +
				SwapTotal + "##" +
				SwapFree + "##" +
				flags + "##" +
				machine + "##" +
				version + "##" +
				uptime + "##" +
				loadavg + "##" +
				cpufreq + "##" +
				"Android" + "##" +
				androidversion + "##" +
				"online" + "##" +
				update_key;
				
	    String sysinformation = 
	    		"script : lico-update app " + sAppVersion + "\n" +
	    	    "hostname : " + hostname + "\n" +
	    		"counter_number : " + counter_number + "\n" +
	    		"machine_number : " + machine_number + "\n" +
	    		"processor : " + cpumodel + "\n" +
	    		"cpunum : " + cpunum + "\n" +
	    		"accounts : 1\n" + 
	    		"totaldisk : " + disktotal + "\n" +
	    		"totalram : " + MemTotal + "\n" +
	    		"numusers : 1\n" +
	    		"freedisk : " + freedisk + "\n" +
	    		"freeram : " + MemFree + "\n" +
	    		"totalswap : " + SwapTotal + "\n" +
	    		"freeswap : " + SwapFree + "\n" +
	    		"flags : " + flags + "\n" +
	    		"machine : " + machine + "\n" +
	    		"version : " + version + "\n" +
	    		"uptime : " + uptime + "\n" +
	    		"load : " + loadavg + "\n" +
	    		"cpufreq : " + cpufreq + "\n" +
	    		"distribution : Android\n" +
	    		"distribversion : " + androidversion + "\n" +
	    		"online : online\n" +
	    		"update_key : " + update_key;
	    
	    myText.setText(sysinformation);
	    
	    lView.addView(myText);
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
	    //Make sure you close all streams.
	    fin.close();        
	    return ret;
	}

    public long TotalMemory() {
    	StatFs statFs = null;
    	statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
    	long totalBlocks = (long) statFs.getBlockCount();
    	long blockSize = (long) statFs.getBlockSize();
    	long Total = ((((long)totalBlocks * (long)blockSize)) / 1024);
        return Total;
    }

    public long FreeMemory() {
    	StatFs statFs = null;
    	statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
    	long availBlocks = (long) statFs.getAvailableBlocks();
    	long blockSize = (long) statFs.getBlockSize();
    	long free_memory = ((((long)availBlocks * (long)blockSize)) / 1024);
        return free_memory;
    }
    
    public long TotalMemoryOfDir(String dir) {
    	StatFs statFs = null;
    	statFs = new StatFs(dir);
    	long totalBlocks = (long) statFs.getBlockCount();
    	long blockSize = (long) statFs.getBlockSize();
    	long Total = ((((long)totalBlocks * (long)blockSize)) / 1024);
        return Total;
    }

    public long FreeMemoryOfDir(String dir) {
    	StatFs statFs = null;
    	statFs = new StatFs(dir);
    	long availBlocks = (long) statFs.getAvailableBlocks();
    	long blockSize = (long) statFs.getBlockSize();
    	long free_memory = ((((long)availBlocks * (long)blockSize)) / 1024);
        return free_memory;
    }
    
    public String Command(String command) {
    	try {
    	    Process process = Runtime.getRuntime().exec(command);
    	    BufferedReader reader = new BufferedReader(
    	    new InputStreamReader(process.getInputStream()));
    	    int read;
    	    char[] buffer = new char[4096];
    	    StringBuffer output = new StringBuffer();
    	    while ((read = reader.read(buffer)) > 0) {
    	        output.append(buffer, 0, read);
    	    }
    	    reader.close();
    	    process.waitFor();
    	    return output.toString();
    	} catch (IOException e) {
    	    throw new RuntimeException(e);
    	} catch (InterruptedException e) {
    	    throw new RuntimeException(e);
    	}
    }
    
	public void onClick(View v) {
		startActivity(new Intent(this,sendSysInfo.class));
		finish();
	}
	
}
