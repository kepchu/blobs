package app;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ColDet.CoIDetInwardCol;
import ColDet.ColDetect;
import ColDet.ColDetDebris;
import ColDet.ColDetDisabled;
import ColDet.Collidable;
import data.FrameData;
import data.FrameBuffer;
import data.World;
import view.ViewAndInputController;

public class Loop implements Runnable {

	private World dc;
	private ViewAndInputController v;
	private InputReceiver uir;
	private FrameBuffer frameBuffer;
	ScheduledExecutorService ses;
	
	
	
	Object lock = new Object();
	
	
	public Loop(World dc, ViewAndInputController v) {

		this.dc = dc;
		this.v = v;
		frameBuffer = FrameBuffer.getInstance();
		v.setFrameBuffer(frameBuffer);
		uir = new InputReceiver(dc, this);
		v.setUserInputReceiver(uir);
		
		//TODO: switch to swing timer
		ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(this, 16666, 8333, TimeUnit.MICROSECONDS);
		
		new Thread(dc).start();
	}

	// the gfx loop
	@Override
	public void run() {
		
		//dc.update();
		
		if (frameBuffer.isEmpty()) 
			System.out.println("main loop: DisplayBuffer empty");
		
		v.update();// TODO: switch to swing timer
					
	}
}
