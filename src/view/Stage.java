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
import data.ChargePoint;
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
	
	

	StageCamera camera;
	

	private boolean initFinished;

	private boolean mouseOudside;

	private boolean drawSideWalls;
	
	public Stage(BufferableFrames displayBuffer) {
		System.out.println("Stage constr. thread - " + Thread.currentThread().getName());
		this.displayBuffer = displayBuffer;
		frame = displayBuffer.getFrame();
				
		camera = new StageCamera(0,0);//1000, 800);//TODO: magic numbers

	}

	
	// DRAWING ROUTINE
	//ISSUE: paint called by system before setData call - this produces NullPointer
	public void paint(Graphics g) {
		//retrieve frame data
		frame = displayBuffer.getFrame();
		if (frame  == null) {
			System.out.println("Stage.paint(): no frame data to draw. Returning.");
			return;
		}
		
		//validate/update camera position
		camera.update();
		
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		//TODO: proper enum enumerating drawing complexity with appropriate drawing methods
		
		
		// calls to methods that draw specific elements
		drawBackground(g2d);//TODO: generate some background to be scrolled when window is moved
		drawRuler(g2d);
		//the below methods use data object
		
		drawBlobs(g2d);
		
		
		if(drawSideWalls) {
			drawBorders(g2d);
		}
		
		drawCharges(g2d);
		
		drawInfo(g2d);
		displayBuffer.advanceFrame();
	}
	
	// methods used by the drawing routine in paint (Graphics g)
	private void drawBackground(Graphics2D g) {
		if (true) {
			g.setColor(new Color(227, 227, 226, 255));
			g.fillRect(0, 0, camera.width, camera.height);
			g.setColor(new Color(243, 243, 243, 255));
			//g.fillOval(20, 20, camera.width - 40, camera.height - 40);
			g.fillOval(0, 0, camera.width, camera.height);
		} else {
			
			g.setColor(new Color(255-243, 255-243, 255-243, 255));
			g.fillRect(0, 0, camera.width, camera.height);
			g.setColor(new Color(255-227, 255-227, 255-226, 255));
			g.fillOval(20, 20, camera.width - 40, camera.height - 40);
		}	
	}

	private void drawRuler(Graphics2D g) {

		int spacing = 200;
		
		spacing = (int)(spacing * camera.scale);
		
		for (int i = 0; i < camera.elevation; i = i + spacing) {
			g.setColor(Color.BLACK);
			g.drawLine(camera.width / 4, (int)camera.elevation - i,
						(camera.width / 4) * 3, (int)camera.elevation - i);
			g.drawString(i + " pix", camera.width / 4, (int)camera.elevation - i - 4);
			g.setColor(Color.GRAY);
			g.drawLine(camera.width / 3, (int)camera.elevation - i - spacing / 2, (camera.width / 3) * 2, (int)camera.elevation - i - spacing / 2);
		}	
	}

	private void drawBorders(Graphics2D g) {
//		g.setColor(Color.RED);
		//draw ground level
//		g.drawLine(0, camera.elevation, camera.width, camera.elevation);				
//		g.drawLine(scaleX(frame.minX), scaleY(frame.minY), scaleX(frame.minX), scaleY(frame.maxY));		
//		g.drawLine(scaleX(frame.maxX), scaleY(frame.minY), scaleX(frame.maxX), scaleY(frame.maxY));		
		g.setColor(Color.BLACK);
		
		g.fillRect(0, 0, camera.scaleX(frame.minX), camera.height);
		g.fillRect(camera.scaleX(frame.maxX), 0, camera.width, camera.height);
		
		//debug
		g.setColor(Color.RED);
		g.fillRect((int)camera.middlePoint.getX() - 5, (int)camera.middlePoint.getY()-5, 10, 10);
		g.drawLine((int)camera.middlePoint.getX(), 0, (int)camera.middlePoint.getX(), camera.height);
		g.drawLine(camera.width/2, (int)camera.middlePoint.getY(), camera.width/2, (int)camera.middlePoint.getY());
	}
	
	private void drawBlobs(Graphics2D g) {
		
		int medDrawingSize = 2;
		int fullDrawingSize = 50000;//30;
		int minTaggingSize = 50000;//50;		
		
		for (Blob b : frame.blobs) {
			int radius = (int)(b.getRadius() * camera.scale);
			
			//Subtracting radius to accommodate to drawOval method
			//(where position denotes top left corner rather than centre)
			g.setColor(b.getColour().getColor());
			
			int offsetX = camera.scaleX(b.getPosition().getX());
			int offsetY = camera.scaleY(b.getPosition().getY());
			
			
//			if(radius <= medDrawingSize) {
//				//draw just a point
//				g.drawLine(offsetX, offsetY, offsetX, offsetY);
//				continue;
//			}
			
			
			int circumference = radius + radius;
			if(radius <= medDrawingSize) {
				//draw something bigger
				g.fillOval(offsetX - circumference, offsetY - circumference, circumference *2, circumference*2);
				continue;
			}
					
			//Subtracting radius to accommodate to drawOval method
			//(where position denotes top left corner rather than centre)
			g.fillOval(offsetX - radius, offsetY - radius, circumference, circumference);
			
			if(radius < fullDrawingSize) {//bailout
				continue;
			}	
	
			//drawReflectionOnBlob(g, offsetX, offsetY, radius);
			
				
			g.setColor(Color.BLACK);
			g.drawOval(offsetX - radius, offsetY - radius, circumference, circumference);
			
			//TODO:
			if (radius > minTaggingSize) {
				//g.drawString(b.getID() + ", " + b.getColour().getCategory(), offsetX + radius, (int)(offsetY + radius * 1.5));
				g.drawString(b.toString(),offsetX, (int)(offsetY + radius/2));
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
		
		int x,y;
		for (ChargePoint c : frame.charges) {
			g.setColor(c.getColourCategory().getDefaultCategoryColor());
			x = camera.scaleX(c.getPosition().getX()); 
			y = camera.scaleY(c.getPosition().getY());
			g.drawString(c.toString(), x, y);
		}
//		g.setColor(Color.BLACK);
//		int width = 10, height = 10;
//		int x,y;
//		for (ChargePoint c : frame.getCharges()) {
//			x = scaleX(c.getPosition().getX() - width/2); 
//			y = scaleY(c.getPosition().getY() - height/2);
//			g.fillRect(x, y, (int)(width * scale),(int)(height * scale));
//		}
	}
	
	private void drawCollisions(Graphics2D g) {
		g.setColor(new Color(0,255,255,100));
		int circumference = 6;
		int maxNumberOfDrawnCollisions = 300;
				
		//draw last maxNumberOfDrawnCollisions from listOfCollisionPoints
		int i = 0;
		for (Vec c: frame.collisions) {
			int offsetX = camera.scaleX(c.getX()) - circumference/2;
			int offsetY = camera.scaleY(c.getY()) - circumference/2;			
			g.fillOval(offsetX, offsetY, circumference, circumference);
			
			if(++i > maxNumberOfDrawnCollisions) {break;}
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

	//CAMERA#############################################
	public void setAutoZoom(boolean autoZoom) {
		this.drawSideWalls = autoZoom;
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
		pointerPosition.setXY(x, y);
		camera.pp.setXY(x,y);
	}
//CAMERA end
	
	public FrameData getFrame () {
		return frame;
	}
	//called from View controller
	public void initFinished() {		
		//camera.height = camera.elevation = getHeight();
		this.addComponentListener(camera);//TODO - move to constr.
		//camera.componentResized(null);//??
		camera.resetDimentions(getWidth(), getHeight());
		//camera.update();
	}

}
