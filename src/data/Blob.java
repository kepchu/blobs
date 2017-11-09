package data;

import app.Collidable;

public class Blob implements Collidable {
	private static int staticID;
	private final int ID;
	private Vec velocity;
	private Vec position;
	private Vec previousPosition;
	private double maxVelocity;
	private double energy;
	private Colour colour;
	private double radius = 0;// a value separate from energy for flexibility
	private double previousRadius = 0;
	private double mass = 1;// for the future
	BlobState state;

	// Settings:
	private double maxInflationSpeed = 5.0;
	private double newbornInflationSpeed;// = 15.0;
	private int startingRadius = 1;
	private double bounceDampeningFactor = 1;
	private double stageFriction = 0.005;
	//things adjusted on per-frame basis by timeInterval received in update() call:
	//velocity -> position, inflationSpeed & newbornInflationSpeed

	public Blob(Vec position, Vec velocity, int energy) {
		staticID++;
		ID = staticID;
		// random part
		this.position = position;
		this.previousPosition = new Vec (position);
		this.energy = energy;
		this.colour = new Colour();
		// standard set-up
		setRadius(startingRadius);
		newbornInflationSpeed = Math.max(energy / 12, 15);
		this.velocity = velocity;
		this.maxVelocity = 30;
		this.state = BlobState.NEWBORN_PRE_APEX;
	}
	public Blob (Vec position, int energy) {
		this(position, new Vec(0, 0), energy);
	}
	
	
	// LIFECYCLE START
	public void update(double timeInterval, Vec drag, Vec stageDelta) {

		velocity.addAndSet(drag.multiply(timeInterval));

		switch (state) {
		case NEWBORN_PRE_APEX:
		case NEWBORN_POST_APEX:
			updateNEWBORN(timeInterval);
			break;
		default:
			updateOUT(timeInterval);
			break;
		}

		updateAllStates(timeInterval, stageDelta);
	}

	@SuppressWarnings("unused")
	private void updateAllStates(double timeInterval, Vec stageDelta) {
		
		
		//account for window movement
		//TIME INTERVAL not applied as real window movement should be matched
		if (false) {// (stageDelta.getMagnitude() > 0) {
			System.out.println("stageDelta in updateAllStates: " + stageDelta);		
			position.addAndSet(stageDelta.multiply(-1));
			velocity.addAndSet(stageDelta.multiply(stageFriction));
		}

		// cap speed and update position
		if (velocity.getMagnitude() > maxVelocity) {
			//System.out.println(this + " MAXED");
			velocity.setMagnitude(maxVelocity);
		}
		
		//TIME INTERVAL APPLIED TODO: change position via mutator method that would update previousPosition...
		previousPosition.setXY(position);
		position.addAndSet(velocity.multiply(timeInterval));
		
		
		//bounce off of stage edges:
		//bottom
		if (getY() + radius >= DataController.maxY) {//below bottom edge
			setY(DataController.maxY - radius);	//align with bottom edge
			velocity.multiplyAndSet(1, -bounceDampeningFactor);//reverse vertical velocity
			velocity.addAndSet(stageDelta.getX()*stageFriction, stageDelta.getY());
		}
		//left
		if (getX() - radius < DataController.minX) {
			setX(DataController.minX + radius);
			velocity.multiplyAndSet(-bounceDampeningFactor, 1);
		}
		//right
		if (getX() + radius > DataController.maxX) {
			setX(DataController.maxX - radius);
			velocity.multiplyAndSet(-bounceDampeningFactor, 1);
		}
		
	}

	// METHODS:
	private void updateNEWBORN(double timeInterval) {	
		//settings
		int inflationApex = (int) (getEnergy() * 1.2);//amount of "overblowing" during initial growth
		double shrinkingSpeedModifier = 2.5;//speed of shrinking back to proper size after initial "overblow"
				
		//update radius
		if (state == BlobState.NEWBORN_PRE_APEX) {
			radius += (newbornInflationSpeed * timeInterval);
			if (radius > inflationApex) {
				state = BlobState.NEWBORN_POST_APEX;
			}	
		}
		// if inflationApex has been reached, deflate until below nominal size and then
		// change state to OUT
		if (state == BlobState.NEWBORN_POST_APEX) {
			radius -= (newbornInflationSpeed * timeInterval)/shrinkingSpeedModifier;
			if (radius < energy) {
				state = BlobState.OUT;
			}
		}
	}

	private void updateOUT(double timeIntrval) {
		// update radius	
		if (Math.abs(radius - energy) > maxInflationSpeed) {
			if (energy > radius) {
				radius += Math.min(maxInflationSpeed * timeIntrval, maxInflationSpeed);
			} else if (energy < radius){
				radius -= Math.min(maxInflationSpeed * timeIntrval, maxInflationSpeed);
			}
		} else {
			radius = energy;
		}
	}

	// LIFECYCLE END

	// TODO: regulate things from the enum according to state change? - separate FSM
	public enum BlobState {
		IN, OUT, NEWBORN_PRE_APEX, NEWBORN_POST_APEX, PLAYER_CONTROLLED;
	}

	
	// Getters & setters:
	public Vec getVelocity() {
		return velocity;
	}

	public void setVelocity(Vec velocity) {
		this.velocity = velocity;
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

	public Colour getColour() {
		return colour;
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
	}

	public BlobState getType() {
		return state;
	}

	public void setType(BlobState type) {
		this.state = type;
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
		return "Blob # " + ID + ", y: " + (int)getY() + ", x: " + (int)getX() +
				", energy: " + energy + ", Y + radius = " + (getY() + radius);
	}
}
