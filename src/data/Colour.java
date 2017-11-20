package data;

import java.awt.Color;

public class Colour {
	
	public enum ColourCategory {
		R(new Color(230,50,50,200)),
		G(new Color(50,230,50,200)),
		B(new Color(50,50,230,200)),
		NEUTRAL(new Color(180,180,180,150));
		
		private final Color defaultCategoryColor;
		
		private ColourCategory (Color c) {
			defaultCategoryColor = c;
		}
		
		public Color getDefaultCategoryColor () {
			return defaultCategoryColor;
		}
		
	}
	
	private Color c;
	private ColourCategory cat;
	//red, green, blue, alpha (transparency)
	private double r, g, b, a;

	public Colour () {
		cat = ColourCategory.NEUTRAL;
		setColor (new Color(rndInt(0, 256),rndInt(0, 256),rndInt(0, 256), rndInt(100, 256)));
	}
	
	//cloning constructor
	public Colour (Colour c) {
		this.c = new Color(c.c.getRed(), c.c.getGreen(), c.c.getBlue(),c.c.getAlpha());
		this.cat = c.cat;
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
		this.a = c.a;
	}

	private int rndInt(int min, int max) {
		int range = max - min;
		return (int) (Math.random() * range) + min;
	}
	
	
	

	private void updateColourCategory() {		
		if (r > Math.max(g, b)) {
			cat = ColourCategory.R;
		} else if (g > b) {
			cat = ColourCategory.G;
		} else if (b > g){//no change of category if b == g
			cat = ColourCategory.B;
		}
	}
	
	public void setColor(Color c) {
		this.c = c;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		a = c.getAlpha();
		updateColourCategory();
	}
	
	public Color getColor() {
		return c;
	}
	
	public ColourCategory getCategory() {
		return cat;
	}

	
	
	public double getComponent(ColourCategory component) {
		switch(component) {
		case R:	return r;
		case G: return g;
		case B: return b;
		case NEUTRAL: return a;
		default:
			throw new IllegalArgumentException("Unknown colour category: " + component);
		}
	}
}
