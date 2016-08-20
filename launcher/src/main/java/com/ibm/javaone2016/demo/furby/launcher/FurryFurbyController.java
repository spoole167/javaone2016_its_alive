package com.ibm.javaone2016.demo.furby.launcher;

import java.util.concurrent.Future;

import com.ibm.iotf.client.device.DeviceClient;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class FurryFurbyController extends AbstractFurbyController {

	public static final Pin PULSE=RaspiPin.GPIO_01;
    public static final Pin AT_HOME=RaspiPin.GPIO_02;
    public static final Pin FORWARDS=RaspiPin.GPIO_05;
    public static final Pin BACKWARDS=RaspiPin.GPIO_04;
   private GpioController gpio;
   GpioPinDigitalOutput forwards; 
   GpioPinDigitalOutput backwards; 
   GpioPinDigitalOutput pulse;
   GpioPinDigitalInput atHome;
   
   FurbyState state=new FurbyState();
    
    int STEP=10;
    
    
    DeviceClient client=null;
   
    public FurryFurbyController(DeviceClient client) {
    	super(client);
    	gpio = GpioFactory.getInstance();
    	
    	// Pin to drive forwards
        forwards = gpio.provisionDigitalOutputPin(FORWARDS, "Forwards",
                        PinState.LOW);
        forwards.setShutdownOptions(true, PinState.LOW);

        // Pin to drive backwards
        backwards = gpio.provisionDigitalOutputPin(BACKWARDS, "Backwards",
                        PinState.LOW);
        backwards.setShutdownOptions(true, PinState.LOW);

        // create pulse pin
        pulse = gpio.provisionDigitalOutputPin(PULSE, "Pulse",
                        PinState.LOW);


        pulse.setShutdownOptions(true, PinState.LOW);
         atHome = gpio.provisionDigitalInputPin(AT_HOME, PinPullResistance.PULL_DOWN);
        atHome.setShutdownOptions(true);
        
        atHome.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

                    if(event.getState().isHigh()) {
                            // stop at home position.
                        state.position=0;
                        state.hitHome=true;
                    	sendPositionEvent(state);
                    

                    }


            }

		



    });



    }
    
	public void say(String words) {
		// TODO Auto-generated method stub
		
	}

	public void sleep() throws Exception{
		
		int pulsedist=1000-state.position;
		if(pulsedist<1) return;
		
		if(state.position>0 && state.position<1600) {
            forwards.high();
            backwards.low();
            Future<?> f=pulse.pulse(pulsedist);
            f.get();
            state.position=1000;
            sendASleep(state);
            }


		
	}


	public void wake() throws Exception {
		
		forwards.high();
        backwards.low();
        state.hitHome=false;
        int counter=0;
        while(!state.hitHome && counter<1000) {
        	Future<?> f=pulse.pulse(STEP);
        f.get();
        state.position+=STEP;
        counter++;
        System.out.println(counter+") now at counter="+state.position);
        }

        System.out.println("now at counter="+state.position);
       
        
		
	}

}
