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

//TODO:/ISSUE: paint called by system before setData call - this produces NullPointer
@SuppressWarnings("serial")
public class Stage extends JPanel implements ComponentListener {
	
	private final static Object LOCK = new Object();
	
	private FrameData frame;
	private BufferableFrames displayBuffer;
	private int panelHeight;
	private int panelWidth;

	private Vec pointerPosition = new Vec(0,0);
	
	private int panelCentreX;
	private int panelCentreY;
	private double scale = 0.3;// "scale" is a zoom factor
	private int currentElevation = 0;//ground level used for drawing, adjusted when scrolling the scene

	private StageCamera camera;
	

	private boolean initFinished;

	private boolean mouseOudside;

	private boolean autoZoom;
	
	public Stage(int initialCameraPosition, BufferableFrames displayBuffer) {
		System.out.println("Stage constr. thread - " + Thread.currentThread().getName());
		this.displayBuffer = displayBuffer;
		frame = displayBuffer.getFrame();
		currentElevation = initialCameraPosition;
		
		camera = new StageCamera(scale, initialCameraPosition, 1000, 800);//TODO: magic numbers
		
//		SwingUtilities.invokeLater(new Runnable() {
//		    public void run() {
//		        this.addComponentListener(new ComponentAdapter() {
//		            public void componentResized(ComponentEvent e) {
//		                System.out.println("Component Listener 1");
//		            }
//		        });
//		    }
//		});
	}

	
	//SCALING METHODS - adjust for scale & camera's position
	public double descaleX(int x) {
		return (x + (panelCentreX*(scale - 1)))/scale;
	}
	public double descaleY(int y) {
		return (y - currentElevation) / scale;
	}
	private int scaleX(double x) {
		return (int)(x * scale - (panelCentreX * (scale -1)));
	}
	private int scaleY(double y) {
		return (int)(y * scale + currentElevation);
	}
	
	
	double descaledStageCentreY() {
		return descaleY(panelCentreY);
	}

	double descaledStageCentreX() {
		return descaleX(panelCentreX);
	}
	
	Vec descaledMinXY() {
		return new Vec (descaleX(0), descaleY(0));
	}
	Vec descaledMaxXY() {
		return new Vec (descaleX(panelWidth), descaleY(panelHeight));
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
		currentElevation = camera.getEleveation();
		scale = camera.getScale();
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		//TODO: proper enum enumerating drawing complexity with appropriate drawing methods
		
		
		// calls to methods that draw specific elements
		drawBackground(g2d);//TODO: generate some background to be scrolled when window is moved
		drawRuler(g2d);
		//the below methods use data object
		
		drawBlobs(g2d);
		
		
		if(autoZoom) {
			drawBorders(g2d);
		}
		
		drawCharges(g2d);
		
		drawInfo(g2d);
		displayBuffer.advanceFrame();
	}
	
	// methods used by the drawing routine in paint (Graphics g)
	private void drawBackground(Graphics2D g) {
		if (false) {
			g.setColor(new Color(227, 227, 226, 255));
			g.fillRect(0, 0, panelWidth, panelHeight);
			g.setColor(new Color(243, 243, 243, 255));
			g.fillOval(20, 20, panelWidth - 40, panelHeight - 40);
		} else {
			g.setColor(new Color(255-227, 255-227, 255-226, 255));
			g.fillRect(0, 0, panelWidth, panelHeight);
			g.setColor(new Color(255-243, 255-243, 255-243, 255));
			g.fillOval(20, 20, panelWidth - 40, panelHeight - 40);
		}	
	}

	private void drawRuler(Graphics2D g) {

		int spacing = 200;
		
		spacing = (int)(spacing * scale);
		
		for (int i = 0; i < currentElevation; i = i + spacing) {
			g.setColor(Color.BLACK);
			g.drawLine(panelWidth / 4, currentElevation - i,
						(panelWidth / 4) * 3, currentElevation - i);
			g.drawString(i + " pix", panelWidth / 4, currentElevation - i - 4);
			g.setColor(Color.GRAY);
			g.drawLine(panelWidth / 3, currentElevation - i - spacing / 2, (panelWidth / 3) * 2, currentElevation - i - spacing / 2);
		}	
	}

