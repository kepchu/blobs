package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import data.Blob;
import data.BufferableFrames;
import data.Mod;
import data.FrameData;
import data.Vec;

//ISSUE: paint called by system before setData call - this produces NullPointer
//This class draws view of data. Area that is drawn and magnification determined by camera field.
@SuppressWarnings("serial")
public class Stage extends JPanel{
	
	private final static Object LOCK = new Object();
	
	private FrameData frame;
	private BufferableFrames displayBuffer;
	

	private Vec pointerPosition = new Vec(0,0);
	
	private final int MAX_ANIMATED_RADIUS = 100;
	int contractingRadius = MAX_ANIMATED_RADIUS;
	
	StageCamera camera;
	
	//SETTINGS //TODO - A HASH-MAP
	private final boolean DRAW_CIRCUMFERENCE = false, TAG_BLOBS = false;
	private boolean setDrawingMode;//TODO: draw from previous positions to new & gradually like coll. points
	
	
	public Stage(BufferableFrames displayBuffer) {
		System.out.println("Stage constr. thread - " + Thread.currentThread().getName());
		this.displayBuffer = displayBuffer;
		frame = displayBuffer.getFrame();
				
		camera = new StageCamera(0,0);//1000, 800);//TODO: magic numbers

	}

	//called from View controller
	public void initFinished() {				
		this.addComponentListener(camera);//TODO - move to constr.
		camera.resetDimentions(getWidth(), getHeight());		
	}
	
	// DRAWING ROUTINE
	// paint method used as the "main loop".
	// Synchronisation by "getFrame()" and "andvanceFrame()" calls;
	public void paint(Graphics g) {
		//retrieve frame data
		frame = displayBuffer.getFrame();
		if (frame  == null) {
			//System.out.println("Stage.paint(): no frame data to draw. Returning.");
			return;
		}
		
		//validate/update camera position
		camera.update();
		
		//draw things
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
		// calls to methods that draw specific elements
		drawBackground(g2d);//TODO: generate some background to be scrolled when window is moved
		//drawRuler(g2d);
		//the below methods use data object
		
		drawBlobs(g2d);
	
		drawBorders(g2d);
				
		drawCharges(g2d);
		
		drawInfo(g2d);
		
		//ready for next frame
		displayBuffer.advanceFrame();
	}
	
