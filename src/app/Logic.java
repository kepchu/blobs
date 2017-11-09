package app;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.FrameData;
import data.FrameBuffer;
import data.World;
import view.ViewAndInputController;

public class Logic implements Runnable {

	private World dc;
	private ViewAndInputController v;
	private InputReceiver uir;
	private FrameBuffer frameBuffer;
	ScheduledExecutorService ses;
	
	private Object[] collisionsArray;
	private int currentColl;
	
	Object lock = new Object();
	
	
	public Logic(World dc, ViewAndInputController v) {

		this.dc = dc;
		this.v = v;
		frameBuffer = FrameBuffer.getInstance();
		v.setFrameBuffer(frameBuffer);
		uir = new InputReceiver(dc, this);
		v.setUserInputReceiver(uir);
		
		ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(this, 16666, 16666, TimeUnit.MICROSECONDS);
		
		currentColl = 0;
		collisionsArray = new Object[4];
		collisionsArray[0] = new ColDetAllignBordersonAtoBvec(dc.getListOfCollisionPoints(), dc.getTimeInterval());
		collisionsArray[1] = new ColDetDisabled(dc.getListOfCollisionPoints());
		collisionsArray[2] = new CoIDetInwardCol(dc.getListOfCollisionPoints());
		collisionsArray[3] = new ColDetDebris(dc.getListOfCollisionPoints());
	}

	// the main loop
	@Override
	public void run() {
		
		dc.update();
		
		if (!frameBuffer.isUpdated()) {
			System.out.println("main loop: Waiting for DisplayBuffer to be populeted to draw ");
			dc.update();
			return;
		} else {
			
			v.update();// TODO: switch to swing timer
			
			// testing - find collisions:
			((ColDetAllignBordersonAtoBvec) collisionsArray[currentColl])
					.detectCollisions(new ArrayList<Collidable>(dc.getBlobs()));
			// try { Thread.sleep(100); } catch (InterruptedException e)
			// {e.printStackTrace(); }
		}
		
	}

	public void switchCollisonsDetect() {
		if (currentColl < collisionsArray.length - 1) {
			currentColl++;
		} else {
			currentColl = 0;
		}
		System.out.println("Col. det. set to " + collisionsArray[currentColl].getClass().getSimpleName());
	}
}
