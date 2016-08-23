package com.ibm.javaone2016.demo.furby.launcher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.ibm.iotf.client.device.DeviceClient;

public class Launcher {

	public static void main(String args[]) throws Exception {

		Properties options =dialHome(args);

		// connect to IoT

		DeviceClient client = null;
		client = new DeviceClient(options);

		FurbyCommandCallback cmdHandler = new FurbyCommandCallback(client);

		Thread t = new Thread(cmdHandler);
		t.start();

		client.setCommandCallback(cmdHandler);

		// Connect to the IBM IoT Foundation
		client.setKeepAliveInterval(120);
		client.connect();

		// send info events once a minute

		JsonObject event = new JsonObject();
		event.addProperty("name", "foo");
		event.addProperty("cpu", 90);
		event.addProperty("mem", 70);

		while (true) {

			client.publishEvent("status", event, 0);
			Thread.sleep(60000);
		}

	}

	private static Properties dialHome(String[] args) throws SocketException, IOException, ClientProtocolException {
		String homeURL = System.getenv("HOME_URL");
		if (homeURL == null)
			homeURL = "http://trio.eu-gb.mybluemix.net/home";

		String serial = System.getenv("SERIAL");

		//
		// if serial is not provided then assume simulator mode
		//

		if (args != null && args.length > 0) {
			serial = args[0];
		}

		if (serial == null || serial.trim().equals("")) {
			serial = "simulator";
		}

		JsonObject jso = new JsonObject();
		jso.addProperty("serial", serial);

		JsonArray addrs = new JsonArray();
		jso.add("ip", addrs);

		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

		while (nis.hasMoreElements()) {
			NetworkInterface ni = nis.nextElement();
			Enumeration<InetAddress> inets = ni.getInetAddresses();
			while (inets.hasMoreElements()) {
				InetAddress ia = inets.nextElement();
				addrs.add(ia.getHostAddress());
			}
		}

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut request = new HttpPut(homeURL);
		StringEntity params = new StringEntity(jso.toString(), "UTF-8");
		params.setContentType("application/json");
		request.addHeader("content-type", "application/json");
		request.addHeader("Accept", "*/*");
		request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		request.addHeader("Accept-Language", "en-US,en;q=0.8");
		request.setEntity(params);

		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("rc " + response.getStatusLine().getStatusCode());
			System.exit(-1);
		}
		
		Properties options = new Properties();

		JsonReader br = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
		br.beginObject();
		while (br.hasNext()) {
			String name = br.nextName();
			options.put(name, br.nextString());
		}
		br.endObject();
		br.close();

		System.out.println("home says hello");
		return options;
	}
}