	// methods used by the drawing routine in paint(Graphics g)
	private void drawBackground(Graphics2D g) {
		if((boolean)State.get("drawingMode")) return;		
		if ((boolean)State.get("darkMode")) {
			//g.setColor(new Color(255-243, 255-243, 255-243, 255));
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, camera.width, camera.height);
			g.setColor(new Color(255-227, 255-227, 255-226, 255));
			g.fillOval(20, 20, camera.width - 40, camera.height - 40);
		} else {
			g.setColor(new Color(227, 227, 226, 255));
			g.fillRect(0, 0, camera.width, camera.height);
			g.setColor(new Color(243, 243, 243, 255));
			g.fillOval(20, 20, camera.width - 40, camera.height - 40);
			//g.fillOval(0, 0, camera.width, camera.height);			
		}	
	}

	private void drawBorders(Graphics2D g) {

		int ground = camera.scaleY(frame.maxY);
		int leftEdge = camera.scaleX(frame.minX);
		int rightEdge = camera.scaleX(frame.maxX);
		
		
		
		g.setColor(Color.BLACK);
		
		g.fillRect(0, ground, camera.width, camera.height - ground);
		
		if((boolean)State.get("drawSideWalls")) {//drawSideWalls) {
			g.fillRect(0, 0, leftEdge, camera.height);
			g.fillRect(rightEdge, 0, camera.width, camera.height);
		}
		
		g.setColor(Color.RED);
		//draw ground level
		g.drawLine(0, ground, camera.width, ground);
		
	}
	
	private void drawBlobs(Graphics2D g) {
		
		int minRadius = 1;//radius scaled to smaller length it will be drawn with length of minRadius
		int halfFontHeight = 4;//used to align tag's centre with blob's centre (vertically);
		
		for (Blob b : frame.blobs) {
				
			g.setColor(b.getColour().getColor());
			int x = camera.scaleX(b.getPosition().getX());
			int y = camera.scaleY(b.getPosition().getY());
			//apply minimal drawing size
			int radius = Math.max((int)(b.getRadius() * camera.scale), minRadius);
			int circumference = radius + radius;
			//Subtracting radius to accommodate to drawOval method
			//(where position denotes top left corner rather than centre)
			g.fillOval(x - radius, y - radius, circumference, circumference);
			
			if (DRAW_CIRCUMFERENCE) {				
				g.setColor(Color.BLACK);
				g.drawOval(x - radius, y - radius, circumference, circumference);
			}
			
			if(TAG_BLOBS) {
				g.setColor(Color.BLACK);
				g.drawString(Integer.toString(b.ID),x, y+halfFontHeight);
			}
		}
	}
	
	private void drawReflectionOnBlob(Graphics2D g, int blobX, int blobY, int radius) {
		
		int displacement = radius/2;
		g.setColor(new Color(255,255,255,200));
		
		Vec sB = new Vec(blobX, blobY);
		Vec blobToPointer = Vec.vecFromAtoB(sB, pointerPosition);
		
		blobToPointer.setMagnitude(displacement * (
				blobToPointer.getMagnitude()/(camera.getStageCentreX()+camera.getStageCentreY())));
		
		int x1 = (int)sB.getX();
		int y1 = (int)sB.getY();
		int x2 = (int)(x1 + blobToPointer.getX());
		int y2 = (int)(y1 + blobToPointer.getY());
		
		//g.drawLine(x1, y1, x2, y2);
		g.fillOval(x2 - displacement/2, y2 - displacement/2, displacement, displacement);
	}
	
	
	private void drawCharges (Graphics2D g) {
		
		int step = 5;
		int pauseTime = -240 * step;
		
//		boolean animate = false;		
//		if (radiusPauseCounter < 0) {
//			if (expandingRadius < MAX_ANIMATED_RADIUS) {
//				expandingRadius += step;
//				contractingRadius = MAX_ANIMATED_RADIUS - expandingRadius;
//				animate = true;
//			} else {
//				expandingRadius = 0;
//				radiusPauseCounter = pauseTime;			
//			}
//		}else {
//			radiusPauseCounter--;
//		}
		
		if (contractingRadius < pauseTime) contractingRadius = MAX_ANIMATED_RADIUS;
		contractingRadius-= step;	
		int expandingRadius = MAX_ANIMATED_RADIUS - contractingRadius;
		
		for (Mod c : frame.charges) {
			g.setColor(c.getColourCategory().getDefaultCategoryColor());
			int x = camera.scaleX(c.getPosition().getX()); 
			int y = camera.scaleY(c.getPosition().getY());
			g.drawString(c.toString(), x, y);
			
			
			if (contractingRadius > 0) {
				g.drawOval(x-contractingRadius, y-contractingRadius,contractingRadius*2, contractingRadius*2);
				g.drawOval(x-expandingRadius, y-expandingRadius,expandingRadius*2, expandingRadius*2);
			}
		}
	}
	
	private void drawInfo(Graphics2D g) {
		int x = 20, y = 20;
		g.setColor(Color.DARK_GRAY);
		g.drawString("Blobs [left-click] or [Insert]: " + frame.blobs.size(), x, y);
		g.drawString("Charges: [R]/[G]/[B] + [left click]: " + frame.charges.size(), x, y+12);
		g.drawString("Collisions ([SPACE BAR] to toggle: " + frame.collisions.size(), x, y+24);
		g.drawString("Buffered frames: " + displayBuffer.currentSize(), x, y+36);
		g.drawString("Speed [PgUp] / [PgDown]:" + frame.speed, x, y+48);
		g.drawString("Gravity [Home] / [End]:" + frame.gravity, x, y+60);
		g.drawString("Scale [CTRL] + m. wheel): " + camera.scale, x, y+72);
		g.drawString("Scroll: mouse wheel", x, y+84);
		g.drawString("Modify Charges: [C] & [SHIFT] + [C]", x, y+96);
		g.drawString("(De)activate mouse pointer: [middle-click] (mouse wheel click)", x, y+108);
	}
	//Drawing methods - END
	
	//CAMERA#############################################
	public void setAutoZoom(boolean autoZoom) {
		camera.setAutoRescalingToWidth(autoZoom);
	}
		
	public void zoomOut() {
		//System.out.println("zoomOut");
		//scale /= deltaZoom;
		camera.zoomOut();
	}

	public void zoomIn() {
		//System.out.println("zoomIn");
		//scale *= deltaZoom;
		camera.zoomIn();
	}

	public void scrollUp() {
		//System.out.println("Scroll up");
		//camera.elevation -= deltaY;
		camera.up();
	}

	public void scrollDown() {
		//System.out.println("Scroll down");
		//camera.elevation += deltaY;
		camera.down();
	}

	public void updatePointerPosition (int x, int y) {
		pointerPosition.setXY(x, y);//TODO: pointer to camera?
	}
//CAMERA end
	
	public FrameData getFrame () {
		return frame;
	}
	
}
