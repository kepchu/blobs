package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.JPanel;

import data.Blob;
import data.Vec;
import data.ChargePoint;

@SuppressWarnings("serial")
public class Stage extends JPanel implements ComponentListener{

	private List<Blob> blobs;
	private List <Vec> listOfCollisionPoints;
	private List<ChargePoint> charges;
	int ground;//actual ground level as received from Model
	private int windowHeight;
	private int windowWidth;

	private int focusPointX;
	private double scale = 1;
	private int currentElevation = 0;//ground level used for drawing, adjusted when scrolling the scene

	double deltaZoom = 1.02;
	int deltaY = 100;

	public Stage(List<Blob> blobs, List <Vec> listOfCollisionPoints, List<ChargePoint> charges, int ground) {
		this.blobs = blobs;
		this.listOfCollisionPoints = listOfCollisionPoints;
		this.charges = charges;
		this.ground = ground;
		
		//ATTENTION: this is crucial to get proper ground level.
		//This functionality could be done by a simple getHeight() call from paint(Graphics g)
		this.addComponentListener(this);
		
	}

	
	
	
	
	// DRAWING ROUTINE
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		// System.out.println("getWidth(): " + getWidth() + " getHeight(): " +
		// getHeight());
		// ground = getHeight() - groundLevel;
		

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		// calls to methods that draw specific elements
		drawBackground(g2d);
		drawRuler(g2d);
		drawBlobs(g2d);
		drawCharges(g2d);
		drawCollisions(g2d);
	}

	// methods used by the drawing routine in paint (Graphics g)
	private void drawBackground(Graphics2D g) {
		// draw ellipse in rectangle
		g.setColor(new Color(227, 227, 226, 255));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(243, 243, 243, 255));
		g.fillOval(20, 20, getWidth() - 40, getHeight() - 40);

	}

	private void drawRuler(Graphics2D g) {
		// System.out.println("ground: " + ground);
		int spacing = 200;

		g.setColor(Color.BLACK);
		g.drawLine(0, ground - currentElevation, windowWidth, ground - currentElevation);

		g.setColor(Color.RED);
		g.drawLine(0, currentElevation, windowWidth, currentElevation);
		
		spacing = (int)(spacing * scale);
		
		for (int i = 0; i < currentElevation; i = i + spacing) {
			g.setColor(Color.BLACK);
			g.drawLine(windowWidth / 4, currentElevation - i
					, (windowWidth / 4) * 3, currentElevation - i);
			g.drawString(i + " pix", windowWidth / 4, currentElevation - i - 4);

			g.setColor(Color.GRAY);
			g.drawLine(windowWidth / 3, currentElevation - i - spacing / 2, (windowWidth / 3) * 2, currentElevation - i - spacing / 2);
		}
		
	}

	synchronized private void drawBlobs(Graphics2D g2d) {
		
		//this the horizontal centre of zooming
		int focusPointX = windowWidth / 2;
		
		for (Blob b : blobs) {
			//System.out.println("Position: " + b.getPosition());
			// "scale" is a zoom factor
			double radius = b.getRadius() * scale;
			
			double x = b.getPosition().getX() * scale - (focusPointX * (scale -1));
			double y = b.getPosition().getY() * scale;

			int circumference = (int) (radius * 2);
			int offsetX = (int) (x - radius);// move handle to blob's centre
			int offsetY = (int) (y - radius);
			// adjust for the "camera's" position
			offsetY += currentElevation;

			g2d.setColor(b.getColour().getColor());
			g2d.fillOval(offsetX, offsetY, circumference, circumference);

			g2d.setColor(Color.BLACK);
			g2d.drawOval(offsetX, offsetY, circumference, circumference);	
		}
	}
	
	private void drawCharges (Graphics2D g) {
		g.setColor(Color.BLACK);
		int width = 10, height = 10;
		int x,y;
		for (ChargePoint c : charges) {
			x = scaleX(c.getPosition().getX()) - width/2;
			y = scaleY(c.getPosition().getY()) - height/2;
			g.fillRect(x, y, width, height);
		}
	}
	
	private void drawCollisions(Graphics2D g2d) {
		//System.out.println("Stage: listOfCollisionPoints.size(): " + listOfCollisionPoints.size());
		g2d.setColor(new Color(0,255,255,100));
		int circumference = 6;
		int maxNumberOfDrawnCollisions = 300;
		//this the horizontal centre of zooming
		focusPointX = windowWidth / 2;
		
		//draw last maxNumberOfDrawnCollisions from listOfCollisionPoints
		for (int i = listOfCollisionPoints.size() - 1;
				i >= Math.max(
						0, listOfCollisionPoints.size() - maxNumberOfDrawnCollisions); i-- ) {
			
			int offsetX = (int)(listOfCollisionPoints.get(i).getX() * scale - (focusPointX * (scale -1))) - circumference/2;
			int offsetY = (int)(listOfCollisionPoints.get(i).getY()* scale) - circumference/2;
			// adjust for the "camera's" position
			offsetY += currentElevation;
			
			g2d.fillOval(offsetX, offsetY, circumference, circumference);
		}
	}

	public void zoomOut() {
		//System.out.println("zoomOut");
		scale /= deltaZoom;
	}

	public void zoomIn() {
		//System.out.println("zoomIn");
		scale *= deltaZoom;
	}

	public void scrollUp() {
		//System.out.println("Scroll up");
		currentElevation -= deltaY;
	}

	public void scrollDown() {
		//System.out.println("Scroll down");
		currentElevation += deltaY;
	}

	public double descaleX(int x) {
		return (x + (focusPointX*(scale - 1)))/scale;
		//return x / scale + (focusPointX / (scale - 1));
		//return x / (scale + ((windowWidth / 2) * (scale - 1)));
		//return x;
	}

	public double descaleY(int y) {
		return (y - currentElevation) / scale;
		//return y;
	}

	private int scaleX(double x) {
		return (int)(x * scale - (focusPointX * (scale -1)));
	}
	private int scaleY(double y) {
		return (int)(y * scale + currentElevation);
	}
	
	//ComponentListener methods
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("componentHidden");
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("componentMoved");
	}
	@Override
	public void componentResized(ComponentEvent e) {
		System.out.println("componentResized");
		windowHeight = getHeight();
		windowWidth = getWidth();
		currentElevation = ground + windowHeight;
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("componentShown");
	}

}
