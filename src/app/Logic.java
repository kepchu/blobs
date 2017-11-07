package app;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.DataController;
import view.ViewAndInputController;

public class Logic implements Runnable {

	private DataController dc;
	private ViewAndInputController v;
	private InputReceiver uir;

	private Object[] collisionsArray;
	private int currentColl;
	ScheduledExecutorService ses;

	private Object lock = new Object();
	
	public Logic(DataController dc, ViewAndInputController v) {

		this.dc = dc;
		this.v = v;
		
		collisionsArray = new Object[4];
		collisionsArray[0] = new ColDetAllignBordersonAtoBvec(dc.getListOfCollisionPoints(), dc.getTimeInterval());
		collisionsArray[1] = new ColDetDisabled(dc.getListOfCollisionPoints());
		collisionsArray[2] = new CoIDetInwardCol(dc.getListOfCollisionPoints());
		collisionsArray[3] = new ColDetDebris(dc.getListOfCollisionPoints());
		
		currentColl = 0;
		
		uir = new InputReceiver(dc, this);
		v.setUserInputReceiver(uir);
		init();
	}

	private void init() {
		ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(this, 16666, 16666, TimeUnit.MICROSECONDS);

		/*for (int i = 0; i < 4; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Game sleep loop");
		}*/

	}

	// the main loop
	@Override
	public void run() {
		v.update();//TODO: switch to swing timer
		dc.update();
		
		synchronized (lock) {
			dc.getListOfCollisionPoints().clear();
		}
		//testing - find collisions:
		((ColDetAllignBordersonAtoBvec)collisionsArray[currentColl]).detectCollisions(new ArrayList<Collidable>(dc.getBlobs()));
		
		//c.detectCollisions(dc.getBlobs().get(0), dc.getBlobs(), dc.getBlobs().get(0).getRadius());
		
		
		  //try { Thread.sleep(100); } catch (InterruptedException e) {e.printStackTrace(); }
		 
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
