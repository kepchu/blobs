package data;

import java.io.Serializable;

import collisions.Collidable;
import data.Colour.ColourCategory;

public class Blob implements Collidable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private static int staticID;
	public final int ID;
	private Vec position;
	private Vec previousPosition;
	private Vec velocity;//affected by gravity and all the bouncing
	private Vec redirection;//affected by mods, added to velocity every frame
	private double energy;
	private double radius;
	private double previousRadius;
	//private double radiusDisplay;//Not in use now
	private Colour colour;
	private BlobType type;
	private BlobPhase phase;
	private ColliderType colliderType;
	private boolean colDetDone;
	// Settings:
	private final double newbornInflationSpeed;// = 15.0;
	private final double maxVelocity = 50;
	private final double maxVelocityPOW = maxVelocity*maxVelocity;
	private final double maxRedirection = 5;//MAx gravity, but settings in the slider values... TODO!
	private final double maxRedirectionPOW = maxRedirection * maxRedirection;
	private final double maxInflationSpeed = 5.0;
	private final int startingRadius = 1;
	private final double bounceDampeningFactor = 0.9;
	private final double stageFriction = 1;
	
	//things adjusted on per-frame basis by timeInterval received in update() call:
	//velocity -> position, inflationSpeed & newbornInflationSpeed

	//cloning constructor
	public Blob(Blob b) {
		ID = b.ID;
		this.position = new Vec(b.position);
		this.previousPosition = new Vec(b.previousPosition);
		this.velocity = b.velocity;
		this.redirection = b.redirection;
		this.energy = b.energy;
		this.radius =b.radius;
		this.previousRadius= b.previousRadius;
		//this.radiusDisplay = b.radiusDisplay;
		
		this.colour = new Colour(b.getColour());
		this.type = b.type;	
		this.phase = b.phase;
		this.colliderType = b.colliderType;	
		this.colDetDone = b.colDetDone;
		
		this.newbornInflationSpeed = b.newbornInflationSpeed;
	}
	
	public Blob(Vec position, Vec velocity, int energy, ColliderType colliderType) {
		ID = ++staticID;
		this.position = position;
		this.previousPosition = new Vec (position);
		this.velocity = velocity;
		this.redirection = new Vec(0,0);
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
		//reset
		setColDetDone(false);
			
		//cap sum of redirection forces for this frame and add to velocity
		if (redirection.getMagnitudePOW() > maxRedirectionPOW) {		
			redirection.setMagnitude(maxRedirection);				
			System.out.println("red. capped at " + maxRedirection);
		}
		//System.out.println("red. f. " + redirection.getMagnitude());
		velocity.addAndSet(redirection);
		redirection.setXY(0,0);
		
		// cap speed 
		if (velocity.getMagnitudePOW() > maxVelocityPOW) {		
			velocity.setMagnitude(maxVelocity);
		}
		
		//update position
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
		if (radius == previousRadius) return;//nothing to do
		
		//animate radius (== actually change its length)
		if (Math.abs(radius - energy) > maxInflationSpeed) {//TODO: spaghetti code
			if (energy > radius) {
				setRadius (radius + Math.min(maxInflationSpeed * timeIntrval, maxInflationSpeed));
			} else if (energy < radius){
				setRadius (radius - Math.min(maxInflationSpeed * timeIntrval, maxInflationSpeed));
			}
		}
		else {
			setRadius(energy);
		}
	}

	// LIFECYCLE END

	// Getters & setters:

	public int getID() {
		return ID;
	}
	public double getMass() {
		return Math.pow(energy, 1.5);
	}
	
	public Vec getVelocity() {
		return velocity;
	}

	public void setVelocity(Vec velocity) {
		this.velocity = velocity;
	}

	public Vec getRedirection() {
		return redirection;
	}

	public void setRedirection(Vec redirection) {
		this.redirection = redirection;
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
	
	public void setRadius(double radius) {
		previousRadius = this.radius;
		this.radius = radius;
//		this.radiusDisplay = radius;
	}
	
//	public double getDisplayRadius() {
//		return this.radiusDisplay;//radius * velocity.getMagnitude();
//	}
//	
//	public void setDisplayRadius(double radiusDisplay) {
//		this.radiusDisplay = radiusDisplay;
//	}
	
	public double getBounceDampeningFactor() {
		return bounceDampeningFactor;
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
		return ID + ", " + colour.getCategory() + "vel: " + velocity.getMagnitude() +
				"phase: " + phase + "; type: " + type + "prev. pos: " + previousPosition;// inf: " + inflationDelta();
	}

	@Override
	public ColliderType getColliderType() {
		return this.colliderType;
	}

	//if internal counter reaches 0 keep it at 0. Otherwise decrement by 1 if argument is negative
	//or increment by 1 if the argument is positive
}
