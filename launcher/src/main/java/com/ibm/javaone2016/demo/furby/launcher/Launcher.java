package com.ibm.javaone2016.demo.furby.launcher;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.ibm.iotf.client.device.DeviceClient;

public class Launcher {

	
	public static void main(String args[]) throws Exception {
			
		
		
		String homeURL=System.getenv("HOME_URL");
		if(homeURL==null) homeURL="http://trio.eu-gb.mybluemix.net/home";
				
		String serial=System.getenv("SERIAL");
		String ipaddress=InetAddress.getLocalHost().getHostAddress();
		
		//
		// if serial is not provided then assume simulator mode
		//
		

		if(args!=null && args.length>0) {
			serial=args[0];
		}
		
		if(serial==null || serial.trim().equals("")) {
			serial="simulator";
		}
		
		
		URL home=new URL(homeURL+"?serial="+serial.trim()+"&ip="+ipaddress);
		Properties options = new Properties();
		
		JsonReader br=new JsonReader(new InputStreamReader(home.openStream()));
		br.beginObject();
	    while (br.hasNext()) {
	    	String name = br.nextName();
	    	options.put(name,br.nextString());
	    }
	    br.endObject();
	    br.close();
	    
	    System.out.println("home says hello");
		
	    // connect to IoT
		
        DeviceClient client = null;
        client = new DeviceClient(options);
        
        FurbyCommandCallback cmdHandler=new FurbyCommandCallback(client);
        
        Thread t=new Thread(cmdHandler);
        t.start();
        
        client.setCommandCallback(cmdHandler);
        

        //Connect to the IBM IoT Foundation
        client.setKeepAliveInterval(120);
        client.connect();
		
        // send info events once a minute
        
        
        JsonObject event = new JsonObject();
        event.addProperty("name", "foo");
        event.addProperty("cpu",  90);
        event.addProperty("mem",  70);

        while(true) {
        
        client.publishEvent("status", event, 0);
        Thread.sleep(60000);
        }
		
		
	}
}
