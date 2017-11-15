package data;

import data.Colour.ColourCategory;
import utils.VecMath;
//attraction/repulsion point
public class ChargePoint {
	
	private Vec position;
	private double power;
	private ColourCategory colourCategory;
	
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
	}
	public ChargePoint(Vec position, double power) {
		this(position, power, ColourCategory.R);
	}
	public ChargePoint(Vec position) {
		this(position, 1.0, ColourCategory.R);
	}
	
	public void setChargeType (ColourCategory colourCategory) {
		this.colourCategory = colourCategory;
	}
	
	public ColourCategory getChargeType () {
		return this.colourCategory;
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

	public void charge(Blob b, double timeInterval) {
		if (this.colourCategory == ColourCategory.NEUTRAL) {
			modify(b);//or do nothing...
			return;
		} else if (b.getColourCategory() == this.colourCategory) {
			modify(b);
		}
	}
	
	private void modify(Blob b) {
		Vec chargeInfluence = VecMath.vecFromAtoB(b.getPosition(), this.position);
		chargeInfluence.setMagnitude(this.power);
		b.getVelocity().addAndSet(chargeInfluence);		
	}
	
}
