package data;

import java.util.ArrayList;
import java.util.List;

import ColDet.CoIDetInwardCol;
import ColDet.ColDetAllignBordersonAtoBvec;
import ColDet.ColDetDebris;
import ColDet.ColDetDisabled;
import ColDet.Collidable;
import utils.U;
import utils.VecMath;

public class World implements Runnable{
	//SETTINGS
	static int maxX = 2000;
	static int maxY = 0;
	static int minX = 0;
	static int minY = -800;
	static int spanX = maxX - minX;
	static int spanY = maxY - minY;
	//static public int stageDeltaX = 0, stageDeltaY = 0;
	//static Vec gravity = new Vec(0.000001, 0.03);
	static Vec gravity = new Vec(0.00000, 0.1);
	static double gravityDelta = 1.02;
	int groundLevel = maxY;	
	double timeInterval = 1.0; //amount of "app world" time between updates
	double timeIntervalStep = 1.3;
	
	private List <Blob> blobs;
	private List<ChargePoint> charges;
	private List <Vec> listOfCollisionPoints;
	static Vec stageMovementDelta;
	
	FrameBuffer buffer;
	
	private Object[] collisionsArray;
	private int currentColl;
	
	
	public World (int noOfBlobs) {
		System.out.println("World constr. thread - " + Thread.currentThread().getName());
		buffer = FrameBuffer.getInstance();
		
		blobs = new ArrayList<Blob> (noOfBlobs);
		charges = new ArrayList<ChargePoint>();
		
		stageMovementDelta = new Vec(0,0);
		listOfCollisionPoints = new ArrayList<Vec>();
		
		currentColl = 0;
		collisionsArray = new Object[4];
		collisionsArray[0] = new ColDetAllignBordersonAtoBvec(getListOfCollisionPoints(), getTimeInterval());
		collisionsArray[1] = new ColDetDisabled(getListOfCollisionPoints());
		collisionsArray[2] = new CoIDetInwardCol(getListOfCollisionPoints());
		collisionsArray[3] = new ColDetDebris(getListOfCollisionPoints());
		//initData(noOfBlobs);
	}
	public World() {
		this(8);
	}
	
	public void initData(int noOfBlobs) {	
		blobs.add (new Blob(new Vec (400, -400), 22));
		blobs.add (new Blob(new Vec (400, -700), new Vec(30,-1),20));
		for (int i = 0; i < noOfBlobs; i++) {
			blobs.add(new Blob(
					new Vec(U.rndInt(100, 700), U.rndInt(10, -1000)),
					U.rndInt(5, 200)));
		}
	}
	
	public void run() {
		System.out.println("World.run() thread - " + Thread.currentThread().getName());
		while (true) {
			update();
		}
	}
	
	public void update() {
		
//		//TODO: after testing move this check below update of the world
//		if (buffer.isFull()) {
//			System.out.println("World.update(): Display buffer full. Waiting.");
//			return;
//		} else {
			// 1 a - update world
			for (Blob b : blobs) {
				// account for gravity
				Vec drag = new Vec(gravity);
				// account for charges
				for (ChargePoint c : charges) {
					Vec chargeInfluence = VecMath.vecFromAtoB(b.getPosition(), c.getPosition());
					chargeInfluence.setMagnitude(c.getPower());

					drag.addAndSet(chargeInfluence);
				}
				b.update(timeInterval, drag, stageMovementDelta);
			}
			//1b - update world - collisions
			colDet();
			
			// 2 - update display buffer
			buffer.addFrame(new FrameData(blobs, charges, listOfCollisionPoints, gravity,
					groundLevel, timeInterval));
			
			stageMovementDelta.setXY(0, 0);//unfinished smooth movement of camera
			listOfCollisionPoints.clear();
		

	}
	private void colDet() {
		// testing - find collisions:
					((ColDetAllignBordersonAtoBvec) collisionsArray[currentColl])
							.detectCollisions(new ArrayList<Collidable>(getBlobs()));
//					 try { Thread.sleep(100); } catch (InterruptedException e)
//					 {e.printStackTrace(); }
	}
	
	public void switchCollisonsDetect() {
		if (currentColl < collisionsArray.length - 1) {
			currentColl++;
		} else {
			currentColl = 0;
		}
		System.out.println("Col. det. set to " + collisionsArray[currentColl].getClass().getSimpleName());
	}
	
	
	//Control interface
		public void speedUp() {
			timeInterval *= timeIntervalStep;
			System.out.println("timeInterval: " + timeInterval);
		}
		public void speedDown() {
			timeInterval *= 1/timeIntervalStep;
			System.out.println("timeInterval: " + timeInterval);
		}
		
		public void gravityUp() {
			gravity.multiplyAndSet(gravityDelta);
			System.out.println("gravity: " + gravity);
		}
		public void gravityDown() {
			gravity.multiplyAndSet(1/gravityDelta);
			System.out.println("gravity: " + gravity);
		}
	
	public void addBlobAt(double x, double y) {
		blobs.add(new Blob (new Vec(x,y), U.rndInt(5, 200)));
	}
	public synchronized void addBlob() {
		blobs.add(new Blob(
				new Vec(U.rndInt(100, 700), U.rndInt(10, -1000)),
				U.rndInt(5, 200)));
	}
	
	public void addCharge(double x, double y) {
		charges.add(new ChargePoint(new Vec(x,y)));
		System.out.println("length of charges: " + charges.size());
	}
	
	//GETTERS & SETTERS
	public List<Blob> getBlobs() {
		return blobs;
	}
	public void setBlobs(List<Blob> blobs) {
		this.blobs = blobs;
	}
	public int getGround() {
		return groundLevel;
	}
	public void setGround(int ground) {
		this.groundLevel = ground;
	}
	public List<Vec> getListOfCollisionPoints() {
		return listOfCollisionPoints;
	}	
	
	public static synchronized int getMaxX() {
		return maxX;
	}

	public static synchronized void setMaxX(int maxX) {
		World.maxX = maxX;
	}

	public static synchronized int getMaxY() {
		return World.maxY;
	}

	public static synchronized void setMaxY(int maxY) {
		World.maxY = maxY;
	}

	public static synchronized int getMinX() {
		return World.minX;
	}

	public static synchronized void setMinX(int minX) {
		World.minX = minX;
	}

	public static synchronized int getMinY() {
		return World.minY;
	}

	public static synchronized void setMinY(int minY) {
		World.minY = minY;
	}
	public static Vec getStageMovementDelta() {
		return stageMovementDelta;
	}
	public static void setStageMovementDelta(Vec stageMovementDelta) {
		World.stageMovementDelta = stageMovementDelta;
	}
	public double getTimeInterval() {
		return timeInterval;
	}
	public void setTimeInterval(double timeInterval) {
		this.timeInterval = timeInterval;
	}
	public List<ChargePoint> getCharges() {
		return this.charges;
	}
	
}
