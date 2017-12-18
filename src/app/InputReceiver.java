package app;

import data.Colour;
import data.Colour.ColourCategory;
import data.StageDescription;
import data.Vec;
import data.ChargePoint;
import data.ChargePoint.Charger;
import data.World;

public class InputReceiver {
	
	World w;
	Loop l;
	
	public InputReceiver(World dc, Loop l) {
		this.w = dc;
		this.l = l;
	}
	
	
	//speed
	public void pageUpAction() {
		w.speedUp();
	}
	
	public void pageDownAction() {
		w.speedDown();		
	}
	
	//increase gravity
	public void homeAction() {
		w.gravityUp();		
	}
	
	//reduce gravity
	public void endAction() {
		w.gravityDown();
	}
	
	public void stageMoved (int deltaX, int deltaY) {
		//System.out.println("UserInput.stageMoved deltaX: " + deltaX + ", y: " + deltaY);
	}

	//add blob
	public void leftClickAt(double x, double y) {
		System.out.println("leftClickAt " + x + ", " + y + ". Adding a blob.");
		w.addBlobAt(x, y);
		//w.addHugeBlob(x, y);
	}

	//add blob at random position
	public void insertAction() {
		w.addBlob(5);		
	}

	//add charge point
	public void rightClickAt(double x, double y) {
		//w.addSmallBlob(x, y);
//		System.out.println("rightClickAt " + x + ", " + y + ". Adding a charge.");
		w.addCharge(new ChargePoint(new Vec (x,y), 1, ColourCategory.NEUTRAL, Charger.COLOUR_CATEGORY_ABSOLUTE));	
	}


	public void rLeftClick(double x, double y) {
		w.addCharge(new ChargePoint(new Vec (x,y), 1, ColourCategory.R, Charger.COLOUR_COMPONENT));
	}
	public void gLeftClick(double x, double y) {
		w.addCharge(new ChargePoint(new Vec (x,y), 1, ColourCategory.G, Charger.COLOUR_COMPONENT));
	}
	public void bLeftClick(double x, double y) {
		w.addCharge(new ChargePoint(new Vec (x,y), 1, ColourCategory.B, Charger.COLOUR_COMPONENT));
	}


	
	
	public void togglePointerRepulse() {
		w.repulseFromPointer();
	}

	public void udateStageDimensions (StageDescription sd) {
		w.updateAlternativeGravityCentre(sd.getAlternativeGravityCentre());
		w.setMinX((int)sd.getMinXY().getX());
		w.setMaxX((int)sd.getMaxXY().getX());
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

	public void switchStageWrap() {
		// TODO Auto-generated method stub
	}
	
	public void switchGravity() {
		w.switchGravity();	
	}
	
	public void  switchCollisions() {
		w.switchCollisonDetections();
	}

	public void switchChargeColourCategories() {
		System.out.println("cShiftAction()");
		w.switchChargeColourCategories();
	}
	public void switchChargeTypes() {
		System.out.println("cShiftAction()");
		w.switchChargeTypes();
	}
	public void undo() {
		// TODO Auto-generated method stub
		
	}


	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
