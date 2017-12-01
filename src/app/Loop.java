package app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.FrameBuffer;
import data.World;
import view.ViewAndInputController;

public class Loop implements Runnable {

	private ViewAndInputController v;
	private InputReceiver uir;
	private FrameBuffer frameBuffer;
	ScheduledExecutorService ses;
	
	
	
	Object lock = new Object();
	
	
	public Loop(World w, ViewAndInputController v) {
		this.v = v;//run() has to be in scope
		
		frameBuffer = FrameBuffer.getInstance();
		v.setFrameBuffer(frameBuffer);
		
		uir = new InputReceiver(w, this);
		v.setUserInputReceiver(uir);
		
		// TODO: swing timers?
		ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(this, 16666, 16666, TimeUnit.MICROSECONDS);
		
		new Thread(w).start();
	}

	// the gfx loop
	@Override
	public void run() {			
		if (frameBuffer.isEmpty()) 
			System.out.println("main loop: display buffer empty");
		
		v.update();
	}
}
