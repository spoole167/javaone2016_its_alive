package com.ibm.javaone2016.demo.furby.launcher;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Time;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.ibm.iotf.client.device.DeviceClient;

public class Launcher {

	
	public static void main(String args[]) throws Exception {
			
		
		
		String homeURL=System.getenv("HOME_URL");
		if(homeURL==null) homeURL="http://trio.eu-gb.mybluemix.net/home";
				
		String serial=System.getenv("SERIAL");
		
		if(serial==null) serial="000000";
		
		if(args!=null && args.length>0) {
			serial=args[0];
		}
		
		URL home=new URL(homeURL+"?serial="+serial.trim());
		
		Properties options = new Properties();
		
		JsonObject obj=new JsonObject();
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
        client.setCommandCallback(new FurbyCommandCallback(client));

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
