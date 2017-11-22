package data;

import java.util.ArrayList;
import java.util.List;

import ColDet.CoIDetInwardCol;
import ColDet.ColDetDebris;
import ColDet.ColDetDisabled;
import ColDet.ColDetect;
import ColDet.Collidable;
import data.ChargePoint.Charger;
import data.Colour.ColourCategory;
import utils.U;
import utils.VecMath;


//TODO: make sure all mutations of core data happen only in "update" loop via buffering Lists to avoid concurrent access
public class World implements Runnable{
	//SETTINGS
	static int maxX = 8000;
	static int maxY = 0;
	static int minX = -4000;
	static int minY = -800;
	static int spanX = maxX - minX;
	static int spanY = maxY - minY;
	
	private ChargePoint pointer;
	private boolean repulseFromPointer;
	
	//static public int stageDeltaX = 0, stageDeltaY = 0;
	//static Vec gravity = new Vec(0.000001, 0.03);
	private static double gravity =  0.2;
	private static double gravityDelta = 1.02;
	private int groundLevel = maxY;	
	private static double timeInterval = 1.0; //amount of "app world" time between updates
	private double timeIntervalStep = 1.3;
	
	private List<Blob> newBlobs;//accessed from concurrent thread to avoid ConcurrentModificationE...
	private List <Blob> blobs;
	private List<ChargePoint> newCharges;
	private List<ChargePoint> charges;
	private List <Vec> listOfCollisionPoints;
	private static Vec stageMovementDelta;
	
	private FrameBuffer buffer;
	
	//TODO
	private Object[] collisionsArray;
	private int currentColl;
	private Vec stageCentre;
	private boolean gravityInCentre;
	private Vec newStageCentre;
	
	
	public World (int noOfBlobs) {
		System.out.println("World constr. thread - " + Thread.currentThread().getName());
		buffer = FrameBuffer.getInstance();
		
		newBlobs = new ArrayList<Blob>();
		blobs = new ArrayList<Blob> (noOfBlobs);
		newCharges = new ArrayList<ChargePoint>();
		charges = new ArrayList<ChargePoint>();
		newStageCentre = new Vec(400,400);
		stageCentre = new Vec(400, 400);
		
		pointer = new ChargePoint(new Vec(0,0), 1.0, ColourCategory.NEUTRAL, Charger.REPULSE_ALL);
		
		stageMovementDelta = new Vec(0,0);
		listOfCollisionPoints = new ArrayList<Vec>();
		
		currentColl = 0;
		collisionsArray = new Object[4];
		collisionsArray[0] = new ColDetect(getListOfCollisionPoints(), getTimeInterval());
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
			updateLoop();
		}
	}
	
	private void updateLoop() {

		Vec drag = new Vec(0, gravity);
		
		//0 - copy exposed data structures to create core data free of errors caused by mutations during computations; 
		blobs.addAll(newBlobs);		
		newBlobs.clear();
		
		charges.addAll(newCharges);
		newCharges.clear();
		
		stageCentre = new Vec(newStageCentre);
		
		// 1 a - update data in data structures
		for (Blob b : blobs) {
			// account for gravity
			if (gravityInCentre) {
				drag = VecMath.vecFromAtoB(b.getPosition(), stageCentre).
						setMagnitude(gravity);
			}
			// account for charges
			for (ChargePoint c : charges) {				
				c.charge(b);
			}
			if (repulseFromPointer) {
				//extra ChargePoint with position equal to mouse pointer position
				pointer.charge(b);
			}
			
			b.update(timeInterval, drag, stageMovementDelta);
		}
		
		// 1b - update world - collisions
		colDet();

		
		// 2 - update display buffer (this thread is put to sleep when buffer is full)
		buffer.addFrame(new FrameData(blobs, charges, listOfCollisionPoints, gravity, groundLevel, timeInterval));

		// 3 - clean-up
		stageMovementDelta.setXY(0, 0);// unfinished smooth movement of camera
		listOfCollisionPoints.clear();
	}
	
	private void colDet() {
		// testing - find collisions:
					((ColDetect) collisionsArray[currentColl])
							.detectCollisions(new ArrayList<Collidable>(getBlobs()));
//					 try { Thread.sleep(100); } catch (InterruptedException e)
//					 {e.printStackTrace(); }
	}
		
	//Control interface
	
	public void switchCollisonsDetect() {
		if (currentColl < collisionsArray.length - 1) {
			currentColl++;
		} else {
			currentColl = 0;
		}
		System.out.println("Col. det. set to " + collisionsArray[currentColl].getClass().getSimpleName());
	}
	public void switchChargeTypes() {
		Charger ch;
		for (ChargePoint c : charges) {
			ch = c.getType();
			
			if (ch.ordinal() == Charger.values().length-1)
				c.setType(Charger.values()[0]);
			else
				c.setType(Charger.values()[ch.ordinal()+1]);
		}
	}
	
	public void switchChargeColourCategories() {
		ColourCategory cc;		
		for (ChargePoint c : charges) {
			cc = c.getColourCategory();
			
			if (cc.ordinal() >= ColourCategory.values().length-2)// "-2" - eliminating NEUTRAL
				c.setColourCategory(ColourCategory.values()[0]);
			else
				c.setColourCategory(ColourCategory.values()[cc.ordinal()+1]);
		}
	}

	
		public void speedUp() {
			timeInterval *= timeIntervalStep;
			System.out.println("timeInterval: " + timeInterval);
		}
		public void speedDown() {
			timeInterval *= 1/timeIntervalStep;
			System.out.println("timeInterval: " + timeInterval);
		}
		
		public void gravityUp() {
			gravity *= gravityDelta;
			System.out.println("gravity: " + gravity);
		}
		public void gravityDown() {
			gravity /= gravityDelta;
			System.out.println("gravity: " + gravity);
		}
	
		
	public void addBlobAt(double x, double y) {
			newBlobs.add(new Blob (new Vec(x,y), U.rndInt(5, 200)));
	}
	
	public void addBlob(int i) {
		int c = 0;
		while (c++ < i) {
			addBlobAt((U.rndInt(minX, maxX)), U.rndInt(minY, maxY));
		}
	}
	
	public void addCharge(ChargePoint ch) {
		newCharges.add(ch);
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
	public static double getTimeInterval() {
		return timeInterval;
	}
	public void setTimeInterval(double timeInterval) {
		this.timeInterval = timeInterval;
	}
	public List<ChargePoint> getCharges() {
		return this.charges;
	}
	public void updatePointer(double x, double y) {
		pointer.getPosition().setXY(x, y);		
	}
	public void repulseFromPointer() {
		repulseFromPointer = !repulseFromPointer;
		System.out.println("repulseFromPointer(boolean b): " + repulseFromPointer);
	}
	public void switchGravity() {
		gravityInCentre = !gravityInCentre;
		System.out.println("gravity switched");
	}
	public void updateStageCentre(double x, double y) {
		newStageCentre.setXY(x,y);
	}
		
}
