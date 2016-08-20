package com.ibm.javaone2016.demo.furby.launcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

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
		
		BufferedReader br=new BufferedReader(new InputStreamReader(home.openStream()));
		String data=br.readLine();
		br.close();
		
		
		System.out.println("home says "+data);
		
		
		
		
		
		
	}
}
