package data;

import java.awt.Color;

public class Colour {
	private Color c;
	
	//red, green, blue, alpha (transparency)
	private double r, g, b, a;

	public Colour () {
		this.c = new Color(rndInt(0, 256),rndInt(0, 256),rndInt(0, 256), rndInt(100, 256));
	}
	
	
	private int rndInt(int min, int max) {
		int range = max - min;
		return (int) (Math.random() * range) + min;
	}
	
	
	public Color getColor() {
		return c;
	}

	public void setColor(Color c) {
		this.c = c;
	}
}