	private void drawBorders(Graphics2D g) {
//		g.setColor(Color.RED);
		//draw ground level
//		g.drawLine(0, currentElevation, panelWidth, currentElevation);				
//		g.drawLine(scaleX(frame.minX), scaleY(frame.minY), scaleX(frame.minX), scaleY(frame.maxY));		
//		g.drawLine(scaleX(frame.maxX), scaleY(frame.minY), scaleX(frame.maxX), scaleY(frame.maxY));		
		g.setColor(Color.BLACK);
		
		g.fillRect(0, 0, scaleX(frame.minX), panelHeight);
		g.fillRect(scaleX(frame.maxX), 0, panelWidth, panelHeight);
	}
	
	private void drawBlobs(Graphics2D g) {
		
		int medDrawingSize = 1;
		int fullDrawingSize = 30;
		int minTaggingSize = 50;
		
		
		for (Blob b : frame.blobs) {
			int radius = (int)(b.getRadius() * scale);
			
			//Subtracting radius to accommodate to drawOval method
			//(where position denotes top left corner rather than centre)
			g.setColor(b.getColour().getColor());
			
			int offsetX = scaleX(b.getPosition().getX());
			int offsetY = scaleY(b.getPosition().getY());
			
			
			if(radius <= medDrawingSize) {
				//drawing just a point
				g.drawLine(offsetX, offsetY, offsetX, offsetY);
				continue;
			}
			
			int circumference = radius + radius;
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
				blobToPointer.getMagnitude()/(panelCentreX+panelCentreY)));
		
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
			x = scaleX(c.getPosition().getX()); 
			y = scaleY(c.getPosition().getY());
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
		//this the horizontal centre of zooming
		panelCentreX = panelWidth / 2;
		
		//draw last maxNumberOfDrawnCollisions from listOfCollisionPoints
		int i = 0;
		for (Vec c: frame.collisions) {
			int offsetX = scaleX(c.getX()) - circumference/2;
			int offsetY = scaleY(c.getY()) - circumference/2;			
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
		g.drawString("Scale [CTRL] + m. wheel): " + scale, x, y+72);
		g.drawString("Scroll: mouse wheel", x, y+84);
		g.drawString("Modify Charges: [C] & [SHIFT] + [C]", x, y+96);
		g.drawString("(De)activate mouse pointer: [middle-click] (mouse wheel click)", x, y+108);
	}

	//CAMERA#############################################
	public void setAutoZoom(boolean autoZoom) {
		this.autoZoom = autoZoom;
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
		//currentElevation -= deltaY;
		camera.up();
	}

	public void scrollDown() {
		//System.out.println("Scroll down");
		//currentElevation += deltaY;
		camera.down();
	}

	
	//ComponentListener methods
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("componentHidden");
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("componentMoved");
	}
	@Override
	public void componentResized(ComponentEvent e) {
		System.out.println("componentResized");
		
		int newPanelWidth = getWidth();
		int newPanelHeight = getHeight();
		
		panelCentreX = newPanelWidth / 2;
		panelCentreY = newPanelHeight / 2;
		
		currentElevation = currentElevation -
				(panelHeight - newPanelHeight);
		
		panelWidth = newPanelWidth;
		panelHeight = newPanelHeight;
		
//		camera.rescaleToWidth(newPanelWidth);
//		camera.adjustElevation(newPanelHeight);
		camera.stageResized(newPanelWidth, newPanelHeight);
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("componentShown");
	}
	
	
	public void updatePointerPosition (int x, int y) {
		pointerPosition.setXY(x, y);
	}

	public FrameData getFrame () {
		return frame;
	}
	//called from View controller
	public void initFinished() {		
		panelHeight = currentElevation = getHeight();
		componentResized(null);
		this.addComponentListener(this);
	}

}
