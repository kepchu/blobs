package data;

import java.io.Serializable;

import data.Colour.ColourCategory;

//attraction/repulsion point
public class Mod implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Charger {
		
		
		VELOCITY_C_CATEGORY_ABSOLUTE {
			
			void affect(Mod c, Blob b) {
				Vec toTarget = Vec.vecFromAtoB(c.position, b.getPosition());
				b.getRedirection().subAndSet(toTarget.setMagnitude(c.power * World.getTimeInterval()));
			}
			
			public String toString() {
				return "Magnet A";
			}
			
		},
		VELOCITY_C_COMPONENT {
			
			boolean areInteracting(Mod c, Blob b) {
				//this type interact regardless colour category so areInteracting
				//is overridden to test only the distance			
				if (c.rangePOW < 0) {//negative range means infinite range, no computation needed
					//System.out.println("returning negative range");
					return true;
				}
				if (c.rangePOW > Vec.distanceBetweenPOW(c.getPosition(), b.getPosition()))
					return true;
				else
					return false;
	
			}
			
			public void affect(Mod c, Blob b) {
				Vec chargeInfluence = Vec.vecFromAtoB(b.getPosition(), c.position);
				chargeInfluence.setMagnitude(c.power * World.getTimeInterval());

				switch (c.colourCategory) {
				case R:
					b.getRedirection().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.R)/255));
					break;
				case B:
					b.getRedirection().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.B)/255));
					break;
				case G:
					b.getRedirection().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.G)/255));
					break;
				case NEUTRAL:
					b.getRedirection().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.NEUTRAL)/255));
					break;
				default:
					throw new IllegalArgumentException("Unknown colour category :P");
					//break;
				}
				
			}
			public String toString() {
				return "Magnet B";
			}
		},
		
		VELOCITY_C_COMPONENT_WEIGHTED {
			
			void affect(Mod c, Blob b) {

				Vec toTarget = Vec.vecFromAtoB(c.position, b.getPosition());
				toTarget.setMagnitude(c.power * World.getTimeInterval());
					
					
					b.getRedirection().subAndSet(toTarget.multiply(
							b.getColourComponent(c.colourCategory)/255));
				
			}
			
			public String toString() {
				return "Magnet C";
			}
		},
		
		REPULSE_ALL {
			
			public String toString() {
				return "Repulse";
			}
			@Override
			public double getDefaultRangePOW() {
				return 1024*1024;
			}
		},
		VELOCITY_RING {
			@Override	
			void affect(Mod c, Blob b) {
				double radius = 3000;
				double radiusPOW = 9000000;
				
				Vec toTarget = Vec.vecFromAtoB(c.position, b.getPosition());		
				double magnitudePOW = toTarget.getMagnitudePOW();
				
				toTarget.setMagnitude(c.power * World.getTimeInterval());
				//attract if further than radius, repeal when closer
				if (magnitudePOW < radiusPOW) {													
					b.getRedirection().addAndSet(toTarget);
				} else {
					b.getRedirection().subAndSet(toTarget);
				}
			}			
		}
//		,
//		LENS {
//			@Override
//			boolean areInteracting(Mod c, Blob b) {
//				//if (c.rangePOW > Vec.distanceBetweenPOW(c.getPosition(), b.getPosition()))
//				if (2000 > Vec.distanceBetween(c.getPosition(), b.getPosition()))
//					return true;
//				else
//					return false;
//				
//			}
//			
//			@Override
//		
//			void affect(Mod c, Blob b) {
//				System.out.println(b.ID);
//				Vec toTarget = Vec.vecFromAtoB(c.position, b.getPosition());
//				
//				b.setDisplayRadius(b.getRadius() * 1.5);
//			}
//			
//		}
		;
		
		//entry point. Methods called inside are overridden to implement different behaviours
		void go(Mod c, Blob b) {
			if ((areInteracting(c,b))) {
				affect(c,b);
			}
		}
		
		boolean areInteracting(Mod c, Blob b) {
			
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
		
		void affect(Mod c, Blob b) {
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
	private Charger charger;
	
	//cloning constructor
	public Mod(Mod c) {
		this.position = c.position;
		this.power = c.power;
		this.rangePOW = c.rangePOW;
		rangePOW = rangePOW * rangePOW;
		this.ID = c.ID;
		this.colourCategory = c.colourCategory;
		this.charger = c.charger;
	}
	
	public Mod(Vec position, double power, double rangePOW, ColourCategory colourCategory, Charger ch) {
		this.ID = ++staticID;
		this.position = position;
		this.power = power;
		this.rangePOW = rangePOW;	
		this.colourCategory = colourCategory;
		this.charger = ch;
	}
	//default range constructor //pointer = new ChargePoint(new Vec(0,0), 1.0, ColourCategory.NEUTRAL, Charger.REPULSE_ALL);
	public Mod(Vec position, double power, ColourCategory colourCategory, Charger ch) {
		this(position, power, ch.getDefaultRangePOW(), colourCategory, ch);	
	}
	
	public Mod(Vec position, ColourCategory colourCategory, Charger ch) {
		this(position, ch.getDefaultPower(), ch.getDefaultRangePOW(), colourCategory, ch);	
	}
	
	//GETTERS / SETTERS
	public void setColourCategory (ColourCategory colourCategory) {
		this.colourCategory = colourCategory;
	}
	
	public ColourCategory getColourCategory () {
		return this.colourCategory;
	}
	
	public void setType (Charger charger) {
		this.charger = charger;
		this.power = charger.getDefaultPower();
		rangePOW = charger.getDefaultRangePOW();
	}
	
	public Charger getType () {
		return this.charger;
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
		charger.go(this, b);
	}
	
	public String toString() {
		return ID +"" + colourCategory + ", " + charger;
	}
}
