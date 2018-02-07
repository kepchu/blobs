package app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.FrameBuffer;
import data.World;
import view.MAINViewAndInput;

public class MainHub {

	private MAINViewAndInput v;
	private InputReceiver uir;
	private FrameBuffer frameBuffer;
	ScheduledExecutorService ses;
	ScheduledExecutorService ses2;
	
	
	Object lock = new Object();
	
	
	public MainHub(World w, MAINViewAndInput v) {
		this.v = v;//run() has to be in scope
		
		frameBuffer = FrameBuffer.getInstance();
		v.setFrameBuffer(frameBuffer);
		
		uir = new InputReceiver(w, this);
		v.setInputReceiver(uir);
		
		// TODO: swing timers?
		ses = Executors.newSingleThreadScheduledExecutor();
		
		//ses.scheduleAtFixedRate(this, 16666, 16666, TimeUnit.MICROSECONDS);
		//ses.scheduleAtFixedRate(this, 4166, 4166, TimeUnit.MICROSECONDS);
		
		//ses.scheduleAtFixedRate(this, 16666666L, 16666666L, TimeUnit.NANOSECONDS);
		ses.scheduleAtFixedRate(v, 16666666, 16666666L, TimeUnit.NANOSECONDS);
		
		ses2 = Executors.newSingleThreadScheduledExecutor();
		ses2.scheduleAtFixedRate(w, 8333333, 4166666, TimeUnit.NANOSECONDS);
									 
		//new Thread(w).start();
	}

//	// the gfx loop
//	@Override
//	public void run() {			
////		if (frameBuffer.isEmpty()) 
////			System.out.println("main loop: display buffer empty");
//		v.update();
//	}
}
