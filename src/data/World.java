package data;

import java.util.ArrayList;
import java.util.List;

import collisions.ProcessorOfCollisions;
import collisions.ColFlag;
import collisions.Collidable;
import data.ChargePoint.Charger;
import data.Colour.ColourCategory;
import data.Command.Com;
import utils.U;


//TODO: make sure all mutations of core data happen only in "update" loop via buffering Lists to avoid concurrent access
public class World implements Runnable{
	
	private final Object LOCK = new Object();
	//SETTINGS
	private static volatile int maxX = 2000;
	private static volatile int minX = -1000;
	private static volatile int maxY = 0;	
	private static volatile int minY = -2000;
	
	private double defaultRadiusMultiplier = 1.0;
	private static double gravity =  0.2;
	private static double gravityDelta = 1.02;
	private static double timeInterval = 1.0; //amount of "app world" time between updates
	private double timeIntervalStep = 1.3;
	
	//at the moment this object's run() is called 240 times/s (4166666 nanoseconds)
	private int VideoFrameCounter = 0;
	private volatile int VideoFrameLimit = 4;
	
	private List<Blob> newBlobs;//accessed from concurrent thread to avoid ConcurrentModificationE...
	private List <Blob> blobs;
	private List<ChargePoint> newCharges;
	private List<ChargePoint> charges;
	private List <Vec> listOfCollisionPoints;
	
	private ChargePoint pointer;
	private boolean repulseFromPointer;
	
	private FrameBuffer buffer;
	//TODO
	private Vec stageCentre;
	private boolean gravityInCentre;
	private Vec newStageCentre;
	
	ProcessorOfCollisions colDet;
	private ColFlag colFlag;
	private boolean mouseInside = true;
	
	private List <Command> commands;
	
