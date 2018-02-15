package data;

import java.awt.Color;
import java.io.Serializable;
import utils.U;

public class Colour implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum ColourCategory {
		R(new Color(230,50,50,200)),
		G(new Color(50,230,50,200)),
		B(new Color(50,50,230,200)),
		NEUTRAL(new Color(80,80,80,234));
		
		private final Color defaultCategoryColor;
		
		private ColourCategory (Color c) {
			defaultCategoryColor = c;
		}
		
		public Color getDefaultCategoryColor () {
			return defaultCategoryColor;
		}
		
	}
	
	private Colour kolor;
	private Color c;
	private ColourCategory cat;
	//red, green, blue, alpha (transparency)
	private double r, g, b, a;

	public Colour () {
		cat = ColourCategory.NEUTRAL;
		setColor (new Color(U.rndInt(0, 256),U.rndInt(0, 256),U.rndInt(0, 256), U.rndInt(100, 256)));
		kolor = new Colour(false, this);
	}
	
	//cloning constructor
	public Colour (Colour c) {
		this(false, c);
		this.kolor = new Colour(false, c.getKolor());
	}
	
	//Private int constructor for Colour initialised as field of Colour - to prevent infinite initialisation regress
	private Colour (boolean b, Colour c) {
		this.c = new Color(c.c.getRed(), c.c.getGreen(), c.c.getBlue(),c.c.getAlpha());
		this.cat = c.cat;
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
		this.a = c.a;
	}
	
	private void validate() {		
		if (r > Math.max(g, b)) {
			cat = ColourCategory.R;
		} else if (g > b) {
			cat = ColourCategory.G;
		} else if (b > g){//no change of category if b == g
			cat = ColourCategory.B;
		}
		
		this.c = new Color((int)r,(int)g,(int)b, (int)a);
	}
	
	public void setColor(Color c) {
		//this.c = c;
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		a = c.getAlpha();
		validate();
	}
	
	public void setComponent(ColourCategory component, double value) {
		
		switch (component) {
		case R:
			r = U.wrapRGB(value);
			break;
		case G:
			g = U.wrapRGB(value);
			break;
		case B:
			b = U.wrapRGB(value);
			break;
		case NEUTRAL:
			a = U.wrapRGB(value);
			break;
		default:
			throw new IllegalArgumentException("Unknown colour category: " + component);
		}
		validate();
	}
	
	//multiply a component by value
	public void multiplyComponentBy(ColourCategory component, double value) {
		switch (component) {
		case R:
			r = U.wrapRGB(r*value);
			break;
		case G:
			g = U.wrapRGB(g*value);
			break;
		case B:
			b = U.wrapRGB(b*value);
			break;
		case NEUTRAL:
			a = U.wrapRGB(a*value);
			break;
		default:
			throw new IllegalArgumentException("Unknown colour category: " + component);
		}
		validate();
	}
	
	//multiplies components except alpha
	public void multiplyBy(double value) {
		System.out.println("colour multiply by " + value);
		r = U.wrapRGB(r * value);
		g = U.wrapRGB(g * value);
		b = U.wrapRGB(b * value);
		//a = U.wrapRGB(a * value);
		
		validate();
	}
	
	//getters
	public ColourCategory getCategory() {
		return cat;
	}
	
	public Color getColor() {
		return c;
	}
	
	public Colour getKolor() {
		return this.kolor;
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
