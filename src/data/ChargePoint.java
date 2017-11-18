package data;

import data.Colour.ColourCategory;
import utils.VecMath;
//attraction/repulsion point
public class ChargePoint {
	enum Charger {
		COLOUR_CATEGORY {
			public void charge(Blob b, ChargePoint c) {
				if (c.colourCategory == ColourCategory.NEUTRAL) {
					Vec chargeInfluence = VecMath.vecFromAtoB(b.getPosition(), c.position);
					chargeInfluence.setMagnitude(c.power * World.getTimeInterval());
					//chargeInfluence.setMagnitude(this.power).multiplyAndSet(World.getTimeInterval());
					b.getVelocity().addAndSet(chargeInfluence);
					
					return;
				}
					
					
				if (c.colourCategory == b.getColourCategory()) {
					Vec chargeInfluence = VecMath.vecFromAtoB(b.getPosition(), c.position);
					chargeInfluence.setMagnitude(c.power * World.getTimeInterval());
					//chargeInfluence.setMagnitude(this.power).multiplyAndSet(World.getTimeInterval());
					b.getVelocity().addAndSet(chargeInfluence);
				}
			}
		},
		COLOUR_COMPONENT {
			public void charge(Blob b, ChargePoint c) {
				Vec chargeInfluence = VecMath.vecFromAtoB(b.getPosition(), c.position);
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
		};
		
		public void charge(Blob b, ChargePoint c) {
			System.out.println("Charger: root charge method");
		}
		
	}
	
	private Vec position;
	private double power;
	private ColourCategory colourCategory;
	private Charger charger;
	//cloning constructor
	public ChargePoint(ChargePoint c) {
		this.position = c.position;
		this.power = c.power;
		
		this.colourCategory = c.colourCategory;
		
	}
	public ChargePoint(Vec position, double power, ColourCategory colourCategory) {
		super();
		this.position = position;
		this.power = power;
		this.colourCategory = colourCategory;
		this.charger = Charger.COLOUR_CATEGORY;
	}
	public ChargePoint(Vec position, double power) {
		this(position, power, ColourCategory.R);
	}
	public ChargePoint(Vec position) {
		this(position, 1.0, ColourCategory.R);
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
	
	private void modify(Blob b) {
		Vec chargeInfluence = VecMath.vecFromAtoB(b.getPosition(), this.position);
		chargeInfluence.setMagnitude(this.power * World.getTimeInterval());
		//chargeInfluence.setMagnitude(this.power).multiplyAndSet(World.getTimeInterval());
		b.getVelocity().addAndSet(chargeInfluence);		
	}
	
}
