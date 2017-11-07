package app;

import data.Blob;
import data.DataController;

public class InputReceiver {
	
	DataController dc;
	Logic l;
	
	public InputReceiver(DataController dc, Logic l) {
		this.dc = dc;
		this.l = l;
	}
	
	
	//speed
	public void pageUpAction() {
		dc.speedUp();
	}
	
	public void pageDownAction() {
		dc.speedDown();		
	}
	
	//increase gravity
	public void homeAction() {
		dc.gravityUp();		
	}
	
	//reduce gravity
	public void endAction() {
		dc.gravityDown();
	}
	
	public void  sBarAction() {
		l.switchCollisonsDetect();
	}
	
	public void stageMoved (int deltaX, int deltaY) {
		//System.out.println("UserInput.stageMoved deltaX: " + deltaX + ", y: " + deltaY);
		DataController.getStageMovementDelta().setXY(deltaX, deltaY);
	}

	//add blob
	public void leftClickAt(double x, double y) {
		System.out.println("leftClickAt " + x + ", " + y + ". Adding a blob.");
		dc.addBlobAt(x, y);
	}

	//add blob at random position
	public void insertAction() {
		dc.addBlob();		
	}

	//add charge point
	public void rightClickAt(double x, double y) {
		System.out.println("rightClickAt " + x + ", " + y + ". Adding a charge.");
		dc.addCharge(x,y);	
	}
}
