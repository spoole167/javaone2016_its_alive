package com.ibm.javaone2016.demo.furby.launcher;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.CommandCallback;
import com.ibm.iotf.client.device.DeviceClient;

public class FurbyCommandCallback implements CommandCallback {

	private DeviceClient client;
	public FurbyCommandCallback(DeviceClient c) {
		client=c;
	}
	@Override
	public void processCommand(Command arg0) {
		
		JsonObject event = new JsonObject();
		event.addProperty("instruction",arg0.toString());
		client.publishEvent("command", event);

	}

}
