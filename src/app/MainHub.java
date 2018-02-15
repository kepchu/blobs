package app;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.FrameBuffer;
import data.FrameData;
import data.World;
import view.MAINViewAndInput;

public class MainHub {

	private MAINViewAndInput v;
	private InputReceiver uir;
	private FrameBuffer frameBuffer;
	ScheduledExecutorService ses;
	ScheduledExecutorService ses2;
	
	
	Object lock = new Object();
	
	
	public MainHub(World w, DiscAccess da, MAINViewAndInput v) {
		this.v = v;//run() has to be in scope
		
		frameBuffer = FrameBuffer.getInstance();
		v.setFrameBuffer(frameBuffer);
		
		uir = new InputReceiver(w, da, this);
		v.setInputReceiver(uir);
		
		//schedule repeated execution gfx/input 60 times per second
		ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(v, 16666666, 16666666L, TimeUnit.NANOSECONDS);
		
		//schedule execution of "back-end" to 240 times per second BUT this thread
		// is being stopped/synchronised by gfx thread in FrameBuffer.addFrame(FrameData data)
		ses2 = Executors.newSingleThreadScheduledExecutor();
		ses2.scheduleAtFixedRate(w, 8333333, 4166666, TimeUnit.NANOSECONDS);
	}
}
