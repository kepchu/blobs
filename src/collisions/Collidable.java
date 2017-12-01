package collisions;

import data.Vec;

public interface Collidable {

	Vec getPosition();
	void setPosition(Vec v);
	double getRadius();

	double inflationDelta();
	Vec getVelocity();
	
	double getEnergy();

	Vec getPreviousPosition();
	
	boolean isColDetDone();
	void setColDetDone(boolean b);
	
	enum ColliderType {
		//standard = impenetrable;  decoration - no collisions;
		//debris = overlapping blobs that move away from each other are allowed to intersect without colliding 
		STANDARD, DEBRIS, INWARD, BOUNDED, DECORATION
	}
	
	ColliderType getColliderType ();
	
	double getBounceDampeningFactor();
	
}
