package com.ibm.javaone2016.demo.furby.launcher;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;
import com.ibm.iotf.client.device.DeviceClient;

public class FurbyCommandCallback implements CommandCallback {

	private DeviceClient client;
	private FurbyController controller=new FurbyController(client);
	
	public FurbyCommandCallback(DeviceClient c) {
		client=c;
	}
	@Override
	public void processCommand(Command arg0) {
		
		try {
		String payload=arg0.getPayload();
		JsonObject object = Json.parse(payload).asObject();
		String cmd = object.get("cmd").asString();
		switch(cmd) {
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
		
		client.publishEvent("command", "done");
		
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

}
