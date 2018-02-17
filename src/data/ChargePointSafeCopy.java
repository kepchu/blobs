package data;

import java.io.Serializable;

import collisions.Collidable;
import data.Colour.ColourCategory;

//attraction/repulsion point
public class ChargePointSafeCopy implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Charger {
		COLOUR_CATEGORY_ABSOLUTE {
			public void charge(Blob b, ChargePointSafeCopy c) {
				if (c.colourCategory == ColourCategory.NEUTRAL) {
					Vec chargeInfluence = Vec.vecFromAtoB(b.getPosition(), c.position);
					chargeInfluence.setMagnitude(c.power * World.getTimeInterval());
					//chargeInfluence.setMagnitude(this.power).multiplyAndSet(World.getTimeInterval());
					b.getVelocity().addAndSet(chargeInfluence);
					
					return;
				}
					
					
				if (c.colourCategory == b.getColourCategory()) {
					Vec chargeInfluence = Vec.vecFromAtoB(b.getPosition(), c.position);
					chargeInfluence.setMagnitude(c.power * World.getTimeInterval());
					//chargeInfluence.setMagnitude(this.power).multiplyAndSet(World.getTimeInterval());
					b.getVelocity().addAndSet(chargeInfluence);
				}
			}
			
			public String toString() {
				return "Magnet A";
			}
			
		},
		COLOUR_COMPONENT {
			public void charge(Blob b, ChargePointSafeCopy c) {
				Vec chargeInfluence = Vec.vecFromAtoB(b.getPosition(), c.position);
				chargeInfluence.setMagnitude(c.power * World.getTimeInterval());

				switch (c.colourCategory) {
				case R:
					b.getVelocity().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.R)/255));
					break;
				case B:
					b.getVelocity().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.B)/255));
					break;
				case G:
					b.getVelocity().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(ColourCategory.G)/255));
					break;
				case NEUTRAL:
					b.getVelocity().addAndSet(chargeInfluence.multiply(
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
		COLOUR_CATEGORY_COMPONENT_WEIGHTED {
			public void charge(Blob b, ChargePointSafeCopy c) {

				if (c.colourCategory == b.getColourCategory()) {
					Vec chargeInfluence = Vec.vecFromAtoB(b.getPosition(), c.position);
					chargeInfluence.setMagnitude(c.power * World.getTimeInterval());
					
					b.getVelocity().addAndSet(chargeInfluence.multiply(
							b.getColourComponent(c.colourCategory)/255));
				}
			}
			public String toString() {
				return "Magnet C";
			}
		},
		
		REPULSE_ALL {
			public void charge(Blob b, ChargePointSafeCopy c) {
				
				double range = 1000;
				
				Vec chargeInfluence = Vec.vecFromAtoB(c.position, b.getPosition());
				double magnitude = chargeInfluence.getMagnitude();
//				if (Double.isNaN(magnitude))
//					System.out.println("Got NaN in REPULSE_ALL");
				if (magnitude < range) {
								
					chargeInfluence.setMagnitude(c.power);
					//chargeInfluence.multiply(-1 * c.power);
					b.getVelocity().addAndSet(chargeInfluence);
				}
				
			}
			public String toString() {
				return "Shove off";
			}
		},
		RING {
			public void charge(Blob b, ChargePointSafeCopy c) {
				double range = 3000;
				
				Vec chargeInfluence = Vec.vecFromAtoB(c.position, b.getPosition());
				double magnitude = chargeInfluence.getMagnitude();
				//System.out.println("magnitude: " + magnitude);
				if (magnitude < range) {
								
					chargeInfluence.setMagnitude(c.power);
					//chargeInfluence.multiply(-1 * c.power);
					b.getVelocity().addAndSet(chargeInfluence);
				} else if (magnitude > range) {
					chargeInfluence.setMagnitude(c.power);
					
					b.getVelocity().subAndSet(chargeInfluence);
				}
			}
		};
		
		
		public void charge(Blob b, ChargePointSafeCopy c) {
			System.out.println("Charger: root charge method");
		}
		
	}
	
	private static int staticID;
	public final int ID;
	private Vec position;
	private double power;
	private ColourCategory colourCategory;
	private Charger charger;
	//cloning constructor
	public ChargePointSafeCopy(ChargePointSafeCopy c) {
		this.position = c.position;
		this.power = c.power;
		this.ID = c.ID;
		this.colourCategory = c.colourCategory;
		this.charger = c.charger;
	}
	
	public ChargePointSafeCopy(Vec position, double power, ColourCategory colourCategory, Charger ch) {
		this.ID = ++staticID;
		this.position = position;
		this.power = power;
		this.colourCategory = colourCategory;
		this.charger = ch;
	}
	
	public void setColourCategory (ColourCategory colourCategory) {
		this.colourCategory = colourCategory;
	}
	
	public ColourCategory getColourCategory () {
		return this.colourCategory;
	}
	
	public void setType (Charger charger) {
		this.charger = charger;
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
		charger.charge(b, this);
	}
	
	public String toString() {
		return ID +"" + colourCategory + ", " + charger;
	}
}