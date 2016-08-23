package com.ibm.javaone2016.demo.furby.launcher;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;
import com.ibm.iotf.client.device.DeviceClient;

public class FurbyCommandCallback implements CommandCallback,Runnable {

	
	private BlockingQueue<Command> queue = new LinkedBlockingQueue<Command>();

    /**
     * 
     */
	private DeviceClient client;
	private IFurbyController controller=null;
	
	public FurbyCommandCallback(DeviceClient c) {
		client=c;
		if(client.getDeviceId().equals("simulator")) {
			controller=new FurbySimController(client);
		}
		else {
		
		controller=new FurryFurbyController(client);
		
		}
	}
	
	@Override
	public void processCommand(Command cmd) {
	
		 try {
             queue.put(cmd);
             } catch (InterruptedException e) {
     }
		
	}
	
	@Override
    public void run() {
		
		 while(true) {
			 
			 Command cmd = null;
             try {
                     cmd = queue.take();
                     handleCommand(cmd);
             } catch (InterruptedException e) {}
			
		 }
	}
	
	private void handleCommand(Command arg0) {
		try {
		String payload=arg0.getPayload();
		JsonObject object = Json.parse(payload).asObject();
		String cmd = object.get("cmd").asString();
		switch(cmd) {
		case "reboot" :
				System.exit(50);
		case "quit" :
			System.exit(0);		
		case "say" :
			controller.say(object.get("text").asString());
			break;
		case "sleep" :
			controller.sleep();
			break;
		case "wake" :
			controller.wake();
			break;
		}
		
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
