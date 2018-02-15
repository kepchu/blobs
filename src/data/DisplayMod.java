package data;

import java.io.Serializable;

import data.Colour.ColourCategory;

//attraction/repulsion point
public class DisplayMod implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Distortion {
		
		LENS {
			@Override
			boolean areInteracting(DisplayMod c, Blob b) {
				//if (c.rangePOW > Vec.distanceBetweenPOW(c.getPosition(), b.getPosition()))
				if (2000 > Vec.distanceBetween(c.getPosition(), b.getPosition()))
					return true;
				else
					return false;
				
			}
			
			@Override
		
			void affect(DisplayMod c, Blob b) {
				System.out.println(b.ID);
				Vec toTarget = Vec.vecFromAtoB(c.position, b.getPosition());
				
				b.setRadius(b.getRadius() * 1.5);
			}
			
		};
		
		//entry point. Methods called inside are overridden to implement different behaviours
		void go(DisplayMod c, Blob b) {
			if ((areInteracting(c,b))) {
				affect(c,b);
			}
		}
		
		boolean areInteracting(DisplayMod c, Blob b) {
			
			//match colour
			if (c.colourCategory != ColourCategory.NEUTRAL &&
					c.colourCategory != b.getColourCategory())
			return false;
			
			//match position
			if (c.rangePOW < 0) {//negative range means infinite range, no computation needed
				//System.out.println("returning negative range");
				return true;
			}
			if (c.rangePOW > Vec.distanceBetweenPOW(c.getPosition(), b.getPosition()))
				return true;
			else
				return false;
		}
		
		void affect(DisplayMod c, Blob b) {
			Vec toTarget = Vec.vecFromAtoB(c.position, b.getPosition());
			b.getRedirection().addAndSet(toTarget.setMagnitude(c.power * World.getTimeInterval()));
		}

		// methods below used only in constructor of the wrapping object to get default values
		// not overridden returns a negative so the category has infinite range;
		protected double getDefaultRangePOW() {
			return -1.0;
		}
		
		protected double getDefaultPower() {
			return 1.02;
		}		
	}
	
	private static int staticID;
	public final int ID;
	private Vec position;
	private double power;
	private double rangePOW;
	
	private ColourCategory colourCategory;
	private Distortion distortion;
	
	//cloning constructor
	public DisplayMod(DisplayMod c) {
		this.position = c.position;
		this.power = c.power;
		this.rangePOW = c.rangePOW;
		rangePOW = rangePOW * rangePOW;
		this.ID = c.ID;
		this.colourCategory = c.colourCategory;
		this.distortion = c.distortion;
	}
	
	public DisplayMod(Vec position, double power, double rangePOW, ColourCategory colourCategory, Distortion ch) {
		this.ID = ++staticID;
		this.position = position;
		this.power = power;
		this.rangePOW = rangePOW;	
		this.colourCategory = colourCategory;
		this.distortion = ch;
	}
	//default range constructor //pointer = new ChargePoint(new Vec(0,0), 1.0, ColourCategory.NEUTRAL, Charger.REPULSE_ALL);
	public DisplayMod(Vec position, double power, ColourCategory colourCategory, Distortion ch) {
		this(position, power, ch.getDefaultRangePOW(), colourCategory, ch);	
	}
	
	public DisplayMod(Vec position, ColourCategory colourCategory, Distortion ch) {
		this(position, ch.getDefaultPower(), ch.getDefaultRangePOW(), colourCategory, ch);	
	}
	
	//GETTERS / SETTERS
	public void setColourCategory (ColourCategory colourCategory) {
		this.colourCategory = colourCategory;
	}
	
	public ColourCategory getColourCategory () {
		return this.colourCategory;
	}
	
	public void setType (Distortion charger) {
		this.distortion = charger;
		this.power = charger.getDefaultPower();
		rangePOW = charger.getDefaultRangePOW();
	}
	
	public Distortion getType () {
		return this.distortion;
	}
	
	public Vec getPosition() {
		return position;
	}
	public void setPosition (Vec position) {
		this.position = position;
	}
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
		this.power = power;
	}

	public void charge(Blob b) {
		distortion.go(this, b);
	}
	
	public String toString() {
		return ID +"" + colourCategory + ", " + distortion;
	}
}
