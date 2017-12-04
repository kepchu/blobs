package collisions;

import data.Vec;

public interface Collidable {

	Vec getPosition();
	Vec getPreviousPosition();
	void setPosition(Vec v);
	Vec getVelocity();
	void setVelocity(Vec v);
	double getRadius();
	double inflationDelta();	
	double getEnergy();

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
