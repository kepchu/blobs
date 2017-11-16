package app;

import data.Colour;
import data.Colour.ColourCategory;
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
	
	public void  sBarAction() {
		w.switchCollisonsDetect();
	}
	
	public void stageMoved (int deltaX, int deltaY) {
		//System.out.println("UserInput.stageMoved deltaX: " + deltaX + ", y: " + deltaY);
		World.getStageMovementDelta().setXY(deltaX, deltaY);
	}

	//add blob
	public void leftClickAt(double x, double y) {
		System.out.println("leftClickAt " + x + ", " + y + ". Adding a blob.");
		w.addBlobAt(x, y);
	}

	//add blob at random position
	public void insertAction() {
		w.addBlob(5);		
	}

	//add charge point
	public void rightClickAt(double x, double y) {
		System.out.println("rightClickAt " + x + ", " + y + ". Adding a charge.");
		w.addCharge(x,y, 1, ColourCategory.NEUTRAL);	
	}


	public void rLeftClick(double x, double y) {
		w.addCharge(x, y, 1.0, ColourCategory.R);	
	}
	public void gLeftClick(double x, double y) {
		w.addCharge(x, y, 1.0, ColourCategory.G);	
	}
	public void bLeftClick(double x, double y) {
		w.addCharge(x, y, 1.0, ColourCategory.B);	
	}
}
