package app;

import data.Colour;
import data.Colour.ColourCategory;
import data.Command.Com;
import data.FrameData;
import data.StageDescription;
import data.Vec;

import java.util.Map;

import data.Mod;
import data.Mod.Charger;
import data.World;

import data.Command;

public class InputReceiver {
	
	World w;
	DiscAccess da; 
	private boolean lockStageSidesToWindow = true;
	
	public InputReceiver(World w, DiscAccess da, MainHub l) {
		this.w = w;
		this.da = da;
	}
	
	
	//speed
	public void pageUpAction() {
		//w.speedUp();
	}
	
	public void pageDownAction() {
		//w.speedDown();		
	}
	
	//increase gravity
	public void homeAction() {
		//w.gravityUp();		
	}
	
	//reduce gravity
	public void endAction() {
		//w.gravityDown();
	}
	
	public void stageMoved (int deltaX, int deltaY) {
		//System.out.println("UserInput.stageMoved deltaX: " + deltaX + ", y: " + deltaY);
	}

	//add blob at random position
	public void insertAction() {
		w.addBlob(5);		
	}

	//add blob
		public void leftClickAt(double x, double y) {
			System.out.println("leftClickAt " + x + ", " + y + ". Adding a blob.");
			//w.addBlobAt(x, y);
			w.addHugeBlob(x, y);
		}
	
	//add a mod
	public void rightClickAt(double x, double y) {
		//w.addSmallBlob(x, y);
//		System.out.println("rightClickAt " + x + ", " + y + ". Adding a charge.");
		w.addCharge(new Mod(new Vec (x,y), ColourCategory.NEUTRAL, Charger.VELOCITY_C_CATEGORY_ABSOLUTE));	
	}
	public void rLeftClick(double x, double y) {
		w.addCharge(new Mod(new Vec (x,y), ColourCategory.R, Charger.VELOCITY_C_COMPONENT));
	}
	public void gLeftClick(double x, double y) {
		w.addCharge(new Mod(new Vec (x,y), ColourCategory.G, Charger.VELOCITY_C_COMPONENT));
	}
	public void bLeftClick(double x, double y) {
		w.addCharge(new Mod(new Vec (x,y), ColourCategory.B, Charger.VELOCITY_C_COMPONENT));
	}


	
	
	public void togglePointerRepulse() {
		w.repulseFromPointer();
	}

	public void lockStageSidesToWindow(boolean b) {
		lockStageSidesToWindow = b;
	}
	
	public void udateStageDimensions (StageDescription sd) {		
		if (lockStageSidesToWindow ) {
			w.updateAlternativeGravityCentre(sd.getAlternativeGravityCentre());
			w.setMinX((int)sd.getMinXY().getX());
			w.setMaxX((int)sd.getMaxXY().getX());
		}
	}
	
	
	public void updateMousePointerPosition(double x, double y) {
		w.updatePointer(x, y);
		
	}


	
	
	public void colourChanged(ColourCategory colourCategory) {
		System.out.println(getClass().getSimpleName() + "colourChanged(): " + colourCategory);
	}

	public void chargeChanged(Charger charger) {
		System.out.println(getClass().getSimpleName() + "colourChanged(): " + charger);	
	}

	
	
	

	
	


	public void setMouseInside(boolean b) {
		w.setMouseInside(b);		
	}


	public void setGravity(double value) {
		// TODO Auto-generated method stub
		w.setGravity(value);
	}


	public void setTemperature(double value) {
		// TODO Auto-generated method stub
		w.setStep(value);
	}
	
	public void videoFrameFactorChanged(int newLimit) {
		System.out.println(newLimit);
		w.setVideoFrameLimit(newLimit);
	}

	
	
	
	
	
	public void switchChargeColourCategories() {
		System.out.println("cShiftAction()");
		//w.switchChargeColourCategories();
		w.applyCommand(Com.SWITCH_MOD_COLOURS);
	}
	public void switchChargeTypes() {
		System.out.println("cShiftAction()");
		//w.switchChargeTypes();
		w.applyCommand(Com.SWITCH_MOD_TYPES);
	}
	public void wrapEdges() {
		//System.out.println("wrapEdges");
		w.applyCommand(Com.SIDES_WRAPPED);
	}
	public void unwrapEdges() {
		//System.out.println("unwrapEdges");
		w.applyCommand(Com.SIDES_BOUNCY);
	}
	
	public void gravityDown() {
		//w.switchGravity();//TODO
		w.applyCommand(Com.GRAVITY_DOWN);
	}
	public void gravityCentre() {
		//w.switchGravity();//TODO
		w.applyCommand(Com.GRAVITY_CENTRE);
	}
	
	public void  switchCollisions() {
		System.out.println("switchCollisions");
		w.applyCommand(Com.SWITCH_COLLISION_DETECTION);
	}
	
	public void multiplyVelocitiesBy(double d) {
		System.out.println("multiplyVelocitiesBy " + d);
		if (d == 0) {
			w.applyCommand(Com.KILL_VELOCITIES);
		}else {
			w.applyCommand(Com.VELOCITIES_MULTIPLY_BY, d);
		}
	}
	
	public void removeLastCharge() {
		System.out.println("removeLastCharge");
		w.applyCommand(Com.KILL_LAST_CHARGE);
	}
	
	public void removeAllCharges() {
		System.out.println("removeAllCharges");
		w.applyCommand(Com.KILL_CHARGES);
	}
	
	public void reset() {
		System.out.println("IR reset");
		w.applyCommand(Com.KILL_BLOBS);
		w.applyCommand(Com.KILL_CHARGES);
	}
	
	
	//disc
	public void loadFrame() {
		System.out.println("ir.loadFrame()");
		w.setData(da.load().getData());
		
//		if(w.setFrame(DiscAccess.loadFrame())) {
//			System.out.println("new frame started");
//		}else {
//			System.out.println("sorry vinnetou - frame insertion failed");
//		}
	}
	public void save(Map<String, Object> guiState) {
		System.out.println("ir.save(frame)");
		da.save(w.makeSnapshot(), guiState);
	} 
}
