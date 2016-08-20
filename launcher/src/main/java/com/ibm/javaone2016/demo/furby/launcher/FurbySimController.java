package com.ibm.javaone2016.demo.furby.launcher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.apache.commons.io.IOUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.ibm.iotf.client.device.DeviceClient;

public class FurbySimController extends AbstractFurbyController {

	FurbyState state=new FurbyState();
	
	
	public FurbySimController(DeviceClient client) {
		super(client);
	}

	@Override
	public void sleep() throws Exception {
		System.out.println("a sleep");
		sendASleep(state);
		
	}

	@Override
	public void wake() throws Exception {
		System.out.println("a wake");
		sendPositionEvent(state);
		
	}

	@Override
	public void say(String words) throws Exception {
		System.out.println("say "+words);
		// get words from service
		String url="http://text-to-furby.w3ibm.mybluemix.net/say?words="+words;
		Clip audioClip=getAudioClip(url);
		url="http://text-to-furby.w3ibm.mybluemix.net/marks?words="+words;
		float[] marks=getMarks(url);
		Speaker s=new Speaker(audioClip,marks);
		
		s.play();
		
		System.out.println("played");
		sendPositionEvent(state);
	}

	private float[] getMarks(String url) throws Exception {
		URL markURL=new URL(url);
		 
	String marks=IOUtils.toString(markURL,Charset.defaultCharset());
	JsonArray array = Json.parse(marks).asArray();
	float[] results=new float[array.size()];
	for(int i=0;i<results.length;i++) {
		results[i]=array.get(i).asFloat();
	}
	return results;
	}

	private Clip getAudioClip(String url) throws Exception {
		URL audio=new URL(url);
		InputStream in = audio.openStream();
		byte[] bytes = IOUtils.toByteArray(in);
		ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
		
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
		AudioFormat format = audioStream.getFormat();
		 System.out.println(format);
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		
		Clip audioClip = (Clip) AudioSystem.getLine(info);
		
		
		audioClip.open(audioStream);
		return audioClip;
	}

}
