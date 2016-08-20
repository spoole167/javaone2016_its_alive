package com.ibm.javaone2016.demo.furby.launcher;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Speaker implements LineListener {

	private Clip clip;
	private long[] offsets;
	private int ref=0;
	boolean stop=false;
	
	public Speaker(Clip audioClip, float[] marks) {
		this.clip=audioClip;
		clip.addLineListener(this);
		offsets=new long[marks.length];
		long last=0;
		for(float m:marks) {
			offsets[ref]=(long)(m*1000.0)-last;
			last=offsets[ref];
			ref++;
			
		}
		
		ref=0;
		
	}

	public void play() throws InterruptedException {
		
		long clipTime;
		
		while(ref<offsets.length) {
		
			clip.start();	
			Thread.sleep(offsets[ref]);
			ref++;
			clipTime= clip.getMicrosecondPosition();
			clip.stop();
			clip.setMicrosecondPosition(clipTime);
		
		}
		
		clip.close();
		
	
		
	}

	@Override
	public void update(LineEvent event) {

		System.out.println(System.nanoTime()+"/"+event.getType());
		
	}

}
