package data;

import collisions.Collidable;
import data.Colour.ColourCategory;

public class Blob implements Collidable {
	private static int staticID;
	public final int ID;
	private Vec position;
	private Vec previousPosition;
	private Vec velocity;
	private double energy;
	private double radius = 0;// a value separate from energy for flexibility
	private double previousRadius = 0;
	private double mass = 1;// for the future
	private Colour colour;
	private BlobType type;
	private BlobPhase phase;
	private ColliderType colliderType;
	private boolean colDetDone;
	// Settings:
	private double maxVelocity = 50;
	private double maxInflationSpeed = 5.0;
	private double newbornInflationSpeed;// = 15.0;
	private int startingRadius = 1;
	private double bounceDampeningFactor = 0.9;
	private double stageFriction = 1;
	
	//things adjusted on per-frame basis by timeInterval received in update() call:
	//velocity -> position, inflationSpeed & newbornInflationSpeed

	//cloning constructor
	public Blob(Blob b) {
		ID = b.ID;
		this.position = new Vec(b.position);
		this.previousPosition = new Vec(b.previousPosition);
		this.velocity = b.velocity;
		this.energy = b.energy;
		this.radius =b.radius;
		this.previousRadius= b.previousRadius;
		this.mass = b.mass;
		this.colour = new Colour(b.getColour());
		this.type = b.type;	
		this.colDetDone = b.colDetDone;
		this.colliderType = b.colliderType;
		
		this.newbornInflationSpeed = b.newbornInflationSpeed;
	}
	
	public Blob(Vec position, Vec velocity, int energy, ColliderType colliderType) {
		ID = ++staticID;
		this.position = position;
		this.previousPosition = new Vec (position);
		this.velocity = velocity;
		this.energy = energy;
		this.colour = new Colour();
		// settings
		setRadius(startingRadius);
		this.colliderType = colliderType;
		this.phase = BlobPhase.NEWBORN_PRE_APEX;
		newbornInflationSpeed = Math.max(energy / 10, 15);
	}
	
	public Blob (Vec position, int energy, ColliderType colliderType) {
		this(position, new Vec(0, 0), energy, colliderType);
	}
	public Blob(Vec position, Vec velocity, int energy) {
		this(position, velocity, energy, ColliderType.STANDARD);
	}
	public Blob (Vec position, int energy) {
		this(position, energy, ColliderType.STANDARD);
	}
	

	public boolean isColDetDone() {
		return colDetDone;
	}
	
	public void setColDetDone(boolean b) {
		colDetDone = b;
	}
	
	
	// LIFECYCLE START
	// TODO: regulate things from the enum according to state change? - separate FSM?
		public enum BlobType {
			BOUNDED, STANDARD, DEBRIS, PLAYER_CONTROLLED;
		}
		
		public enum BlobPhase {
			 NEWBORN_PRE_APEX, NEWBORN_POST_APEX, ADULT, DEAD
		}
	
	public void update(double timeInterval, Vec drag) {
		
		velocity.addAndSet(drag.multiply(timeInterval));

		switch (phase) {
		case NEWBORN_PRE_APEX:
		case NEWBORN_POST_APEX:
			updateNEWBORN(timeInterval);
			break;
		default:
			updateOUT(timeInterval);
			break;
		}

		updateAllStates(timeInterval);
	}

	//@SuppressWarnings("unused")
	private void updateAllStates(double timeInterval) {
		//reset collision state
		setColDetDone(false);
		
		// cap speed and update position
		if (velocity.getMagnitude() > maxVelocity) {
			//System.out.println(this + " MAXED");
			velocity.setMagnitude(maxVelocity);
		}
		
		//TIME INTERVAL APPLIED TODO: change position via mutator method that would update previousPosition...
		previousPosition.setXY(position);
		position.addAndSet(velocity.multiply(stageFriction * timeInterval));		
	}

	private void updateNEWBORN(double timeInterval) {	
		//settings
		int inflationApex = (int) (getEnergy() * 1.2);//amount of "overblowing" during initial growth
		double shrinkingSpeedModifier = 2.5;//speed of shrinking back to proper size after initial "overblow"
				
		//update radius
		if (phase == BlobPhase.NEWBORN_PRE_APEX) {
			setRadius(radius + (newbornInflationSpeed * timeInterval));
			if (radius > inflationApex) {
				phase = BlobPhase.NEWBORN_POST_APEX;
			}	
		}
		// if inflationApex has been reached, deflate until below nominal size and then
		// change state to OUT
		if (phase == BlobPhase.NEWBORN_POST_APEX) {
			setRadius(radius - (newbornInflationSpeed * timeInterval)/shrinkingSpeedModifier);
			if (radius < energy) {
				phase = BlobPhase.ADULT;
			}
		}
	}

	private void updateOUT(double timeIntrval) {
		// update radius	
		if (Math.abs(radius - energy) > maxInflationSpeed) {
			if (energy > radius) {
				setRadius (radius + Math.min(maxInflationSpeed * timeIntrval, maxInflationSpeed));
			} else if (energy < radius){
				setRadius (radius - Math.min(maxInflationSpeed * timeIntrval, maxInflationSpeed));
			}
		} else {
			setRadius(energy);
		}
	}

	// LIFECYCLE END

	// Getters & setters:

	public int getID() {
		return ID;
	}
	public double getMass() {
		return energy;
	}
	
	public Vec getVelocity() {
		return new Vec(velocity);
	}

	public void setVelocity(Vec velocity) {
		this.velocity = velocity.multiply(1.0/energy);
	}

	public Vec getPreviousPosition() {
		return previousPosition;
	}
	
	public Vec getPosition() {
		return position;
	}

	public void setPosition(Vec position) {
		this.position = position;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public ColourCategory getColourCategory() {
		return getColour().getCategory();
	}
	
	public Colour getColour() {
		return colour;
	}

	public double getColourComponent(ColourCategory component) {
		return this.colour.getComponent(component);
	}
	
	public void setColour(Colour colour) {
		this.colour = colour;
	}

	public double getRadius() {
		return radius;
	}

	public double getBounceDampeningFactor() {
		return bounceDampeningFactor;
	}

	public void setRadius(double radius) {
		previousRadius = this.radius;
		this.radius = radius;
	}

	public BlobType getType() {
		return type;
	}

	public void setType(BlobType type) {
		this.type = type;
	}

	public double getX() {
		return position.getX();
	}

	public void setX(double x) {
		position.setX(x);
	}

	public double getY() {
		return position.getY();
	}

	public void setY(double y) {
		position.setY(y);
	}

	public double inflationDelta() {
		return (radius - previousRadius);
	}
	
	public String toString() {
//		return "Blob # " + ID + ", y: " + (int)getY() + ", x: " + (int)getX() +
//				", energy: " + energy + ", Y + radius = " + (getY() + radius);
		return ID + ", " + colour.getCategory() + " inf: " + inflationDelta();
	}

	@Override
	public ColliderType getColliderType() {
		return this.colliderType;
	}

}
