package com.ibm.javaone2016.demo.furby.launcher;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.DeviceClient;

public abstract class AbstractFurbyController implements IFurbyController{

	private DeviceClient client;
	
	public AbstractFurbyController(DeviceClient client) {
		this.client=client;
	}
	protected void sendPositionEvent(FurbyState state) {
	
	 JsonObject data = new JsonObject();
	   data.addProperty("home", state.hitHome);
	   data.addProperty("position", state.position);
	   client.publishEvent("position", data, 0);
	
		
	}

	protected void sendASleep(FurbyState state) {
		 JsonObject data = new JsonObject();
	     data.addProperty("home",state.hitHome);
	     data.addProperty("position", state.position);
	     client.publishEvent("sleep", data, 0);
	
		
	}

}