	public World () {
		System.out.println("World constr. thread - " + Thread.currentThread().getName());
		buffer = FrameBuffer.getInstance();
		commands = new ArrayList();
		
		
		newBlobs = new ArrayList<Blob>();
		blobs = new ArrayList<Blob> ();
		newCharges = new ArrayList<ChargePoint>();
		charges = new ArrayList<ChargePoint>();
		newStageCentre = new Vec(400,400);
		stageCentre = new Vec(400, 400);
		
		pointer = new ChargePoint(new Vec(0,0), 1.0, ColourCategory.NEUTRAL, Charger.REPULSE_ALL);
		
		listOfCollisionPoints = new ArrayList<Vec>();
		
		colDet = new ProcessorOfCollisions(getListOfCollisionPoints(), getTimeInterval());
		colFlag = ColFlag.DO_NOT_FORCE;
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
	
//	public void run() {
//		System.out.println("World.run() thread - " + Thread.currentThread().getName());
//		while (true) {
//			updateLoop();
//		}
//	}
	
	public void run() {
		
		//0 - copy exposed data structures to create core data free of errors caused by mutations during computations; 
		blobs.addAll(newBlobs);		
		newBlobs.clear();
		
		charges.addAll(newCharges);
		newCharges.clear();
		
		stageCentre = new Vec(newStageCentre);
		
		// 1 a - update data in data structures
		for (Blob b : blobs) {
			Vec drag; 
			// account for gravity
			if (gravityInCentre) {
				drag = Vec.vecFromAtoB(b.getPosition(), stageCentre).
						setMagnitude(gravity);
			} else {
				drag = new Vec(0, gravity);
			}
			// account for charges
			for (ChargePoint c : charges) {				
				c.charge(b);
			}
			
			if (repulseFromPointer) {
				// extra ChargePoint with position equal to mouse pointer position
				if (mouseInside) {
					pointer.charge(b);
				}
			}		
			
			
			b.update(timeInterval, drag);
		}
		
		//1b - execute commands
		if (!commands.isEmpty()) {
			//commands list is a normal list and it may be accessed from other thread
			//commands are issued from gui
			synchronized(LOCK) {
				executeCommandList();
				commands.clear();
			}
		
		}
		
		// 1c - update world - collisions
		colDet();

		
		// 2 - update display buffer (this thread is put to sleep when buffer is full)
		if(VideoFrameCounter < VideoFrameLimit) {
			VideoFrameCounter++;
			return;
		}
		VideoFrameCounter = 0;
		
		buffer.addFrame(
				new FrameData(blobs, charges, listOfCollisionPoints,
						gravity, timeInterval, minX, minY, maxX, maxY));
	}
	
	private void executeCommandList() {
		for (Command c : commands) {
			
			switch (c.commandType) {
			case GRAVITY_CENTRE:
				gravityInCentre = true;
				break;
			case GRAVITY_DOWN:
				gravityInCentre = false;
				break;
			case KILL_BLOBS:
				blobs.clear();
				break;
			case KILL_CHARGES:
				charges.clear();
				break;
			case KILL_LAST_CHARGE:
				if (!charges.isEmpty()) {
					charges.remove(charges.size() - 1);
				}
				break;
			case KILL_VELOCITIES:
				for (Blob b : blobs) {
					b.setVelocity(b.getVelocity().setXY(0, 0));
				}
				break;
			case SIDES_BOUNCY:
				System.out.println("SIDES_BOUNCY to be done");
				break;
			case SIDES_WRAPPED:
				System.out.println("SIDES_WRAPPED to be done");
				break;
			case SWITCH_COLLISION_DETECTION:
				switchCollisonDetections();
				break;
			case SWITCH_CHARGE_COLOURS:
				switchChargeColourCategories();
				break;
			case SWITCH_CHARGE_TYPES:
				switchChargeTypes();
				break;
			case VELOCITIES_MULTIPLY_BY:
				for (Blob b : blobs) {
					b.setVelocity(b.getVelocity().multiply(c.doubleValue));
				}
				break;
			case VERTICALLY_WRAPPED:
				break;
			
			}
			
		}
	}
	
	private void colDet() {
					//((ColDetect) collisionsArray[currentColl]).detectCollisions(new ArrayList<Collidable>(getBlobs()), minX, maxX, minY, maxY);
//					 try { Thread.sleep(100); } catch (InterruptedException e)
//					 {e.printStackTrace(); }
					colDet.doCollisions(new ArrayList<Collidable>(getBlobs()), minX, maxX, minY, maxY,
							defaultRadiusMultiplier, colFlag);
	}
		
	//Control interface
	
	private void switchCollisonDetections() {
		
		if (colFlag.ordinal() == ColFlag.values().length - 1) {
			colFlag = ColFlag.values()[0];
		} else {	
			colFlag = ColFlag.values()[colFlag.ordinal()+1];
		}
		System.out.println("Col. det. set to " + colFlag.name());
	}
	
	private void switchChargeTypes() {
		Charger ch;
		for (ChargePoint c : charges) {
			ch = c.getType();
			
			if (ch.ordinal() == Charger.values().length-1)
				c.setType(Charger.values()[0]);
			else
				c.setType(Charger.values()[ch.ordinal()+1]);
		}
	}
	
	private void switchChargeColourCategories() {
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

	public void setTemperature(double value) {
		timeInterval = value;
	}
		public void gravityUp() {
			gravity *= gravityDelta;
			System.out.println("gravity: " + gravity);
		}
		public void gravityDown() {
			gravity /= gravityDelta;
			System.out.println("gravity: " + gravity);
		}

	public void setGravity(double value) {
		gravity = value;
	}
	
	public void addHugeBlob(double x, double y) {
		newBlobs.add(new Blob (new Vec(x,y), 200));
	}
		
	public void addSmallBlob(double x, double y) {
		newBlobs.add(new Blob (new Vec(x,y), 30));
	}
	
	public void addBlobAt(double x, double y) {
			newBlobs.add(new Blob (new Vec(x,y), U.rndInt(5, 70)));
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
//	private void switchGravity() {
//		gravityInCentre = !gravityInCentre;
//		System.out.println("gravity switched");
//	}
	public void updateAlternativeGravityCentre(Vec v) {
		newStageCentre = v;
	}

	public void setMouseInside(boolean b) {
		mouseInside = b;		
	}

	public void setVideoFrameLimit(int videoFrameLimit) {
		VideoFrameLimit = videoFrameLimit;
	}

	//###########################################
	//Commands	
	public void applyCommand (Com commandType) {
		if (commandType == Com.VELOCITIES_MULTIPLY_BY) {
			throw new IllegalArgumentException(Com.VELOCITIES_MULTIPLY_BY + " requires argument of type \"double\"");
		}
		commands.add(new Command(commandType));
	}
	public void applyCommand (Com commandType, int value) {
		commands.add(new Command(commandType, value));
	}
	public void applyCommand (Com commandType, double value) {
		commands.add(new Command(commandType, value));
	}
}
