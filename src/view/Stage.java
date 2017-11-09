package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import data.Blob;
import data.Vec;
import data.World;
import data.ChargePoint;
import data.DisplayBuffer;
import data.BufferData;

@SuppressWarnings("serial")
public class Stage extends JPanel implements ComponentListener{

	private List<Blob> blobs;
	private List <Vec> collisions;
	private List<ChargePoint> charges;
	private int ground;//actual ground level as received from Model
	private int panelHeight;
	private int panelWidth;

	private int panelCentreX;
	private int panelCentreY;
	private double scale = 1;// "scale" is a zoom factor
	private int currentElevation = 0;//ground level used for drawing, adjusted when scrolling the scene

	private double deltaZoom = 1.5;
	private int deltaY = 100;
	
	public Stage(int ground, int initialCameraPosition) {
		this.ground = ground;
		currentElevation = ground + initialCameraPosition;
		//IMPORTANT: the below is crucial to get proper dimensions & ground level (in "componentResized").
		//This functionality could be done by a simple getHeight() call from paint(Graphics g)
		this.addComponentListener(this);		
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
	
	// DRAWING ROUTINE
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		// calls to methods that draw specific elements
		drawBackground(g2d);
		drawRuler(g2d);
		drawBorders(g2d);
		drawBlobs(g2d);
		drawCharges(g2d);
		drawCollisions(g2d);
	}

	// methods used by the drawing routine in paint (Graphics g)
	private void drawBackground(Graphics2D g) {
		g.setColor(new Color(227, 227, 226, 255));
		g.fillRect(0, 0, panelWidth, panelHeight);
		g.setColor(new Color(243, 243, 243, 255));
		g.fillOval(20, 20, panelWidth - 40, panelHeight - 40);
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
		g.setColor(Color.RED);
		//draw ground level
		g.drawLine(0, currentElevation, panelWidth, currentElevation);
		//TODO: draw "word dimensions" - minX, maxY, etc. from DataController
	}
	
	private void drawBlobs(Graphics2D g2d) {
		
		for (Blob b : blobs) {
			double radius = b.getRadius();
			
			//Subtracting radius to accommodate to drawOval method
			//(where position denotes top left corner rather than centre)
			int offsetX = scaleX(b.getPosition().getX() - radius);
			int offsetY = scaleY(b.getPosition().getY() - radius);

			int circumference = (int) (radius * 2 * scale);

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
			x = scaleX(c.getPosition().getX() - width/2); 
			y = scaleY(c.getPosition().getY() - height/2);
			g.fillRect(x, y, (int)(width * scale),(int)(height * scale));
		}
	}
	
	private void drawCollisions(Graphics2D g2d) {
		g2d.setColor(new Color(0,255,255,100));
		int circumference = 6;
		int maxNumberOfDrawnCollisions = 300;
		//this the horizontal centre of zooming
		panelCentreX = panelWidth / 2;
		
		//draw last maxNumberOfDrawnCollisions from listOfCollisionPoints
		for (int i = collisions.size() - 1;
				i >= Math.max(
						0, collisions.size() - maxNumberOfDrawnCollisions); i-- ) {
	
			int offsetX = scaleX(collisions.get(i).getX()) - circumference/2;
			int offsetY = scaleY(collisions.get(i).getY()) - circumference/2;			
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
		panelHeight = getHeight();
		panelWidth = getWidth();
		
		panelCentreX = panelWidth / 2;
		panelCentreY = panelHeight / 2;
		//currentElevation = ground + panelHeight;
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		System.out.println("componentShown");
	}


	public void setData(BufferData data) {
		this.blobs = data.getBlobs();
		this.collisions = data.getCollisions();
		this.charges = data.getCharges();
		this.ground = (int) data.getGround();	
	}

}
