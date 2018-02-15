package data;

import java.util.ArrayList;
import java.util.List;

import collisions.ProcessorOfCollisions;
import collisions.ColFlag;
import collisions.Collidable;
import data.Mod.Charger;
import data.Colour.ColourCategory;
import data.Command.Com;
import utils.U;


//TODO: make sure all mutations of core data happen only in "update" loop via buffering Lists to avoid concurrent access
public class World implements Runnable{
	
	public static boolean debug = false;
	
	private final Object LOCK = new Object();
	//SETTINGS
	private static volatile int maxX = 200;
	private static volatile int minX = 0;//-1000;
	private static volatile int maxY = 0;	
	private static volatile int minY = -2000;
	
	private double defaultRadiusMultiplier = 1.0;
	private static double gravity =  0.2;
	private static double gravityDelta = 1.02;
	private static double timeStep = 1.0; //amount of "app world" time between updates
	private double timeStepDelta = 1.0;
	
	//at the moment this object's run() is called 240 times/s (4166666 nanoseconds)
	private int VideoFrameCounter = 0;
	private volatile int VideoFrameLimit = 4;
	
	private List<Blob> newBlobs;//accessed from concurrent thread to avoid ConcurrentModificationE...
	private List <Blob> blobs;
	private List<Mod> newCharges;
	private List<Mod> mods;
	
	private List <Vec> listOfCollisionPoints;
	
	private Mod pointer;
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
	private FrameData newDataSet;
	
	public World () {
		System.out.println("World constr. thread - " + Thread.currentThread().getName());
		buffer = FrameBuffer.getInstance();
		commands = new ArrayList();
		
		
		newBlobs = new ArrayList<Blob>();
		blobs = new ArrayList<Blob> ();
		newCharges = new ArrayList<Mod>();
		mods = new ArrayList<Mod>();
		newStageCentre = new Vec(400,400);
		stageCentre = new Vec(400, 400);
		
		pointer = new Mod(new Vec(0,0), 1.0, ColourCategory.NEUTRAL, Charger.REPULSE_ALL);
		
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
	
	public void run() {
		
		//0 - copy exposed data structures to create core data free of errors caused by mutations during computations; 
		blobs.addAll(newBlobs);		
		newBlobs.clear();
		
		mods.addAll(newCharges);
		newCharges.clear();
		
		stageCentre = new Vec(newStageCentre);
		
		//1b - execute commands
				if (!commands.isEmpty()) {
					//commands list is a normal list and it may be accessed from other thread
					//commands are issued from gui
					//synchronized(LOCK) {
						executeCommandList();
						commands.clear();
					//}
						System.out.println("commands cleared");
				}
		
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
			
			// add influence of mods
			for (Mod c : mods) {				
				c.charge(b);
			}
			
			//TODO: pointer
			if (repulseFromPointer) {
				// extra ChargePoint with position equal to mouse pointer position
				if (mouseInside) {
					pointer.charge(b);
				}
			}		
			
			
			b.update(timeStep, drag);
		}
		
		
		// 1c - update world - collisions
		colDet();

		
		// 2 - update display buffer (this thread is put to sleep when buffer is full)
		if(VideoFrameCounter < VideoFrameLimit) {
			VideoFrameCounter++;
			return;
		}
		VideoFrameCounter = 0;
		
		buffer.addFrame(makeSnapshot());
				
	}
	
	public FrameData makeSnapshot() {
		return new FrameData(blobs, mods, listOfCollisionPoints,
				gravity, timeStep, minX, minY, maxX, maxY);
	}
	
	private void executeCommandList() {
		for (Command c : commands) {
			System.out.println(c.commandType + ", "+ c);
			switch (c.commandType) {
			case SWITCH_TO_ALTERNATIVE_DATA:
				applyAlternativeData();
				
				return;//running new data set - it's a "new beginning"
				
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
				mods.clear();
				break;
			case KILL_LAST_CHARGE:
				if (!mods.isEmpty()) {
					mods.remove(mods.size() - 1);
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
			case SWITCH_MOD_COLOURS:
				switchModColourCategories();
				break;
			case SWITCH_MOD_TYPES:
				switchModTypes();
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
	
	private void switchModTypes() {
		Charger ch;
		for (Mod c : mods) {
			ch = c.getType();
			
			if (ch.ordinal() == Charger.values().length-1)
				c.setType(Charger.values()[0]);
			else
				c.setType(Charger.values()[ch.ordinal()+1]);
		}
		
	}
	
	private void switchModColourCategories() {
		ColourCategory cc;		
		for (Mod c : mods) {
			cc = c.getColourCategory();
			
			if (cc.ordinal() >= ColourCategory.values().length-2)// "-2" - eliminating NEUTRAL
				c.setColourCategory(ColourCategory.values()[0]);
			else
				c.setColourCategory(ColourCategory.values()[cc.ordinal()+1]);
		}
	}

	
		public void speedUp() {
			timeStep *= timeStepDelta;
			System.out.println("timeInterval: " + timeStep);
		}
		public void speedDown() {
			timeStep *= 1/timeStepDelta;
			System.out.println("timeInterval: " + timeStep);
		}

	public void setStep(double value) {
		timeStep = value;
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
			newBlobs.add(new Blob (new Vec(x,y), U.rndInt(20, 70)));
	}
	
	public void addBlob(int i) {
		int c = 0;
		while (c++ < i) {
			addBlobAt((U.rndInt(minX, maxX)), U.rndInt(minY, maxY));
		}
	}
	
	public void addCharge(Mod ch) {
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
		return timeStep;
	}
	public void setTimeInterval(double timeInterval) {
		this.timeStep = timeInterval;
	}
	public List<Mod> getCharges() {
		return this.mods;
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

	public void setData(FrameData f) {
		this.newDataSet = f;
		applyCommand(Com.SWITCH_TO_ALTERNATIVE_DATA);
		System.out.println("loaded blobs: " + f.blobs.size());
//		//this.blobs.clear();
//		this.blobs.addAll(f.blobs);
//		this.charges = f.charges;
////		//this.collisions = f.collisions;
//		this.gravity = f.gravity;
//		this.timeInterval = f.speed;
//		this.minX = f.minX;
//		this.minY = f.minY;
//		this.maxX = f.maxX;
//		this.maxY = f.maxY;
//		System.out.println("all set");
	}
	
	private void applyAlternativeData() {
//		debug = true;
//		System.out.println(this.blobs.get(0));
		this.blobs = new ArrayList<Blob>(newDataSet.blobs);
//		System.out.println(newDataSet.blobs.size());
//		System.out.println(newDataSet.blobs.get(0));
//		System.out.println(newDataSet.blobs.size());
		
		//this.blobs.add(newDataSet.blobs.get(0));
		//initData(1);
		this.mods = new ArrayList<Mod>(newDataSet.charges);
		//this.collisions = 
		this.gravity = newDataSet.gravity;
		this.timeStep = newDataSet.speed;
		this.minX = newDataSet.minX;
		this.minY = newDataSet.minY;
		this.maxX = newDataSet.maxX;
		this.maxY = newDataSet.maxY;
		
		
		System.out.println("applyAlternativeData - all set");
	}
}
